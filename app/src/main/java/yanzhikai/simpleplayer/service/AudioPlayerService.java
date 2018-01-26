package yanzhikai.simpleplayer.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkTimedText;
import yanzhikai.simpleplayer.AudioPlayerListener;
import yanzhikai.simpleplayer.MainActivity;
import yanzhikai.simpleplayer.R;
import yanzhikai.simpleplayer.SimpleAudioPlayer;
import yanzhikai.simpleplayer.event.AudioEvent;
import yanzhikai.simpleplayer.event.AudioStartPauseEvent;
import yanzhikai.simpleplayer.event.CurrentAudioDetailEvent;
import yanzhikai.simpleplayer.event.PlayListChangedEvent;
import yanzhikai.simpleplayer.model.AudioInfo;
import yanzhikai.simpleplayer.model.PlayList;
import yanzhikai.simpleplayer.utils.MediaUtil;
import yanzhikai.simpleplayer.utils.ToastUtil;

public class AudioPlayerService extends Service {
    public static final String TAG = "yjkAudioPlayerService";

    private boolean isPrepared = false;

    private SimpleAudioPlayer mAudioPlayer;

    private Notification mNotification;

    private PlayingThread mPlayingThread;

    private boolean isSeeking = false;

    public static boolean isPlaying = false;

    private static final int NOTIFICATION_ID = 110;

    private static final int REQUEST_CODE = 0;
    private static final String ACTION_PLAY_PAUSE = "PLAY_PAUSE";
    private static final String ACTION_PRE = "PRE";
    private static final String ACTION_NEXT = "NEXT";

    /**
     * 状态栏播放器视图
     */
    private RemoteViews mNotificationRemoteViews;

    private PlayerReceiver mPlayerReceiver;

    public AudioPlayerService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        init();
        buildNotification();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        startForeground(NOTIFICATION_ID, mNotification);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void buildNotification() {
        mNotificationRemoteViews = new RemoteViews(getPackageName(), R.layout.layout_player_notification);
        Intent intentPlay = new Intent(ACTION_PLAY_PAUSE);// 指定操作意图--设置对应的行为ACTION
        PendingIntent pIntentPlay = PendingIntent.getBroadcast(this.getApplicationContext(),
                REQUEST_CODE, intentPlay, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intentPre = new Intent(ACTION_PRE);
        PendingIntent pIntentPre = PendingIntent.getBroadcast(this.getApplicationContext(),
                REQUEST_CODE, intentPre, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intentNext = new Intent(ACTION_NEXT);
        PendingIntent pIntentNext = PendingIntent.getBroadcast(this.getApplicationContext(),
                REQUEST_CODE, intentNext, PendingIntent.FLAG_UPDATE_CURRENT);


        Notification.Builder builder = new Notification.Builder
                (this.getApplicationContext()); //获取一个Notification构造器
        Intent nfIntent = new Intent(this, MainActivity.class);

        builder.setContentIntent(PendingIntent.
                getActivity(this, 0, nfIntent, 0)) // 设置PendingIntent
                .setContent(mNotificationRemoteViews)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher);

        mNotificationRemoteViews.setOnClickPendingIntent(R.id.iv_play_pause, pIntentPlay);
        mNotificationRemoteViews.setOnClickPendingIntent(R.id.iv_pre, pIntentPre);
        mNotificationRemoteViews.setOnClickPendingIntent(R.id.iv_next, pIntentNext);


        mNotification = builder.build(); // 获取构建好的Notification
//        mNotification.defaults = Notification.DEFAULT_SOUND; //设置为默认的声音

    }

    private void init() {
        mAudioPlayer = new SimpleAudioPlayer(getApplicationContext());
        mAudioPlayer.setAudioListener(new MyAudioPlayerListener());
        EventBus.getDefault().register(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_PLAY_PAUSE);
        filter.addAction(ACTION_NEXT);
        filter.addAction(ACTION_PRE);

        mPlayerReceiver = new PlayerReceiver();
        registerReceiver(mPlayerReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopPlayingThread();
        EventBus.getDefault().unregister(this);
        mAudioPlayer.release();
        unregisterReceiver(mPlayerReceiver);
    }

    private void playStart() {
        if (isPrepared) {
            mAudioPlayer.start();
        }
        startPlayingThread();
        isPlaying = true;
        EventBus.getDefault().post(new AudioStartPauseEvent(true));
        updateNotification();
    }

    private void playPause() {
        mAudioPlayer.pause();
        stopPlayingThread();
        isPlaying = false;
        EventBus.getDefault().post(new AudioStartPauseEvent(false));

        updateNotification();
    }

    private void playNext() {
        AudioInfo audioInfo = PlayList.getInstance().getNextAudio(false);
        mAudioPlayer.setPath(audioInfo.getFilePath());
        mAudioPlayer.prepareAsync();
//        PlayList.getInstance().setCurrentAudio(audioInfo);
        startPlayingThread();
        notifyAudioChanged();
        isPlaying = true;
        EventBus.getDefault().post(new AudioStartPauseEvent(true));
        updateNotification();
    }

    private void playPre() {
        AudioInfo audioInfo = PlayList.getInstance().getNextAudio(true);
        mAudioPlayer.setPath(audioInfo.getFilePath());
        mAudioPlayer.prepareAsync();
//        PlayList.getInstance().setCurrentAudio(audioInfo);
        startPlayingThread();
        notifyAudioChanged();
        isPlaying = true;
        EventBus.getDefault().post(new AudioStartPauseEvent(true));
        updateNotification();
    }

    private void updateNotification() {
        if (isPlaying) {
            mNotificationRemoteViews.setImageViewResource(R.id.iv_play_pause, R.mipmap.pause);
        } else {
            mNotificationRemoteViews.setImageViewResource(R.id.iv_play_pause, R.mipmap.play);
        }
        AudioInfo audioInfo = PlayList.getInstance().getCurrentAudio();
        if (audioInfo != null) {
            mNotificationRemoteViews.setTextViewText(R.id.tv_title, audioInfo.getSongName());
        } else {
            mNotificationRemoteViews.setTextViewText(R.id.tv_title, getResources().getString(R.string.string_welcome));
        }
        startForeground(NOTIFICATION_ID, mNotification);
    }

    @Subscribe
    public void handleEvent(AudioEvent event) {
        Log.d(TAG, "handleEvent: " + event.getType());
        switch (event.getType()) {
            case AudioEvent.AUDIO_NULL:

                break;
            case AudioEvent.AUDIO_PLAY:
                playStart();
                break;
            case AudioEvent.AUDIO_PAUSE:
                playPause();
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
                isPlaying = true;
                EventBus.getDefault().post(new AudioStartPauseEvent(true));
                updateNotification();
                break;
            case AudioEvent.AUDIO_SEEK_START:
                if (mAudioPlayer.isPlaying()) {
                    isSeeking = true;
                }
                break;
            case AudioEvent.AUDIO_SEEK_TO:
//                if (mAudioPlayer.isPlaying()) {
                AudioInfo audioInfo = PlayList.getInstance().getCurrentAudio();
                if (audioInfo != null) {
                    Log.i(TAG, "seek to: " + (long) (event.getProgress() * PlayList.getInstance().getCurrentAudio().getDuration()));
                    mAudioPlayer.seekTo((long) (event.getProgress() * PlayList.getInstance().getCurrentAudio().getDuration()));
                }
//                }
                break;
        }
    }


    private void notifyAudioChanged() {
        EventBus.getDefault().post(new PlayListChangedEvent(PlayListChangedEvent.CURRENT_AUDIO_CHANGED));
    }

    private void handlePlayingDetail() {
        if (!isSeeking) {
            AudioInfo currentInfo = PlayList.getInstance().getCurrentAudio();
            if (currentInfo != null) {
                CurrentAudioDetailEvent detailEvent = new CurrentAudioDetailEvent();
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
            switch (i) {
                case IMediaPlayer.MEDIA_ERROR_UNKNOWN:
                    ToastUtil.makeShortToast(AudioPlayerService.this, "MEDIA_ERROR_UNKNOWN");
                    break;
                case IMediaPlayer.MEDIA_ERROR_SERVER_DIED:
                    ToastUtil.makeShortToast(AudioPlayerService.this, "MEDIA_ERROR_SERVER_DIED");
                    break;
                case IMediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                    ToastUtil.makeShortToast(AudioPlayerService.this, "MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK");
                    break;
                case IMediaPlayer.MEDIA_ERROR_IO:
                    ToastUtil.makeShortToast(AudioPlayerService.this, "MEDIA_ERROR_IO");
                    break;
                case IMediaPlayer.MEDIA_ERROR_MALFORMED:
                    ToastUtil.makeShortToast(AudioPlayerService.this, "MEDIA_ERROR_MALFORMED");
                    break;
                case IMediaPlayer.MEDIA_ERROR_UNSUPPORTED:
                    ToastUtil.makeShortToast(AudioPlayerService.this, "MEDIA_ERROR_UNSUPPORTED");
                    break;
                case IMediaPlayer.MEDIA_ERROR_TIMED_OUT:
                    ToastUtil.makeShortToast(AudioPlayerService.this, "MEDIA_ERROR_TIMED_OUT");
                    break;
            }
            return false;
        }

        @Override
        public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i1) {
//            ToastUtil.makeShortToast(AudioPlayerService.this, "onInfo" + i);
            Log.i(TAG, "onInfo: ");
            if (i == IMediaPlayer.MEDIA_INFO_BUFFERING_START) {
                Log.d(TAG, "MEDIA_INFO_BUFFERING_START: ");
            } else if (i == MediaPlayer.MEDIA_INFO_BUFFERING_END) {

                Log.d(TAG, "MEDIA_INFO_BUFFERING_END: ");
            } else if (i == IMediaPlayer.MEDIA_INFO_NOT_SEEKABLE) {
                Log.d(TAG, "MEDIA_INFO_NOT_SEEKABLE: ");
            }
            return false;
        }

        @Override
        public void onPrepared(IMediaPlayer iMediaPlayer) {
            Log.i(TAG, "onPrepared: ");
            isPrepared = true;
            isPlaying = true;
            EventBus.getDefault().post(new AudioStartPauseEvent(true));
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


    public class PlayerReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null) {
                switch (intent.getAction()) {
                    case ACTION_PLAY_PAUSE:
                        if (mAudioPlayer.isPlaying()) {
                            playPause();
                            ToastUtil.makeShortToast(AudioPlayerService.this, "playPause");
                        } else {
                            ToastUtil.makeShortToast(AudioPlayerService.this, "playStart");
                            playStart();
                        }
                        break;
                    case ACTION_NEXT:
                        playNext();
                        break;
                    case ACTION_PRE:
                        playPre();
                        break;
                }
            }
        }
    }
}
