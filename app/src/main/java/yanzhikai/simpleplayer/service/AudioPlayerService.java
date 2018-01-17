package yanzhikai.simpleplayer.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkTimedText;
import yanzhikai.simpleplayer.AudioPlayerListener;
import yanzhikai.simpleplayer.MainActivity;
import yanzhikai.simpleplayer.R;
import yanzhikai.simpleplayer.SimpleAudioPlayer;
import yanzhikai.simpleplayer.event.AudioChangedEvent;
import yanzhikai.simpleplayer.event.AudioEvent;
import yanzhikai.simpleplayer.event.CurrentAudioDetailEvent;
import yanzhikai.simpleplayer.event.UIControlEvent;
import yanzhikai.simpleplayer.model.AudioInfo;
import yanzhikai.simpleplayer.model.PlayList;
import yanzhikai.simpleplayer.utils.MediaUtil;

public class AudioPlayerService extends Service {
    public static final String TAG = "yjkAudioPlayerService";

    private boolean isPrepared = false;

    private SimpleAudioPlayer mAudioPlayer;

    private Notification mNotification;

    private PlayingThread mPlayingThread;

    private boolean isSeeking = false;

    public static boolean isPlaying = false;

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

    private void init() {
        mAudioPlayer = new SimpleAudioPlayer(getApplicationContext());
        mAudioPlayer.setAudioListener(new MyAudioPlayerListener());
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopPlayingThread();
        EventBus.getDefault().unregister(this);
        mAudioPlayer.release();
    }

    private void playNext() {
        AudioInfo audioInfo = PlayList.getInstance().getNextAudio(false);
        mAudioPlayer.setPath(audioInfo.getFilePath());
        mAudioPlayer.prepareAsync();
//        PlayList.getInstance().setCurrentAudio(audioInfo);
        startPlayingThread();
        notifyAudioChanged();
    }

    private void playPre() {
        AudioInfo audioInfo = PlayList.getInstance().getNextAudio(true);
        mAudioPlayer.setPath(audioInfo.getFilePath());
        mAudioPlayer.prepareAsync();
//        PlayList.getInstance().setCurrentAudio(audioInfo);
        startPlayingThread();
        notifyAudioChanged();
    }

    @Subscribe
    public void handleEvent(AudioEvent event) {
        Log.d(TAG, "handleEvent: " + event.getType());
        switch (event.getType()) {
            case AudioEvent.AUDIO_NULL:

                break;
            case AudioEvent.AUDIO_PLAY:
                if (isPrepared) {
                    mAudioPlayer.start();
                }
                isPlaying = true;
                startPlayingThread();
                break;
            case AudioEvent.AUDIO_PAUSE:
                mAudioPlayer.pause();
                stopPlayingThread();
                isPlaying = false;
                break;
            case AudioEvent.AUDIO_PRE:
                playPre();

                break;
            case AudioEvent.AUDIO_NEXT:
                playNext();

                break;
            case AudioEvent.AUDIO_PLAY_CHOSEN:
                AudioInfo info = PlayList.getInstance().getAudioList().get(event.getAudioIndex());
                Log.d(TAG, "handleEvent: AUDIO_PLAY_CHOSEN:" + info.getFilePath());
                mAudioPlayer.setPath(info.getFilePath());
                mAudioPlayer.prepareAsync();
                PlayList.getInstance().setCurrentAudio(info, event.getAudioIndex());
                startPlayingThread();
                notifyAudioChanged();
                break;
            case AudioEvent.AUDIO_SEEK_START:
                if (mAudioPlayer.isPlaying()){
                    makeToast("isSeeking ");
                    isSeeking = true;
                }
                break;
            case AudioEvent.AUDIO_SEEK_TO:
                if (mAudioPlayer.isPlaying()) {
                    Log.i(TAG, "seek to: " + (long) (event.getProgress() * PlayList.getInstance().getCurrentAudio().getDuration()));
                    mAudioPlayer.seekTo((long) (event.getProgress() * PlayList.getInstance().getCurrentAudio().getDuration()));
                }
                break;
        }
    }

    private void makeToast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_LONG).show();
    }


    private void notifyAudioChanged() {
        EventBus.getDefault().post(new AudioChangedEvent());
    }

    private void handlePlayingDetail() {
        if (!isSeeking) {
            CurrentAudioDetailEvent detailEvent = new CurrentAudioDetailEvent();
            AudioInfo currentInfo = PlayList.getInstance().getCurrentAudio();
            if (currentInfo != null) {
                detailEvent.durationText = currentInfo.getDurationText();
                detailEvent.progress = (float) mAudioPlayer.getCurrentPosition() / currentInfo.getDuration();
                detailEvent.currentTimeText = MediaUtil.parseTimeToString((int) (currentInfo.getDuration() * detailEvent.progress));
                EventBus.getDefault().post(detailEvent);
            }
        }
    }

    private void startPlayingThread() {
        Log.i(TAG, "startPlayingThread: ");
        if (mPlayingThread != null) {
            mPlayingThread.isPlaying = false;
            mPlayingThread = null;
        }

        mPlayingThread = new PlayingThread();
        mPlayingThread.start();

    }

    private void stopPlayingThread() {
        if (mPlayingThread != null) {
            mPlayingThread.isPlaying = false;
        }
        mPlayingThread = null;
    }

    private class PlayingThread extends Thread {
        public boolean isPlaying = false;

        public PlayingThread() {
            isPlaying = true;
        }

        @Override
        public void run() {
            while (isPlaying) {
                Log.i(TAG, "run: " + getId());
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
            Log.i(TAG, "onBufferingUpdate: ");
        }

        @Override
        public void onCompletion(IMediaPlayer iMediaPlayer) {
            Log.i(TAG, "onCompletion: ");
            Toast.makeText(AudioPlayerService.this, "onCompletion", Toast.LENGTH_SHORT).show();
            playNext();
        }

        @Override
        public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
            Log.i(TAG, "onError: ");
            if (i == IMediaPlayer.MEDIA_ERROR_UNKNOWN){
                Log.d(TAG, "MEDIA_ERROR_UNKNOWN: ");
            }
            return false;
        }

        @Override
        public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i1) {
            Log.i(TAG, "onInfo: ");
            if (i == IMediaPlayer.MEDIA_INFO_BUFFERING_START) {
                Log.d(TAG, "MEDIA_INFO_BUFFERING_START: ");
            } else if (i == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
                Log.d(TAG, "MEDIA_INFO_BUFFERING_END: ");
            }else if (i == IMediaPlayer.MEDIA_INFO_NOT_SEEKABLE){
                Log.d(TAG, "MEDIA_INFO_NOT_SEEKABLE: ");
            }
            return false;
        }

        @Override
        public void onPrepared(IMediaPlayer iMediaPlayer) {
            Log.i(TAG, "onPrepared: ");
            isPrepared = true;
            isPlaying = true;
            Toast.makeText(AudioPlayerService.this, "onPrepared", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onSeekComplete(IMediaPlayer iMediaPlayer) {
            Log.i(TAG, "onSeekComplete: ");
            isSeeking = false;
        }

        @Override
        public void onVideoSizeChanged(IMediaPlayer iMediaPlayer, int i, int i1, int i2, int i3) {
            Log.i(TAG, "onVideoSizeChanged: ");

        }

        @Override
        public void onTimedText(IMediaPlayer iMediaPlayer, IjkTimedText ijkTimedText) {
            Log.i(TAG, "onTimedText: " + ijkTimedText);
        }
    }
}
