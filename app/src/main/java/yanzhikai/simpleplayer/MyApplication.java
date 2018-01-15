package yanzhikai.simpleplayer;

import android.app.Application;

import yanzhikai.simpleplayer.db.LocalAudioDaoManager;
import yanzhikai.simpleplayer.db.PlayListAudioDaoManager;

/**
 * author : yany
 * e-mail : yanzhikai_yjk@qq.com
 * time   : 2018/01/11
 * desc   :
 */

public class MyApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void init(){
        LocalAudioDaoManager.getInstance().init(getApplicationContext());
        PlayListAudioDaoManager.getInstance().init(getApplicationContext());
    }

    public static void closeDB(){
        LocalAudioDaoManager.getInstance().closeConnection();
        PlayListAudioDaoManager.getInstance().closeConnection();
    }


}
