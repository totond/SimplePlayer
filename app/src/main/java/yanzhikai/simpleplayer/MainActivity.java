package yanzhikai.simpleplayer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import yanzhikai.simpleplayer.adapter.PlayListAdapter;
import yanzhikai.simpleplayer.event.AudioEvent;
import yanzhikai.simpleplayer.model.AudioInfo;
import yanzhikai.simpleplayer.model.PlayList;
import yanzhikai.simpleplayer.service.AudioPlayerService;
import yanzhikai.simpleplayer.ui.ScanActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    public static final String TAG = "yjkMainActivity";
    private Button btn_play,btn_pause,btn_stop,btn_choose;
    private SimpleAudioPlayer mAudioPlayer;
    private RecyclerView rv_play_list;
    private PlayListAdapter mPlayListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAudioPlayer = new SimpleAudioPlayer(this);
        mAudioPlayer.setAudioListener(new MyAudioPlayerListener());
        EventBus.getDefault().register(this);
        initView();
    }

    @Subscribe
    public void testEvent(AudioInfo info){
        Log.d(TAG, "testEvent: ");
    }

    private void initView() {
        btn_play = findViewById(R.id.btn_play);
        btn_pause = findViewById(R.id.btn_pause);
        btn_stop = findViewById(R.id.btn_stop);
        btn_choose = findViewById(R.id.btn_choose);
        btn_play.setOnClickListener(this);
        btn_pause.setOnClickListener(this);
        btn_stop.setOnClickListener(this);
        btn_choose.setOnClickListener(this);

        rv_play_list = findViewById(R.id.rv_play_list);
        rv_play_list.setLayoutManager(new LinearLayoutManager(this));
        mPlayListAdapter = new PlayListAdapter(this,PlayList.getInstance().getAudioList());
        rv_play_list.setAdapter(mPlayListAdapter);

    }

    private void updateList(){
        mPlayListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_play:
//                mAudioPlayer.start();
//                startService(new Intent(this,AudioPlayerService.class));
                Log.d(TAG, "PlayList.getInstance().getAudioList(): " + PlayList.getInstance().getAudioList().size());
                updateList();
                break;
            case R.id.btn_pause:
                mAudioPlayer.pause();
                EventBus.getDefault().post(new AudioEvent());
                break;
            case R.id.btn_stop:
                mAudioPlayer.stop();
                break;
            case R.id.btn_choose:
//                mAudioPlayer.setPath("/storage/emulated/0/Music/陈奕迅 - 陀飞轮.mp3");
//                mAudioPlayer.prepareAsync();
                startActivity(new Intent(this, ScanActivity.class));
                break;
        }
    }

    private class MyAudioPlayerListener implements AudioPlayerListener{

        @Override
        public void onNewPlayer() {
            Log.d(TAG, "onNewPlayer: ");
        }

        @Override
        public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int i) {
            Log.d(TAG, "onBufferingUpdate: ");
        }

        @Override
        public void onCompletion(IMediaPlayer iMediaPlayer) {
            Log.d(TAG, "onCompletion: ");
        }

        @Override
        public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
            Log.d(TAG, "onError: ");
            return false;
        }

        @Override
        public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i1) {
            Log.d(TAG, "onInfo: ");
            return false;
        }

        @Override
        public void onPrepared(IMediaPlayer iMediaPlayer) {
            Log.d(TAG, "onPrepared: ");
            Toast.makeText(MainActivity.this, "onPrepared", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onSeekComplete(IMediaPlayer iMediaPlayer) {
            Log.d(TAG, "onSeekComplete: ");

        }

        @Override
        public void onVideoSizeChanged(IMediaPlayer iMediaPlayer, int i, int i1, int i2, int i3) {
            Log.d(TAG, "onVideoSizeChanged: ");

        }
    }
}