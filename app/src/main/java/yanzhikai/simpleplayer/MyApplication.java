package yanzhikai.simpleplayer;

import android.app.Application;
import android.content.Intent;

import org.greenrobot.eventbus.EventBus;

import yanzhikai.simpleplayer.db.AudioListDaoManager;
import yanzhikai.simpleplayer.db.LocalAudioDaoManager;
import yanzhikai.simpleplayer.db.PlayListAudioDaoManager;
import yanzhikai.simpleplayer.service.AudioPlayerService;

/**
 * author : yany
 * e-mail : yanzhikai_yjk@qq.com
 * time   : 2018/01/11
 * desc   :
 */

public class MyApplication extends Application {
    public static final String PLAY_LIST_NAME = "播放列表";

    public static boolean countDowning = false;

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void init(){
        LocalAudioDaoManager.getInstance().init(getApplicationContext());
        PlayListAudioDaoManager.getInstance().init(getApplicationContext());
        AudioListDaoManager.getInstance().init(getApplicationContext());
        startService(new Intent(this,AudioPlayerService.class));
    }

    public static void closeDB(){
        LocalAudioDaoManager.getInstance().closeConnection();
        PlayListAudioDaoManager.getInstance().closeConnection();
        AudioListDaoManager.getInstance().closeConnection();
    }


}
