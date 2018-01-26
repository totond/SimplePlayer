package yanzhikai.simpleplayer.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.greenrobot.eventbus.Subscribe;

import yanzhikai.simpleplayer.R;
import yanzhikai.simpleplayer.adapter.LocalAudioListAdapter;
import yanzhikai.simpleplayer.db.AudioListDaoManager;
import yanzhikai.simpleplayer.event.AudioEvent;
import yanzhikai.simpleplayer.event.PlayListChangedEvent;
import yanzhikai.simpleplayer.model.PlayList;
import yanzhikai.simpleplayer.utils.EventUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class LocalAudioListFragment extends Fragment implements View.OnClickListener {

    private RecyclerView rv_local_list;
    private LocalAudioListAdapter mLocalAudioListAdapter;
    private TextView tv_edit,tv_delete,tv_add,tv_search,tv_choose_all;

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

    @Subscribe
    public void handle(AudioEvent audioEvent){

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

                break;
            case R.id.tv_add:
                PlayList.getInstance().add(mLocalAudioListAdapter.getSelectedItem());
                updateEditMode(false);
                mLocalAudioListAdapter.setEditMode(true);
                mLocalAudioListAdapter.clearSelected();
                EventUtil.post(new PlayListChangedEvent(PlayListChangedEvent.ITEM_ADDED));
                mLocalAudioListAdapter.notifyDataSetChanged();
                break;
            case R.id.tv_search:

                break;
            case R.id.tv_choose_all:
                mLocalAudioListAdapter.selectAllItems();
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

    private class MyLocalListItemOnClickListener implements LocalAudioListAdapter.LocalListItemOnClickListener {

        @Override
        public void onItemClick(int index) {

        }
    }
}
