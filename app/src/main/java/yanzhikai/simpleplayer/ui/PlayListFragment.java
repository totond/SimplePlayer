package yanzhikai.simpleplayer.ui;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import yanzhikai.simpleplayer.R;
import yanzhikai.simpleplayer.adapter.PlayListAdapter;
import yanzhikai.simpleplayer.db.LocalAudioDaoManager;
import yanzhikai.simpleplayer.event.AudioChangedEvent;
import yanzhikai.simpleplayer.event.AudioEvent;
import yanzhikai.simpleplayer.model.AudioInfo;
import yanzhikai.simpleplayer.model.PlayList;
import yanzhikai.simpleplayer.utils.EventUtil;
import yanzhikai.simpleplayer.utils.ToastUtil;

import static yanzhikai.simpleplayer.event.AudioEvent.AUDIO_PLAY_CHOSEN;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlayListFragment extends Fragment {
    private RecyclerView rv_play_list;
    private PlayListAdapter mPlayListAdapter;
    private TextView tv_edit;

    public PlayListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventUtil.register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventUtil.unregister(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_play_list, container, false);
        rv_play_list = rootView.findViewById(R.id.rv_play_list);
        rv_play_list.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration divider = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        mPlayListAdapter = new PlayListAdapter(getContext(), PlayList.getInstance().getAudioList());
        mPlayListAdapter.setOnClickListener(new MyPlayListListener());
        rv_play_list.setAdapter(mPlayListAdapter);
        rv_play_list.addItemDecoration(divider);

        tv_edit = rootView.findViewById(R.id.tv_edit);
        tv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayListAdapter.getEditMode()){
                    updateEditMode(true);
                    mPlayListAdapter.setEditMode(false);
                    ToastUtil.makeShortToast(PlayListFragment.this.getContext(),"选择了"+mPlayListAdapter.getSelectedItem().size());
                }else {
                    updateEditMode(false);
                    mPlayListAdapter.setEditMode(true);
                }



                mPlayListAdapter.notifyDataSetChanged();


            }
        });
        handleAudioChanged();
        return rootView;
    }

    //对切换歌曲做出的处理，滑动RecyclerView到相应位置
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleAudioChanged(AudioChangedEvent changedEvent){
        handleAudioChanged();
    }

    private void handleAudioChanged(){
        mPlayListAdapter.refreshItem();
        rv_play_list.smoothScrollToPosition(PlayList.getInstance().getCurrentIndex());
    }
    @Override
    public void onResume() {
        super.onResume();
        updateList();
    }

    private void updateList(){
        PlayList.getInstance().add(LocalAudioDaoManager.getInstance().queryAllAudio());
//        PlayListAudioDaoManager.getInstance().insertMultiAudio(LocalAudioDaoManager.getInstance().queryAllAudio());
        mPlayListAdapter.notifyDataSetChanged();
    }

    private void updateEditMode(boolean isEditing){
        if (isEditing){
            tv_edit.setText(R.string.play_list_edit);
        }else {
            tv_edit.setText(R.string.play_list_edit_completed);
        }
    }

    private class MyPlayListListener implements PlayListAdapter.PlayListItemOnClickListener {
        @Override
        public void onItemClick(int index) {
            EventBus.getDefault().post(new AudioEvent(index,AUDIO_PLAY_CHOSEN));
        }
    }

    private class MyPlayListSelectListener implements PlayListAdapter.ListOnSelectListener{

        @Override
        public void onItemSelected(int index) {

        }
    }
}
