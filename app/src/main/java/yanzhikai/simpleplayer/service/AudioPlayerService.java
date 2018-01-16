package yanzhikai.simpleplayer.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import yanzhikai.simpleplayer.AudioPlayerListener;
import yanzhikai.simpleplayer.MainActivity;
import yanzhikai.simpleplayer.R;
import yanzhikai.simpleplayer.SimpleAudioPlayer;
import yanzhikai.simpleplayer.event.AudioEvent;
import yanzhikai.simpleplayer.event.CurrentAudioDetailEvent;
import yanzhikai.simpleplayer.model.AudioInfo;
import yanzhikai.simpleplayer.model.PlayList;
import yanzhikai.simpleplayer.utils.MediaUtil;

public class AudioPlayerService extends Service {
    public static final String TAG = "yjkAudioPlayerService";

    private boolean isPrepared = false;

    private SimpleAudioPlayer mAudioPlayer;

    private Notification mNotification;

    private PlayingThread mPlayingThread;

    public AudioPlayerService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        buildNotification();
        init();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        startForeground(110, mNotification);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void buildNotification() {
        Notification.Builder builder = new Notification.Builder
                (this.getApplicationContext()); //获取一个Notification构造器
        Intent nfIntent = new Intent(this, MainActivity.class);

        builder.setContentIntent(PendingIntent.
                getActivity(this, 0, nfIntent, 0)) // 设置PendingIntent

                .setContentTitle("下拉列表中的Title") // 设置下拉列表里的标题
                .setSmallIcon(R.mipmap.ic_launcher) // 设置状态栏内的小图标
                .setContentText("要显示的内容") // 设置上下文内容
                .setWhen(System.currentTimeMillis()); // 设置该通知发生的时间

        mNotification = builder.build(); // 获取构建好的Notification
        mNotification.defaults = Notification.DEFAULT_SOUND; //设置为默认的声音

    }

    private void init(){
        mAudioPlayer = new SimpleAudioPlayer(getApplicationContext());
        mAudioPlayer.setAudioListener(new MyAudioPlayerListener());
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopPlayingThread();
        EventBus.getDefault().unregister(this);
    }

    private void playNext(){
        mAudioPlayer.setPath(PlayList.getInstance().getNextAudio(false).getFilePath());
        mAudioPlayer.prepareAsync();
    }

    private void playPre(){
        mAudioPlayer.setPath(PlayList.getInstance().getNextAudio(true).getFilePath());
        mAudioPlayer.prepareAsync();
    }

    @Subscribe
    public void handleEvent(AudioEvent event){
        Log.d(TAG, "handleEvent: " + event.getType());
        switch (event.getType()){
            case AudioEvent.AUDIO_NULL:

                break;
            case AudioEvent.AUDIO_PLAY:
                if (isPrepared){
                    mAudioPlayer.start();
                }
                break;
            case AudioEvent.AUDIO_PAUSE:
                mAudioPlayer.pause();
                stopPlayingThread();
                break;
            case AudioEvent.AUDIO_PRE:
                playPre();
                break;
            case AudioEvent.AUDIO_NEXT:
                playNext();
                break;
            case AudioEvent.AUDIO_PLAY_CHOSEN:
                Log.d(TAG, "handleEvent: AUDIO_PLAY_CHOSEN:" + event.getInfo().getFilePath());
                mAudioPlayer.setPath(event.getInfo().getFilePath());
                mAudioPlayer.prepareAsync();
                startPlayingThread();
                break;
        }
    }

    private void handlePlayingDetail(){
        Log.d(TAG, "handlePlayingDetail: ");
        CurrentAudioDetailEvent detailEvent = new CurrentAudioDetailEvent();
        AudioInfo currentInfo = PlayList.getInstance().getCurrentAudio();
        detailEvent.durationText = currentInfo.getDurationText();
        detailEvent.progress = (float) mAudioPlayer.getCurrentPosition() / currentInfo.getDuration();
        detailEvent.currentTimeText = MediaUtil.parseTimeToString((int) (currentInfo.getDuration() * detailEvent.progress));
        EventBus.getDefault().post(currentInfo);
    }

    private void startPlayingThread(){
        if (mPlayingThread == null) {
            mPlayingThread = new PlayingThread();
        }
        mPlayingThread.isPlaying = true;
        mPlayingThread.start();
    }

    private void stopPlayingThread(){
        if (mPlayingThread != null) {
            mPlayingThread.isPlaying = false;
        }
        mPlayingThread = null;
    }

    private class PlayingThread extends Thread{
        public boolean isPlaying = false;

        public PlayingThread(){
            isPlaying = true;
        }

        @Override
        public void run() {
            if (isPlaying){
                handlePlayingDetail();
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class MyAudioPlayerListener implements AudioPlayerListener {

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
            Toast.makeText(AudioPlayerService.this, "onCompletion", Toast.LENGTH_SHORT).show();
            playNext();
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
            isPrepared = true;
            Toast.makeText(AudioPlayerService.this, "onPrepared", Toast.LENGTH_SHORT).show();
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
