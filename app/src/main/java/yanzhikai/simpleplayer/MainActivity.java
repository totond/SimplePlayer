package yanzhikai.simpleplayer;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import yanzhikai.simpleplayer.adapter.PlayListAdapter;
import yanzhikai.simpleplayer.db.LocalAudioDaoManager;
import yanzhikai.simpleplayer.db.PlayListAudioDaoManager;
import yanzhikai.simpleplayer.event.AudioChangedEvent;
import yanzhikai.simpleplayer.event.AudioEvent;
import yanzhikai.simpleplayer.event.AudioStartPauseEvent;
import yanzhikai.simpleplayer.event.CurrentAudioDetailEvent;
import yanzhikai.simpleplayer.model.AudioInfo;
import yanzhikai.simpleplayer.model.PlayList;
import yanzhikai.simpleplayer.service.AudioPlayerService;
import yanzhikai.simpleplayer.ui.ScanActivity;

import static yanzhikai.simpleplayer.event.AudioEvent.AUDIO_PLAY_CHOSEN;

public class MainActivity extends FragmentActivity implements View.OnClickListener{
    public static final String TAG = "yjkMainActivity";
    private Button btn_pre, btn_play_pause, btn_next,btn_choose;
    private SeekBar sb_progress;
    private RecyclerView rv_play_list;
    private PlayListAdapter mPlayListAdapter;
    private boolean canUpdate = true;
    private LinearLayout ly_play_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        EventBus.getDefault().register(this);
        initView();

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateList();
    }

    private void initView() {
        btn_pre = findViewById(R.id.btn_pre);
        btn_play_pause = findViewById(R.id.btn_pause);
        btn_next = findViewById(R.id.btn_next);
        btn_choose = findViewById(R.id.btn_choose);
        sb_progress = findViewById(R.id.sb_progress);
        ly_play_list = findViewById(R.id.ly_play_list);

        btn_pre.setOnClickListener(this);
        btn_play_pause.setOnClickListener(this);
        btn_next.setOnClickListener(this);
        btn_choose.setOnClickListener(this);
        sb_progress.setMax(1000);
        sb_progress.setOnSeekBarChangeListener(new MyProgressListener());

        rv_play_list = findViewById(R.id.rv_play_list);
        rv_play_list.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        mPlayListAdapter = new PlayListAdapter(this, PlayList.getInstance().getAudioList());
        mPlayListAdapter.setListener(new MyPlayListListener());
        rv_play_list.setAdapter(mPlayListAdapter);
        rv_play_list.addItemDecoration(divider);

    }

    private void smaller(){
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) ly_play_list.getLayoutParams();
        ValueAnimator valueAnimator;
        if (layoutParams.width > 800) {
            valueAnimator = ValueAnimator.ofInt(1280, 600);
        }else {
            valueAnimator = ValueAnimator.ofInt(600, 1280);
        }
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) ly_play_list.getLayoutParams();
                layoutParams.width = (int) animation.getAnimatedValue();
                ly_play_list.setLayoutParams(layoutParams);

            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mPlayListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        valueAnimator.setDuration(1000);
        valueAnimator.start();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleUpdate(CurrentAudioDetailEvent event){
        updateProgress(event.progress);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleAudioChanged(AudioChangedEvent changedEvent){
        mPlayListAdapter.refreshItem();
        rv_play_list.smoothScrollToPosition(PlayList.getInstance().getCurrentIndex());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleAudioStartPause(AudioStartPauseEvent startPauseEvent){
        Log.d(TAG, "handleAudioStartPause: ");
        updateStartAndPause(startPauseEvent.getIsPause());
    }

    private void updateProgress(float progress){
        if (canUpdate) {
            sb_progress.setProgress((int) (progress * 1000));
        }
    }

    private void updateStartAndPause(boolean isPause){
        if (isPause){
            btn_play_pause.setText("PAUSE");
        }else {
            btn_play_pause.setText("START");
        }
    }
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void handleUIControl(UIControlEvent uiControlEvent){
//        switch (uiControlEvent.getType()){
//            case UIControlEvent.AUDIO_SEEK_COMPLETED:
//                canUpdate = true;
//                makeToast("canUpdate");
//                break;
//        }
//    }

    private void updateList(){
        Log.d(TAG, "updateList: ");
        PlayList.getInstance().add(LocalAudioDaoManager.getInstance().queryAllAudio());
//        PlayListAudioDaoManager.getInstance().insertMultiAudio(LocalAudioDaoManager.getInstance().queryAllAudio());
        mPlayListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_pre:
//                mAudioPlayer.start();

//                Log.d(TAG, "PlayList.getInstance().getAudioList(): " + PlayList.getInstance().getAudioList().size());
//                updateList();
                EventBus.getDefault().post(new AudioEvent(-1,AudioEvent.AUDIO_PRE));
                break;
            case R.id.btn_pause:
                if (AudioPlayerService.isPlaying) {
                    EventBus.getDefault().post(new AudioEvent(-1, AudioEvent.AUDIO_PAUSE));
                }else {
                    EventBus.getDefault().post(new AudioEvent(-1, AudioEvent.AUDIO_PLAY));
                }
                break;
            case R.id.btn_next:
//                mAudioPlayer.stop();
//                PlayingAudioInfo playingAudioInfo = new PlayingAudioInfo(PlayListAudioDaoManager.getInstance().queryAllAudio().get(0));
//                playingAudioInfo.setCurrentTime(0);
//                playingAudioInfo.setCurrentTimeText("00:00");
//                PlayListAudioDaoManager.getInstance().setPlayingAudio(playingAudioInfo);
                EventBus.getDefault().post(new AudioEvent(-1,AudioEvent.AUDIO_NEXT));
                break;
            case R.id.btn_choose:
//                mAudioPlayer.setPath("/storage/emulated/0/Music/陈奕迅 - 陀飞轮.mp3");
//                mAudioPlayer.prepareAsync();
//                startActivity(new Intent(this, ScanActivity.class));
                smaller();
                break;
        }
    }

    private class MyPlayListListener implements PlayListAdapter.PlayListItemOnClickListener {
        @Override
        public void onItemClick(AudioInfo info, int index) {
            Log.d(TAG, "onItemClick: " + info.getSongName());
            EventBus.getDefault().post(new AudioEvent(index,AUDIO_PLAY_CHOSEN));
        }
    }

    private class MyProgressListener implements SeekBar.OnSeekBarChangeListener{

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
//            canUpdate = false;
            AudioEvent audioEvent = new AudioEvent(-1,AudioEvent.AUDIO_SEEK_START);
            EventBus.getDefault().post(audioEvent);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            AudioEvent audioEvent = new AudioEvent(-1,AudioEvent.AUDIO_SEEK_TO);
            audioEvent.setProgress(sb_progress.getProgress() / 1000f);
            EventBus.getDefault().post(audioEvent);
//            canUpdate = true;
        }
    }

//    public void loadFragment(Fragment fragment, String tag, String name) {
//        Fragment newFragment = getSupportFragmentManager().findFragmentByTag(tag);
//        if (newFragment == null) {
//            FragmentTransaction transaction = getSupportFragmentManager()
//                    .beginTransaction()
//                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out)
//                    .add(R.id.content_layout, fragment, tag);
//            transaction.addToBackStack(name);
//            transaction.commit();
//        } else {
//            getSupportFragmentManager().popBackStack(ROOT_NAME, 0);
//        }
//    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        MyApplication.closeDB();
    }
}