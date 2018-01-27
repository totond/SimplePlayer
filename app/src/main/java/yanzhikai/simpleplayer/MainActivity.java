package yanzhikai.simpleplayer;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import yanzhikai.simpleplayer.db.AudioListDaoManager;
import yanzhikai.simpleplayer.event.AudioEvent;
import yanzhikai.simpleplayer.event.AudioStartPauseEvent;
import yanzhikai.simpleplayer.event.CurrentAudioDetailEvent;
import yanzhikai.simpleplayer.model.AudioInfo;
import yanzhikai.simpleplayer.model.AudioListInfo;
import yanzhikai.simpleplayer.service.AudioPlayerService;
import yanzhikai.simpleplayer.ui.LocalAudioListFragment;
import yanzhikai.simpleplayer.ui.PlayListFragment;
import yanzhikai.simpleplayer.utils.ScreenUtils;

public class MainActivity extends FragmentActivity implements View.OnClickListener{
    public static final String TAG = "yjkMainActivity";
    private static final String PLAY_LIST_FRAGMENT_TAG = "PlayListFragment";
    private static final String LOCAL_AUDIO_LIST_FRAGMENT_TAG = "LocalAudioListFragment";
    private static final String PLAY_LIST_FRAGMENT_NAME = "PlayList";
    private ImageView btn_pre, btn_play_pause, btn_next;
    private Button btn_choose;
    private SeekBar sb_progress;
    private boolean canUpdate = true;
//    private RecyclerView rv_play_list;
//    private PlayListAdapter mPlayListAdapter;
    private LinearLayout ly_play_list;
    private RelativeLayout ly_sub_list;
    private int screenWidth, screenHeight, playListWidth, subListWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        EventBus.getDefault().register(this);
        initView();
        initScreen();
        loadPlayListFragment();

    }

    @Override
    protected void onResume() {
        super.onResume();
//        updateList();

    }

    private void initView() {
        btn_pre = findViewById(R.id.btn_pre);
        btn_play_pause = findViewById(R.id.btn_pause);
        btn_next = findViewById(R.id.btn_next);
        btn_choose = findViewById(R.id.btn_choose);
        sb_progress = findViewById(R.id.sb_progress);
        ly_play_list = findViewById(R.id.ly_play_list);
        ly_sub_list = findViewById(R.id.ly_sub_list);

        btn_pre.setOnClickListener(this);
        btn_play_pause.setOnClickListener(this);
        btn_next.setOnClickListener(this);
        btn_choose.setOnClickListener(this);
        sb_progress.setMax(1000);
        sb_progress.setOnSeekBarChangeListener(new MyProgressListener());

//        rv_play_list = findViewById(R.id.rv_play_list);
//        rv_play_list.setLayoutManager(new LinearLayoutManager(this));
//        DividerItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
//        mPlayListAdapter = new PlayListAdapter(this, PlayList.getInstance().getAudioList());
//        mPlayListAdapter.setOnClickListener(new MyPlayListListener());
//        rv_play_list.setAdapter(mPlayListAdapter);
//        rv_play_list.addItemDecoration(divider);
//        testAudioList();
    }

    private void initScreen(){
        screenWidth = ScreenUtils.getScreenWidth(this);
        screenHeight = ScreenUtils.getScreenHeight(this);
        //要求ly_play_list和ly_sub_list的宽度是具体值
        playListWidth = ly_play_list.getLayoutParams().width;
        subListWidth = ly_sub_list.getLayoutParams().width;
        Log.d(TAG, "playListWidth: " + playListWidth);
        Log.d(TAG, "screenWidth: " + screenWidth);
    }

    private void smaller() {
        Log.d(TAG, "playListWidth: " + playListWidth);
        Log.d(TAG, "screenWidth: " + screenWidth);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) ly_play_list.getLayoutParams();
        ValueAnimator valueAnimator;
        if (layoutParams.width > screenWidth / 2) {
            valueAnimator = ValueAnimator.ofInt(playListWidth, screenWidth / 2);
            loadLocalAudioListFragment();
        } else {
            valueAnimator = ValueAnimator.ofInt(screenWidth / 2, playListWidth);
            removeLocalAudioListFragment();
        }
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) ly_play_list.getLayoutParams();
                layoutParams.width = (int) animation.getAnimatedValue();
                ly_play_list.setLayoutParams(layoutParams);

                RelativeLayout.LayoutParams subLayoutParams = (RelativeLayout.LayoutParams) ly_sub_list.getLayoutParams();
                subLayoutParams.width = screenWidth - (int) animation.getAnimatedValue();
                ly_sub_list.setLayoutParams(subLayoutParams);

            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
//                mPlayListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        valueAnimator.setDuration(500);
        valueAnimator.start();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleUpdate(CurrentAudioDetailEvent event){
        updateProgress(event.progress);
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void handleAudioChanged(AudioChangedEventPlay changedEvent){
//        mPlayListAdapter.refreshItem();
//        rv_play_list.smoothScrollToPosition(PlayList.getInstance().getCurrentIndex());
//    }

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
            btn_play_pause.setImageResource(R.mipmap.pause);
        }else {
            btn_play_pause.setImageResource(R.mipmap.play);
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

//    private void updateList(){
//        Log.d(TAG, "updateList: ");
//        PlayList.getInstance().add(LocalAudioDaoManager.getInstance().queryAllAudio());
////        PlayListAudioDaoManager.getInstance().insertMultiAudio(LocalAudioDaoManager.getInstance().queryAllAudio());
//        mPlayListAdapter.notifyDataSetChanged();
//    }

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

    private void testAudioList(){
//        AudioListDaoManager.getInstance().init(this);

        //新建
        AudioListInfo audioListInfo = new AudioListInfo();
        audioListInfo.setListName("TestList1");
//        AudioListDaoManager.getInstance().insertList(audioListInfo);

        //修改
//        AudioListInfo audioListInfo1 = AudioListDaoManager.getInstance().queryAudioListByQueryBuilder("TestList1").get(0);
//        audioListInfo1.getInfoList().addAll(LocalAudioDaoManager.getInstance().queryAllAudio());
//        Log.d(TAG, "testAudioList: pre size:" + audioListInfo1.getInfoList().size());
//        audioListInfo1.update();

        //查询
        List<AudioInfo> audioInfos = AudioListDaoManager.getInstance().queryAudioListByQueryBuilder("TestList1").get(0).getInfoList();
        Log.d(TAG, "testAudioList:size " + AudioListDaoManager.getInstance().queryAudioListByQueryBuilder("TestList1").size());
        Log.d(TAG, "testAudioList:result " + audioInfos.size());
//        ToastUtil.makeShortToast(this,audioInfos.get(0).getSongName());

    }

//    private class MyPlayListListener implements PlayListAdapter.PlayListItemOnClickListener {
//        @Override
//        public void onItemClick(AudioInfo info, int index) {
//            Log.d(TAG, "onItemClick: " + info.getSongName());
//            EventBus.getDefault().post(new AudioEvent(index,AUDIO_PLAY_CHOSEN));
//        }
//    }

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

    public void loadLocalAudioListFragment() {
        Fragment newFragment = getSupportFragmentManager().findFragmentByTag(LOCAL_AUDIO_LIST_FRAGMENT_TAG);
        if (newFragment == null) {
            Fragment fragment = new LocalAudioListFragment();
            FragmentTransaction transaction = getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out)
                    .replace(R.id.ly_sub_list, fragment, LOCAL_AUDIO_LIST_FRAGMENT_TAG);
//            transaction.addToBackStack(PLAY_LIST_FRAGMENT_NAME);
            transaction.commit();
        } else {
            getSupportFragmentManager().popBackStack(LOCAL_AUDIO_LIST_FRAGMENT_TAG, 0);
        }
    }

    public void removeLocalAudioListFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(LOCAL_AUDIO_LIST_FRAGMENT_TAG);
        if (fragment != null){
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.remove(fragment);
            fragmentTransaction.commit();
        }
    }

    public void loadPlayListFragment() {
        Fragment newFragment = getSupportFragmentManager().findFragmentByTag(PLAY_LIST_FRAGMENT_TAG);
        if (newFragment == null) {
            Fragment fragment = new PlayListFragment();
            FragmentTransaction transaction = getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out)
                    .replace(R.id.ly_play_list, fragment, PLAY_LIST_FRAGMENT_TAG);
//            transaction.addToBackStack(PLAY_LIST_FRAGMENT_NAME);
            transaction.commit();
        } else {
            getSupportFragmentManager().popBackStack(PLAY_LIST_FRAGMENT_NAME, 0);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        MyApplication.closeDB();
    }
}