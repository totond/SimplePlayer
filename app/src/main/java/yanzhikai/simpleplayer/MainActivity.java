package yanzhikai.simpleplayer;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import yanzhikai.simpleplayer.adapter.PlayListAdapter;
import yanzhikai.simpleplayer.db.LocalAudioDaoManager;
import yanzhikai.simpleplayer.db.PlayListAudioDaoManager;
import yanzhikai.simpleplayer.event.AudioChangedEvent;
import yanzhikai.simpleplayer.event.AudioEvent;
import yanzhikai.simpleplayer.event.CurrentAudioDetailEvent;
import yanzhikai.simpleplayer.model.AudioInfo;
import yanzhikai.simpleplayer.model.PlayList;
import yanzhikai.simpleplayer.service.AudioPlayerService;
import yanzhikai.simpleplayer.ui.ScanActivity;

import static yanzhikai.simpleplayer.event.AudioEvent.AUDIO_PLAY_CHOSEN;

public class MainActivity extends FragmentActivity implements View.OnClickListener{
    public static final String TAG = "yjkMainActivity";
    private Button btn_play,btn_pause,btn_stop,btn_choose;
    private SimpleAudioPlayer mAudioPlayer;
    private SeekBar sb_progress;
    private RecyclerView rv_play_list;
    private PlayListAdapter mPlayListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAudioPlayer = new SimpleAudioPlayer(this);
        startService(new Intent(this,AudioPlayerService.class));
        EventBus.getDefault().register(this);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateList();
    }

    private void initView() {
        btn_play = findViewById(R.id.btn_pre);
        btn_pause = findViewById(R.id.btn_pause);
        btn_stop = findViewById(R.id.btn_next);
        btn_choose = findViewById(R.id.btn_choose);
        sb_progress = findViewById(R.id.sb_progress);
        btn_play.setOnClickListener(this);
        btn_pause.setOnClickListener(this);
        btn_stop.setOnClickListener(this);
        btn_choose.setOnClickListener(this);
        sb_progress.setMax(1000);
        sb_progress.setOnSeekBarChangeListener(new MyProgressListener());

        rv_play_list = findViewById(R.id.rv_play_list);
        rv_play_list.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        mPlayListAdapter = new PlayListAdapter(this, PlayList.getInstance().getAudioList());
        mPlayListAdapter.setListener(new MyPlayListListener());
        rv_play_list.setAdapter(mPlayListAdapter);
        rv_play_list.addItemDecoration(divider);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleUpdate(CurrentAudioDetailEvent event){
        updateProgress(event.progress);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleAudioChanged(AudioChangedEvent changedEvent){
        mPlayListAdapter.refreshItem();
    }

    private void updateProgress(float progress){
        sb_progress.setProgress((int) (progress * 1000));
    }

    private void updateList(){
        Log.d(TAG, "updateList: ");
        PlayList.getInstance().add(LocalAudioDaoManager.getInstance().queryAllAudio());
//        PlayListAudioDaoManager.getInstance().insertMultiAudio(LocalAudioDaoManager.getInstance().queryAllAudio());
        mPlayListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_pre:
//                mAudioPlayer.start();

//                Log.d(TAG, "PlayList.getInstance().getAudioList(): " + PlayList.getInstance().getAudioList().size());
//                updateList();
                EventBus.getDefault().post(new AudioEvent(-1,AudioEvent.AUDIO_PRE));
                mPlayListAdapter.notifyItemChanged(1);
                break;
            case R.id.btn_pause:
//                mAudioPlayer.pause();
                EventBus.getDefault().post(new AudioEvent(-1,AudioEvent.AUDIO_PAUSE));
//                PlayingAudioInfo playing = PlayListAudioDaoManager.getInstance().queryPlayingAudio().get(0);
//                Log.d(TAG, "getSongName: " + playing.getSongName());
//                Log.d(TAG, "getCurrentTimeText: " + playing.getCurrentTimeText());
//                Log.d(TAG, "size: " + PlayListAudioDaoManager.getInstance().queryPlayingAudio().size());
                break;
            case R.id.btn_next:
//                mAudioPlayer.stop();
//                PlayingAudioInfo playingAudioInfo = new PlayingAudioInfo(PlayListAudioDaoManager.getInstance().queryAllAudio().get(0));
//                playingAudioInfo.setCurrentTime(0);
//                playingAudioInfo.setCurrentTimeText("00:00");
//                PlayListAudioDaoManager.getInstance().setPlayingAudio(playingAudioInfo);
                EventBus.getDefault().post(new AudioEvent(-1,AudioEvent.AUDIO_NEXT));
                break;
            case R.id.btn_choose:
//                mAudioPlayer.setPath("/storage/emulated/0/Music/陈奕迅 - 陀飞轮.mp3");
//                mAudioPlayer.prepareAsync();
                startActivity(new Intent(this, ScanActivity.class));
                break;
        }
    }

    private class MyPlayListListener implements PlayListAdapter.PlayListItemOnClickListener {
        @Override
        public void onItemClick(AudioInfo info, int index) {
            Log.d(TAG, "onItemClick: " + info.getSongName());
            EventBus.getDefault().post(new AudioEvent(index,AUDIO_PLAY_CHOSEN));
        }
    }

    private class MyProgressListener implements SeekBar.OnSeekBarChangeListener{

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        PlayListAudioDaoManager.getInstance().closeConnection();
        LocalAudioDaoManager.getInstance().closeConnection();
    }
}