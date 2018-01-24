package yanzhikai.simpleplayer.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import java.util.List;

import yanzhikai.simpleplayer.R;
import yanzhikai.simpleplayer.db.AudioListDaoManager;
import yanzhikai.simpleplayer.db.LocalAudioDaoManager;
import yanzhikai.simpleplayer.model.AudioInfo;
import yanzhikai.simpleplayer.model.AudioListInfo;
import yanzhikai.simpleplayer.utils.MediaUtil;
import yanzhikai.simpleplayer.utils.ToastUtil;

public class ScanActivity extends Activity {
    public static final String TAG = "yjkScanActivity";
    private Button btn_scan;
    private ProgressBar pb_scan;

    private final int SCAN = 0;
    private final int SCANING = 1;
    private final int FINISH = 2;

    private Handler mHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        Log.d(TAG, "onCreate:Local size: " + AudioListDaoManager.getInstance().queryAllLocalAudio().size());
        btn_scan = findViewById(R.id.btn_scan);
        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doScaningHandler();
            }
        });
        pb_scan = findViewById(R.id.pb_scan);
        mHandler = new ScanHandler();
//        testAudioList();
    }

    /**
     * 扫描
     */
    private void doScaningHandler() {
        mHandler.sendEmptyMessage(SCANING);
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                MediaUtil.scanLocalMusic(ScanActivity.this, new MediaUtil.ForeachListener() {
                    @Override
                    public void foreach(AudioInfo audioInfo) {

                        if (audioInfo != null) {
                            Log.d(TAG, "audioInfo: " + audioInfo.toString());
//                            PlayList.getInstance().add(audioInfo);
                            AudioListDaoManager.getInstance().insertLocalAudio(audioInfo);
                            LocalAudioDaoManager.getInstance().insertAudio(audioInfo);
                        }

                    }

                    @Override
                    public boolean filter(String hash) {
                        return isExist(hash);
                    }
                });
                mHandler.sendEmptyMessage(FINISH);
            }
        }.start();

    }

    private boolean isExist(String hash){
//        return LocalAudioDaoManager.getInstance().isExist(hash);
        return AudioListDaoManager.getInstance().isExistInLocalList(hash);
    }

    private void testAudioList(){
//        AudioListDaoManager.getInstance().init(this);

        //新建
        AudioListInfo audioListInfo = new AudioListInfo();
        audioListInfo.setAudioListName("TestList1");
        AudioListDaoManager.getInstance().insertList(audioListInfo);

        //修改
        AudioListInfo audioListInfo1 = AudioListDaoManager.getInstance().queryAllList().get(0);
        audioListInfo1.getInfoList().addAll(LocalAudioDaoManager.getInstance().queryAllAudio());
        Log.d(TAG, "testAudioList:1size " + audioListInfo1.getInfoList().size());
        audioListInfo1.update();

        //查询
        List<AudioInfo> audioInfos = AudioListDaoManager.getInstance().queryAudioByQueryBuilder("TestList1").get(0).getInfoList();
        Log.d(TAG, "testAudioList:id " + AudioListDaoManager.getInstance().queryAudioByQueryBuilder("TestList1").get(0).get_id());
        Log.d(TAG, "testAudioList: " + audioInfos.get(0).getSongName());
        ToastUtil.makeShortToast(this,audioInfos.get(0).getSongName());
    }

    private class ScanHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SCAN:
//                    showScaningView();
                    break;
                case SCANING:
                    Log.d(TAG, "SCANING: ");
//                    updateScaningView();
                    pb_scan.setVisibility(View.VISIBLE);
                    break;
                case FINISH:
//                    showFinishView();
                    Log.d(TAG, "handleMessage: FINISH");
                    Log.d(TAG, "handle LocalAudioList: size: " + AudioListDaoManager.getInstance().queryAudioByQueryBuilder(AudioListDaoManager.LOCAL_LIST_NAME).size());
                    AudioListDaoManager.getInstance().queryAudioByQueryBuilder(AudioListDaoManager.LOCAL_LIST_NAME).get(0).resetInfoList();
                    Log.d(TAG, "handleMessage:count " + AudioListDaoManager.getInstance().queryAllLocalAudio().size());
//                    Log.d(TAG, "testAudioList:id " + AudioListDaoManager.getInstance().queryAudioByQueryBuilder(AudioListDaoManager.LOCAL_LIST_NAME).get(0).getInfoList().get(0).getSongName());
                    pb_scan.setVisibility(View.INVISIBLE);
                    testAudioList();
                    break;

            }
        }
    }




}
