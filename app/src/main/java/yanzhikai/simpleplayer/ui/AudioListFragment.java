package yanzhikai.simpleplayer.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import yanzhikai.simpleplayer.MainActivity;
import yanzhikai.simpleplayer.R;
import yanzhikai.simpleplayer.adapter.AudioListAdapter;
import yanzhikai.simpleplayer.adapter.PlayListAdapter;
import yanzhikai.simpleplayer.db.AudioListDaoManager;
import yanzhikai.simpleplayer.event.AudioEvent;
import yanzhikai.simpleplayer.event.AudioListChangedEvent;
import yanzhikai.simpleplayer.event.ListSizeChangedEvent;
import yanzhikai.simpleplayer.model.AudioInfo;
import yanzhikai.simpleplayer.model.PlayList;
import yanzhikai.simpleplayer.utils.EventUtil;
import yanzhikai.simpleplayer.utils.ToastUtil;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AudioListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AudioListFragment extends Fragment implements View.OnClickListener {
    private static final String ARG_LIST_NAME = "listName";

    private RecyclerView rv_play_list;
    private AudioListAdapter mAudioListAdapter;
    private TextView tv_edit,tv_delete,tv_choose_all,tv_audio_list_title,tv_cover;
    private String listName;
    private ImageView iv_back;


    public AudioListFragment() {
        // Required empty public constructor
    }

    public static AudioListFragment newInstance(String listName) {
        AudioListFragment fragment = new AudioListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_LIST_NAME, listName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            listName = getArguments().getString(ARG_LIST_NAME);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventUtil.register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        EventUtil.register(this);

        View rootView = inflater.inflate(R.layout.fragment_audio_list, container, false);
        rv_play_list = rootView.findViewById(R.id.rv_play_list);
        rv_play_list.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration divider = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        mAudioListAdapter = new AudioListAdapter(getContext(), AudioListDaoManager.getInstance().queryAudioListByName(listName).getInfoList());
//        mAudioListAdapter.setOnClickListener(new PlayListFragment.MyPlayListListener());
        DefaultItemAnimator animator = new DefaultItemAnimator();
        animator.setAddDuration(2000);
        animator.setRemoveDuration(2000);
        rv_play_list.setItemAnimator(animator);
        rv_play_list.setAdapter(mAudioListAdapter);
        rv_play_list.addItemDecoration(divider);

        tv_audio_list_title = rootView.findViewById(R.id.tv_audio_list_title);
        tv_edit = rootView.findViewById(R.id.tv_edit);
        tv_delete = rootView.findViewById(R.id.tv_delete);
        tv_choose_all = rootView.findViewById(R.id.tv_choose_all);
        tv_cover = rootView.findViewById(R.id.tv_cover);
        iv_back = rootView.findViewById(R.id.iv_back);

        tv_edit.setOnClickListener(this);
        tv_delete.setOnClickListener(this);
        tv_choose_all.setOnClickListener(this);
        tv_cover.setOnClickListener(this);
        iv_back.setOnClickListener(this);

        tv_audio_list_title.setText(listName);

        return rootView;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleAudioChanged(AudioListChangedEvent audioListChangedEvent){
        switch (audioListChangedEvent.getType()){
            case AudioListChangedEvent.ITEM_ADDED:
                mAudioListAdapter.setAudioInfos(AudioListDaoManager.getInstance().queryAudioListByName(listName).getRefreshList());
                Log.d("yjk", "handleAudioChanged: ITEM_ADDED");
                mAudioListAdapter.notifyDataSetChanged();
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleSizeChanged(ListSizeChangedEvent listSizeChangedEvent){
        mAudioListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_edit:
                if (mAudioListAdapter.getEditMode()){
                    updateEditMode(true);
                    mAudioListAdapter.setEditMode(false);
//                    ToastUtil.makeShortToast(PlayListFragment.this.getContext(),"选择了"+mAudioListAdapter.getSelectedItem().size());
                }else {
                    updateEditMode(false);
                    mAudioListAdapter.clearSelected();
                    mAudioListAdapter.setEditMode(true);
                }

                mAudioListAdapter.notifyDataSetChanged();
                break;
            case R.id.tv_delete:
                ArrayList<AudioInfo> deleteList = mAudioListAdapter.getSelectedItem();
                for (AudioInfo audioInfo : deleteList) {
                    AudioListDaoManager.getInstance().deleteAudioInList(audioInfo,listName);
                }
                mAudioListAdapter.clearSelected();
                mAudioListAdapter.setAudioInfos(AudioListDaoManager.getInstance().queryAudioListByName(listName).getRefreshList());
                mAudioListAdapter.notifyDataSetChanged();
                ToastUtil.makeShortToast(getContext(),"删除了"+deleteList.size() + "首歌");
                break;
            case R.id.tv_choose_all:
                mAudioListAdapter.selectAllItems();
                mAudioListAdapter.notifyDataSetChanged();
                break;
            case R.id.tv_cover:
                PlayList.getInstance().clear();
                EventUtil.post(new AudioEvent(AudioEvent.AUDIO_NULL));
                PlayList.getInstance().add(mAudioListAdapter.getAudioInfos());
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                for (int i = fragmentManager.getBackStackEntryCount(); i > 0 ; i--){
                    fragmentManager.popBackStack();
                }
                break;
            case R.id.iv_back:
                MainActivity mainActivity = (MainActivity)getActivity();
                mainActivity.popFragment();
                break;
        }
    }

    private void updateEditMode(boolean isEditing){
        if (isEditing){
            tv_delete.setVisibility(View.INVISIBLE);
            tv_choose_all.setVisibility(View.INVISIBLE);
            tv_edit.setText(R.string.play_list_edit);
            tv_cover.setVisibility(View.VISIBLE);
        }else {
            tv_delete.setVisibility(View.VISIBLE);
            tv_choose_all.setVisibility(View.VISIBLE);
            tv_edit.setText(R.string.play_list_edit_completed);
            tv_cover.setVisibility(View.INVISIBLE);
        }
    }

    public String getListName() {
        return listName;
    }
}
