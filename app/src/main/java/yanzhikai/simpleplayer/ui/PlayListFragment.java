package yanzhikai.simpleplayer.ui;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import yanzhikai.simpleplayer.R;
import yanzhikai.simpleplayer.adapter.BaseOnItemClickListener;
import yanzhikai.simpleplayer.adapter.PlayListAdapter;
import yanzhikai.simpleplayer.event.AudioEvent;
import yanzhikai.simpleplayer.event.PlayListChangedEvent;
import yanzhikai.simpleplayer.model.AudioInfo;
import yanzhikai.simpleplayer.model.PlayList;
import yanzhikai.simpleplayer.utils.EventUtil;
import yanzhikai.simpleplayer.utils.ToastUtil;

import static yanzhikai.simpleplayer.event.AudioEvent.AUDIO_PLAY_CHOSEN;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlayListFragment extends Fragment implements View.OnClickListener{
    private RecyclerView rv_play_list;
    private PlayListAdapter mPlayListAdapter;
    private TextView tv_edit,tv_delete,tv_choose_all;

    public PlayListFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        View rootView = inflater.inflate(R.layout.fragment_play_list, container, false);
        rv_play_list = rootView.findViewById(R.id.rv_play_list);
        rv_play_list.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration divider = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        mPlayListAdapter = new PlayListAdapter(getContext(), PlayList.getInstance().getAudioList());
        mPlayListAdapter.setOnClickListener(new MyPlayListListener());
        DefaultItemAnimator animator = new DefaultItemAnimator();
        animator.setAddDuration(2000);
        animator.setRemoveDuration(2000);
        rv_play_list.setItemAnimator(animator);
        rv_play_list.setAdapter(mPlayListAdapter);
        rv_play_list.addItemDecoration(divider);


        tv_edit = rootView.findViewById(R.id.tv_edit);
        tv_edit.setOnClickListener(this);

        tv_delete = rootView.findViewById(R.id.tv_delete);
        tv_delete.setOnClickListener(this);

        tv_choose_all = rootView.findViewById(R.id.tv_choose_all);
        tv_choose_all.setOnClickListener(this);
        handleCurrentAudioChanged();
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_edit:
                if (mPlayListAdapter.getEditMode()){
                    updateEditMode(true);
                    mPlayListAdapter.setEditMode(false);
//                    ToastUtil.makeShortToast(PlayListFragment.this.getContext(),"选择了"+mPlayListAdapter.getSelectedItem().size());
                }else {
                    updateEditMode(false);
                    mPlayListAdapter.clearSelected();
                    mPlayListAdapter.setEditMode(true);
                }

                mPlayListAdapter.notifyDataSetChanged();
                break;
            case R.id.tv_delete:
                ArrayList<AudioInfo> deleteList = mPlayListAdapter.getSelectedItem();
                boolean flag = true;
                for (AudioInfo audioInfo : deleteList) {
                    flag &= PlayList.getInstance().remove(audioInfo);
                }
                mPlayListAdapter.clearSelected();
                mPlayListAdapter.notifyDataSetChanged();
                ToastUtil.makeShortToast(PlayListFragment.this.getContext(),"删除了"+deleteList.size() + "首歌" + flag);
                break;
            case R.id.tv_choose_all:
                mPlayListAdapter.selectAllItems();
                mPlayListAdapter.notifyDataSetChanged();
                break;
        }
    }

    private void handleCurrentAudioChanged(){
        mPlayListAdapter.refreshItem();
        int index = PlayList.getInstance().getCurrentIndex();
        if (index >= 0) {
            rv_play_list.smoothScrollToPosition(PlayList.getInstance().getCurrentIndex());
        }
    }

    //对切换歌曲做出的处理，滑动RecyclerView到相应位置
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleListChanged(PlayListChangedEvent playListChangedEvent){
        switch (playListChangedEvent.getType()){
            case PlayListChangedEvent.CURRENT_AUDIO_CHANGED:
                handleCurrentAudioChanged();
                break;
            case PlayListChangedEvent.ITEM_ADDED:
                Log.d("yjk", "handleListChanged: ITEM_ADDED");
                mPlayListAdapter.notifyDataSetChanged();
                rv_play_list.smoothScrollToPosition(mPlayListAdapter.getItemCount() - 1);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateList();
    }

    private void updateList(){
//        PlayList.getInstance().add(LocalAudioDaoManager.getInstance().queryAllAudio());
//        PlayListAudioDaoManager.getInstance().insertMultiAudio(LocalAudioDaoManager.getInstance().queryAllAudio());
//        mPlayListAdapter.notifyDataSetChanged();
    }

    private void updateEditMode(boolean isEditing){
        if (isEditing){
            tv_delete.setVisibility(View.INVISIBLE);
            tv_choose_all.setVisibility(View.INVISIBLE);
            tv_edit.setText(R.string.play_list_edit);
        }else {
            tv_delete.setVisibility(View.VISIBLE);
            tv_choose_all.setVisibility(View.VISIBLE);
            tv_edit.setText(R.string.play_list_edit_completed);
        }
    }

    private class MyPlayListListener implements BaseOnItemClickListener {
        @Override
        public void onItemClick(int index) {
            AudioEvent audioEvent = new AudioEvent(AUDIO_PLAY_CHOSEN);
            audioEvent.setAudioIndex(index);
            EventBus.getDefault().post(audioEvent);
        }
    }

    private class MyPlayListSelectListener implements PlayListAdapter.ListOnSelectListener{

        @Override
        public void onItemSelected(int index) {

        }
    }
}
