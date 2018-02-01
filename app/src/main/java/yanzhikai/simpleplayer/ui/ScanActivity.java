package yanzhikai.simpleplayer.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import java.util.ArrayList;

import yanzhikai.simpleplayer.R;
import yanzhikai.simpleplayer.db.AudioListDaoManager;
import yanzhikai.simpleplayer.db.LocalAudioDaoManager;
import yanzhikai.simpleplayer.model.AudioInfo;
import yanzhikai.simpleplayer.utils.MediaUtil;

public class ScanActivity extends Activity {
    public static final String TAG = "yjkScanActivity";
    private Button btn_scan;
    private ProgressBar pb_scan;

    private final int SCAN = 0;
    private final int SCANING = 1;
    private final int FINISH = 2;

    private Handler mHandler;

    private ArrayList<AudioInfo> audioInfos = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        Log.d(TAG, "onCreate:Local size: " + AudioListDaoManager.getInstance().queryAllLocalAudio().size());
        btn_scan = findViewById(R.id.btn_scan);
        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doScanningHandler();
            }
        });
        pb_scan = findViewById(R.id.pb_scan);
        mHandler = new ScanHandler();
    }

    /**
     * 扫描
     */
    private void doScanningHandler() {
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
                            AudioListDaoManager.getInstance().insertAudioToList(audioInfo,AudioListDaoManager.getInstance().getLocalListInfo());
                            audioInfos.add(audioInfo);
                            LocalAudioDaoManager.getInstance().insertAudio(audioInfo);
                        }

                    }

                    @Override
                    public boolean filter(String hash) {
                        return isExist(hash);
                    }
                },false);
                mHandler.sendEmptyMessage(FINISH);
            }
        }.start();

    }

    //在AudioListDaoManager里面做了存在判断，这里不用做
    private boolean isExist(String hash){
//        return LocalAudioDaoManager.getInstance().isAudioListExist(hash);
//        return AudioListDaoManager.getInstance().isExistInLocalList(hash);
        return false;
    }

//    private void testAudioList(){
////        AudioListDaoManager.getInstance().init(this);
//
//        //新建
//        AudioListInfo audioListInfo = new AudioListInfo();
//        audioListInfo.setListName("TestList1");
//        AudioListDaoManager.getInstance().insertList(audioListInfo);
//
//        //修改
//        AudioListInfo audioListInfo1 = AudioListDaoManager.getInstance().queryAudioListByQueryBuilder("TestList1").get(0);
//        Log.d(TAG, "testAudioList: pre pre size:" + audioListInfo1.getInfoList().size());
//        audioListInfo1.getInfoList().addAll(LocalAudioDaoManager.getInstance().queryAllAudio());
//        Log.d(TAG, "testAudioList: pre size:" + audioListInfo1.getInfoList().size());
//        audioListInfo1.update();
//        audioListInfo1.resetInfoList();
////        AudioListDaoManager.getInstance().getDaoMaster().newSession().update(audioListInfo1);
//        //查询
//        queryTest();
//
//    }

//    private void queryTest(){
//        //查询
//        List<AudioInfo> audioInfos = AudioListDaoManager.getInstance().queryAudioListByQueryBuilder("TestList1").get(0).getInfoList();
//        Log.d(TAG, "testAudioList:size " + AudioListDaoManager.getInstance().queryAudioListByQueryBuilder("TestList1").size());
//        Log.d(TAG, "testAudioList:result " + audioInfos.size());
////        ToastUtil.makeShortToast(this,audioInfos.get(0).getSongName());
//        Log.d(TAG, "testAudioList: thread :" + Thread.currentThread().getName());
//    }


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
                    AudioListDaoManager.getInstance().getLocalListInfo().getInfoList().addAll(audioInfos);
                    AudioListDaoManager.getInstance().getLocalListInfo().update();
                    Log.d(TAG, "handleMessage: FINISH");
                    Log.d(TAG, "handle LocalAudioList: size: " + AudioListDaoManager.getInstance().queryAudioListByQueryBuilder(AudioListDaoManager.LOCAL_LIST_NAME).size());
                    AudioListDaoManager.getInstance().queryAudioListByQueryBuilder(AudioListDaoManager.LOCAL_LIST_NAME).get(0).resetInfoList();
                    Log.d(TAG, "handleMessage:count " + AudioListDaoManager.getInstance().queryAllLocalAudio().size());
//                    Log.d(TAG, "testAudioList:id " + AudioListDaoManager.getInstance().queryAudioListByQueryBuilder(AudioListDaoManager.LOCAL_LIST_NAME).get(0).getInfoList().get(0).getSongName());
                    pb_scan.setVisibility(View.INVISIBLE);
//                    testAudioList();
                    break;

            }
        }
    }




}
