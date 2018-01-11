package yanzhikai.simpleplayer.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import yanzhikai.simpleplayer.MainActivity;
import yanzhikai.simpleplayer.R;
import yanzhikai.simpleplayer.SimpleAudioPlayer;
import yanzhikai.simpleplayer.event.AudioEvent;

public class AudioPlayerService extends Service {
    public static final String TAG = "yjkAudioPlayerService";

    private SimpleAudioPlayer mAudioPlayer;

    private Notification mNotification;

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
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
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

        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void handleEvent(AudioEvent event){
        switch (event.type){
            case AudioEvent.AUDIO_NULL:

                break;
            case AudioEvent.AUDIO_PLAY:

                break;
            case AudioEvent.AUDIO_PAUSE:

                break;
            case AudioEvent.AUDIO_PRE:

                break;
            case AudioEvent.AUDIO_NEXT:

                break;
        }
    }
}
