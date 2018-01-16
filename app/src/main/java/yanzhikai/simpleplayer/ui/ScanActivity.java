package yanzhikai.simpleplayer.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import yanzhikai.simpleplayer.R;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        btn_scan = findViewById(R.id.btn_scan);
        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doScaningHandler();
            }
        });
        pb_scan = findViewById(R.id.pb_scan);
        mHandler = new ScanHandler();
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
        return LocalAudioDaoManager.getInstance().isExist(hash);
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
                    pb_scan.setVisibility(View.INVISIBLE);
                    break;

            }
        }
    }



}
