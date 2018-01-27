package yanzhikai.simpleplayer.ui;


import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.RuntimePermissions;
import yanzhikai.simpleplayer.R;
import yanzhikai.simpleplayer.adapter.LocalAudioListAdapter;
import yanzhikai.simpleplayer.db.AudioListDaoManager;
import yanzhikai.simpleplayer.event.AudioEvent;
import yanzhikai.simpleplayer.event.LocalListChangedEvent;
import yanzhikai.simpleplayer.event.PlayListChangedEvent;
import yanzhikai.simpleplayer.model.AudioInfo;
import yanzhikai.simpleplayer.model.PlayList;
import yanzhikai.simpleplayer.model.StorageInfo;
import yanzhikai.simpleplayer.utils.EventUtil;
import yanzhikai.simpleplayer.utils.MediaUtil;
import yanzhikai.simpleplayer.utils.StorageListUtil;
import yanzhikai.simpleplayer.utils.ToastUtil;

/**
 * A simple {@link Fragment} subclass.
 */
@RuntimePermissions
public class LocalAudioListFragment extends Fragment implements View.OnClickListener {
    public static final String TAG = "LocalAudioListFragment";

    private RecyclerView rv_local_list;
    private LocalAudioListAdapter mLocalAudioListAdapter;
    private TextView tv_edit,tv_delete,tv_add,tv_search,tv_choose_all;
    private ProgressBar pb_search;
    private boolean mIsSearching = false;

    public LocalAudioListFragment() {
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventUtil.unregister(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        EventUtil.register(this);
        View rootView = inflater.inflate(R.layout.fragment_loacl_audio_list, container, false);
        tv_edit = rootView.findViewById(R.id.tv_edit);
        tv_delete = rootView.findViewById(R.id.tv_delete);
        tv_add = rootView.findViewById(R.id.tv_add);
        tv_search = rootView.findViewById(R.id.tv_search);
        tv_choose_all = rootView.findViewById(R.id.tv_choose_all);
        pb_search = rootView.findViewById(R.id.pb_search);

        tv_edit.setOnClickListener(this);
        tv_delete.setOnClickListener(this);
        tv_add.setOnClickListener(this);
        tv_search.setOnClickListener(this);
        tv_choose_all.setOnClickListener(this);

        rv_local_list = rootView.findViewById(R.id.rv_local_list);
        rv_local_list.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration divider = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        mLocalAudioListAdapter = new LocalAudioListAdapter(getContext(), AudioListDaoManager.getInstance().getLocalListInfo().getInfoList());
        mLocalAudioListAdapter.setOnClickListener(new MyLocalListItemOnClickListener());
        rv_local_list.setAdapter(mLocalAudioListAdapter);
        rv_local_list.addItemDecoration(divider);

        updateEditMode(true);

        return rootView;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleLocalListChanged(LocalListChangedEvent localListChangedEvent){
        switch (localListChangedEvent.getType()){
            case LocalListChangedEvent.SEARCH_FINISH:
                mIsSearching = false;
                updateSearching();
                mLocalAudioListAdapter.setAudioInfos(AudioListDaoManager.getInstance().getLocalListInfo().getRefreshList());
                mLocalAudioListAdapter.notifyDataSetChanged();
                rv_local_list.smoothScrollToPosition(mLocalAudioListAdapter.getItemCount() - 1);
                break;
            case LocalListChangedEvent.AUDIO_ADDED:
                mLocalAudioListAdapter.notifyDataSetChanged();
                rv_local_list.smoothScrollToPosition(AudioListDaoManager.getInstance().getLocalListInfo().getInfoList().size() - 1);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_edit:
                if (mLocalAudioListAdapter.getEditMode()){
                    updateEditMode(true);
                    mLocalAudioListAdapter.setEditMode(false);
//                    ToastUtil.makeShortToast(PlayListFragment.this.getContext(),"选择了"+mPlayListAdapter.getSelectedItem().size());
                }else {
                    updateEditMode(false);
                    mLocalAudioListAdapter.clearSelected();
                    mLocalAudioListAdapter.setEditMode(true);
                }
                mLocalAudioListAdapter.notifyDataSetChanged();
                break;
            case R.id.tv_delete:
                for (AudioInfo audioInfo : mLocalAudioListAdapter.getSelectedItem()){
                    AudioListDaoManager.getInstance().deleteAudioInLocalList(audioInfo);
                }
                mLocalAudioListAdapter.setAudioInfos(AudioListDaoManager.getInstance().getLocalListInfo().getRefreshList());
                mLocalAudioListAdapter.clearSelected();
                mLocalAudioListAdapter.notifyDataSetChanged();
                break;
            case R.id.tv_add:
                if (!PlayList.getInstance().add(mLocalAudioListAdapter.getSelectedItem())){
                    ToastUtil.makeShortToast(getContext(),"加入了除去列表中已经存在的歌曲");
                }
                updateEditMode(false);
                mLocalAudioListAdapter.setEditMode(true);
                mLocalAudioListAdapter.clearSelected();
                EventUtil.post(new PlayListChangedEvent(PlayListChangedEvent.ITEM_ADDED));
                mLocalAudioListAdapter.notifyDataSetChanged();
                break;
            case R.id.tv_search:
                LocalAudioListFragmentPermissionsDispatcher.searchWithPermissionCheck(this);
                break;
            case R.id.tv_choose_all:
                mLocalAudioListAdapter.selectAllItems();
                mLocalAudioListAdapter.notifyDataSetChanged();
                break;
        }
    }

    private void updateEditMode(boolean isEditing){
        if (isEditing){
            tv_delete.setVisibility(View.INVISIBLE);
            tv_add.setVisibility(View.INVISIBLE);
            tv_search.setVisibility(View.VISIBLE);
            tv_choose_all.setVisibility(View.INVISIBLE);
            tv_edit.setText(R.string.local_list_edit);
        }else {
            tv_delete.setVisibility(View.VISIBLE);
            tv_add.setVisibility(View.VISIBLE);
            tv_search.setVisibility(View.INVISIBLE);
            tv_choose_all.setVisibility(View.VISIBLE);
            tv_edit.setText(R.string.local_list_edit_completed);
        }
    }

    private void updateSearching(){
        if (mIsSearching){
            pb_search.setVisibility(View.VISIBLE);
            tv_choose_all.setVisibility(View.INVISIBLE);
            tv_edit.setVisibility(View.INVISIBLE);
            tv_search.setText(R.string.local_list_search_cancel);
        }else {
            pb_search.setVisibility(View.INVISIBLE);
            tv_search.setText(R.string.local_list_search);
            tv_edit.setVisibility(View.VISIBLE);
            updateEditMode(true);
        }
    }

    @NeedsPermission({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void search() {
        mIsSearching = !mIsSearching;
        updateSearching();
        searchAudio();
    }

    private void searchAudio(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                List<StorageInfo> list = StorageListUtil
                        .listAvaliableStorage(getActivity().getApplicationContext());
                for (int i = 0; i < list.size(); i++) {
                    StorageInfo storageInfo = list.get(i);
                    if (!mIsSearching){
                        break;
                    }
                    MediaUtil.scanLocalAudioFile(storageInfo.path, new MediaUtil.ForeachListener() {
                        @Override
                        public void foreach(AudioInfo audioInfo) {
                            if (AudioListDaoManager.getInstance().insertAudioToList(audioInfo,AudioListDaoManager.getInstance().getLocalListInfo())){
                                Log.d(TAG, "foreach: 加入");
                            }
                        }

                        @Override
                        public boolean filter(String hash) {
                            return false;
                        }
                    });
                }
                EventUtil.post(new LocalListChangedEvent(LocalListChangedEvent.SEARCH_FINISH));
            }
        }.start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        LocalAudioListFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @OnPermissionDenied({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void onDenied() {

    }

    private class MyLocalListItemOnClickListener implements LocalAudioListAdapter.LocalListItemOnClickListener {

        @Override
        public void onItemClick(int index) {

        }
    }
}
