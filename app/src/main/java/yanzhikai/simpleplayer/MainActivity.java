package yanzhikai.simpleplayer;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import yanzhikai.simpleplayer.base.BaseAnimatorListener;
import yanzhikai.simpleplayer.db.AudioListDaoManager;
import yanzhikai.simpleplayer.event.AudioEvent;
import yanzhikai.simpleplayer.event.AudioItemAddEvent;
import yanzhikai.simpleplayer.event.AudioListChangedEvent;
import yanzhikai.simpleplayer.event.AudioStartPauseEvent;
import yanzhikai.simpleplayer.event.CurrentAudioDetailEvent;
import yanzhikai.simpleplayer.event.ListSizeChangedEvent;
import yanzhikai.simpleplayer.event.LocalListChangedEvent;
import yanzhikai.simpleplayer.event.OpenAudioListEvent;
import yanzhikai.simpleplayer.event.OpenClockEvent;
import yanzhikai.simpleplayer.event.PlayListChangedEvent;
import yanzhikai.simpleplayer.event.ShowClockEvent;
import yanzhikai.simpleplayer.model.AudioInfo;
import yanzhikai.simpleplayer.model.AudioListInfo;
import yanzhikai.simpleplayer.model.PlayList;
import yanzhikai.simpleplayer.service.AudioPlayerService;
import yanzhikai.simpleplayer.ui.AlarmFragment;
import yanzhikai.simpleplayer.ui.AudioListFragment;
import yanzhikai.simpleplayer.ui.ClockFragment;
import yanzhikai.simpleplayer.ui.LocalAudioListFragment;
import yanzhikai.simpleplayer.ui.PlayListFragment;
import yanzhikai.simpleplayer.ui.ShowListsFragment;
import yanzhikai.simpleplayer.utils.EventUtil;
import yanzhikai.simpleplayer.utils.ScreenUtils;
import yanzhikai.simpleplayer.utils.ToastUtil;

public class MainActivity extends FragmentActivity implements View.OnClickListener {
    public static final String TAG = "yjkMainActivity";
    private static final String PLAY_LIST_FRAGMENT_TAG = "PlayListFragment";
    private static final String LOCAL_AUDIO_LIST_FRAGMENT_TAG = "LocalAudioListFragment";
    private static final String PLAY_LIST_FRAGMENT_NAME = "PlayList";
    private static final String SHOW_LIST_FRAGMENT_NAME = "ShowList";
    private static final String AUDIO_LIST_FRAGMENT_NAME = "AudioList";
    private static final String CLOCK_FRAGMENT_NAME = "Clock";
    private static final String ALARM_FRAGMENT_NAME = "Alarm";
    private static final String LEFT_FRAGMENT_TAG = "LeftFragment";
    private static final String RIGHT_FRAGMENT_TAG = "RightFragment";
    private static final int TIME_ANIMATION = 500;
    private ImageView btn_pre, btn_play_pause, btn_next;
    private Button btn_choose;
    private SeekBar sb_progress;
    private boolean canUpdate = true;

    //    private RecyclerView rv_play_list;
//    private PlayListAdapter mPlayListAdapter;
    private LinearLayout ly_left_list;
    private RelativeLayout ly_right_list;
    private int screenWidth, screenHeight, playListWidth, subListWidth;

    private ViewGroup ly_local_btn, ly_audios_btn, ly_timer_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        EventBus.getDefault().register(this);
        initView();
        initScreen();
        loadLeftFragment(new PlayListFragment());

    }

    @Override
    protected void onResume() {
        super.onResume();
//        updateList();

    }

    private void initView() {
        btn_pre = findViewById(R.id.btn_pre);
        btn_play_pause = findViewById(R.id.btn_play_pause);
        btn_next = findViewById(R.id.btn_next);
        btn_choose = findViewById(R.id.btn_choose);
        sb_progress = findViewById(R.id.sb_progress);
        ly_left_list = findViewById(R.id.ly_left_list);
        ly_right_list = findViewById(R.id.ly_right_list);

        ly_local_btn = findViewById(R.id.ly_local_btn);
        ly_audios_btn = findViewById(R.id.ly_audios_btn);
        ly_timer_btn = findViewById(R.id.ly_timer_btn);

        btn_pre.setOnClickListener(this);
        btn_play_pause.setOnClickListener(this);
        btn_next.setOnClickListener(this);
        btn_choose.setOnClickListener(this);

        ly_local_btn.setOnClickListener(this);
        ly_audios_btn.setOnClickListener(this);
        ly_timer_btn.setOnClickListener(this);

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

    private void initScreen() {
        screenWidth = ScreenUtils.getScreenWidth(this);
        screenHeight = ScreenUtils.getScreenHeight(this);
        //要求ly_play_list和ly_sub_list的宽度是具体值
        playListWidth = ly_left_list.getLayoutParams().width;
        subListWidth = ly_right_list.getLayoutParams().width;
        Log.d(TAG, "playListWidth: " + playListWidth);
        Log.d(TAG, "screenWidth: " + screenWidth);
    }

    public void openOrCloseRightFragment() {
        Log.d(TAG, "playListWidth: " + playListWidth);
        Log.d(TAG, "screenWidth: " + screenWidth);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) ly_left_list.getLayoutParams();
        ValueAnimator valueAnimator;
        if (layoutParams.width > screenWidth / 2) {
            openRight();
        } else {
            closeRight();
        }

    }

    private void closeRight(){
        ValueAnimator valueAnimator = ValueAnimator.ofInt(screenWidth / 2, playListWidth);
        removeFragment(RIGHT_FRAGMENT_TAG);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) ly_left_list.getLayoutParams();
                layoutParams.width = (int) animation.getAnimatedValue();
                ly_left_list.setLayoutParams(layoutParams);

                RelativeLayout.LayoutParams subLayoutParams = (RelativeLayout.LayoutParams) ly_right_list.getLayoutParams();
                subLayoutParams.width = screenWidth - (int) animation.getAnimatedValue();
                ly_right_list.setLayoutParams(subLayoutParams);

            }
        });
        valueAnimator.addListener(new BaseAnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                ly_audios_btn.setVisibility(View.VISIBLE);
                ly_local_btn.setVisibility(View.VISIBLE);
                ly_timer_btn.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                EventUtil.post(new ListSizeChangedEvent());
            }
        });
        valueAnimator.setDuration(TIME_ANIMATION);
        valueAnimator.start();
    }

    private void openRight(){
        ValueAnimator valueAnimator = ValueAnimator.ofInt(playListWidth, screenWidth / 2);
        loadRightFragment(new LocalAudioListFragment());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) ly_left_list.getLayoutParams();
                layoutParams.width = (int) animation.getAnimatedValue();
                ly_left_list.setLayoutParams(layoutParams);

                RelativeLayout.LayoutParams subLayoutParams = (RelativeLayout.LayoutParams) ly_right_list.getLayoutParams();
                subLayoutParams.width = screenWidth - (int) animation.getAnimatedValue();
                ly_right_list.setLayoutParams(subLayoutParams);

            }
        });
        valueAnimator.addListener(new BaseAnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                ly_audios_btn.setVisibility(View.INVISIBLE);
                ly_local_btn.setVisibility(View.INVISIBLE);
                ly_timer_btn.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                EventUtil.post(new ListSizeChangedEvent());
            }
        });
        valueAnimator.setDuration(TIME_ANIMATION);
        valueAnimator.start();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleUpdate(CurrentAudioDetailEvent event) {
        updateProgress(event.progress);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleOpenList(OpenAudioListEvent openAudioListEvent) {
        loadLeftFragmentWithStack(AudioListFragment.newInstance(openAudioListEvent.getListName()), AUDIO_LIST_FRAGMENT_NAME);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleOpenClock(OpenClockEvent openClockEvent){
        loadLeftFragmentWithStack(ClockFragment.newInstance(),CLOCK_FRAGMENT_NAME);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleAudioStartPause(AudioStartPauseEvent startPauseEvent) {
        Log.d(TAG, "handleAudioStartPause: ");
        updateStartAndPause(startPauseEvent.getIsPause());
    }

    @Subscribe
    public void handleAudioItemAdd(AudioItemAddEvent audioItemAddEvent) {
        Log.d(TAG, "handleAudioItemAdd: size" + audioItemAddEvent.getInfoList().size());
        Fragment currentLeftFragment = getSupportFragmentManager().findFragmentByTag(LEFT_FRAGMENT_TAG);
        if (currentLeftFragment instanceof PlayListFragment) {
            if (!PlayList.getInstance().add(audioItemAddEvent.getInfoList())) {
                ToastUtil.makeShortToast(this, "加入了除去列表中已经存在的歌曲");
                Log.d(TAG, "handleAudioItemAdd: PlayListFragment failed");
            }
            EventUtil.post(new PlayListChangedEvent(PlayListChangedEvent.ITEM_ADDED));
        } else if (currentLeftFragment instanceof AudioListFragment) {
            if (!AudioListDaoManager.getInstance().
                    insertAudiosToList(
                            audioItemAddEvent.getInfoList(),
                            AudioListDaoManager.getInstance().
                                    queryAudioListByName(((AudioListFragment) currentLeftFragment).getListName())
                    )) {
                ToastUtil.makeShortToast(this, "加入了除去列表中已经存在的歌曲");
                Log.d(TAG, "handleAudioItemAdd: AudioListFragment failed");
            }
            EventUtil.post(new AudioListChangedEvent(AudioListChangedEvent.ITEM_ADDED));
        } else {
            ToastUtil.makeShortToast(this, "当前左边列表不可以加入歌曲");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handlerClockShow(ShowClockEvent showClockEvent){
        Log.d(TAG, "handlerClockShow: ");
        showClockDialog(showClockEvent.msg);
        ToastUtil.makeShortToast(this,showClockEvent.msg);
    }

    private void updateProgress(float progress) {
        if (canUpdate) {
            sb_progress.setProgress((int) (progress * 1000));
        }
    }

    private void updateStartAndPause(boolean isPause) {
        if (isPause) {
            btn_play_pause.setImageResource(R.mipmap.pause);
        } else {
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
        switch (v.getId()) {
            case R.id.btn_pre:
//                mAudioPlayer.start();

//                Log.d(TAG, "PlayList.getInstance().getAudioList(): " + PlayList.getInstance().getAudioList().size());
//                updateList();
                EventBus.getDefault().post(new AudioEvent( AudioEvent.AUDIO_PRE));
                break;
            case R.id.btn_play_pause:
                if (AudioPlayerService.isPlaying) {
                    EventBus.getDefault().post(new AudioEvent(AudioEvent.AUDIO_PAUSE));
                } else {
                    EventBus.getDefault().post(new AudioEvent(AudioEvent.AUDIO_PLAY));
                }
                break;
            case R.id.btn_next:
//                mAudioPlayer.stop();
//                PlayingAudioInfo playingAudioInfo = new PlayingAudioInfo(PlayListAudioDaoManager.getInstance().queryAllAudio().get(0));
//                playingAudioInfo.setCurrentTime(0);
//                playingAudioInfo.setCurrentTimeText("00:00");
//                PlayListAudioDaoManager.getInstance().setPlayingAudio(playingAudioInfo);
                EventBus.getDefault().post(new AudioEvent(AudioEvent.AUDIO_NEXT));
                break;
            case R.id.btn_choose:
//                mAudioPlayer.setPath("/storage/emulated/0/Music/陈奕迅 - 陀飞轮.mp3");
//                mAudioPlayer.prepareAsync();
//                startActivity(new Intent(this, ScanActivity.class));
//                openOrCloseRightFragment();
                openOrCloseRightFragment();

                break;
            case R.id.ly_local_btn:
                openOrCloseRightFragment();
                break;
            case R.id.ly_audios_btn:
                openOrCloseRightFragment();
                loadLeftFragmentWithStack(new ShowListsFragment(), SHOW_LIST_FRAGMENT_NAME);
                break;
            case R.id.ly_timer_btn:
                loadLeftFragmentWithStack(AlarmFragment.newInstance(),ALARM_FRAGMENT_NAME);
                break;
        }
    }

    private void testAudioList() {
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

    public void showClockDialog(String msg){
        AlertDialog.Builder saveDialogBuilder = new AlertDialog.Builder(this);
        saveDialogBuilder
                .setTitle(msg)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setCancelable(false)
                .show();
    }


    private class MyProgressListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
//            canUpdate = false;
            AudioEvent audioEvent = new AudioEvent(AudioEvent.AUDIO_SEEK_START);
            EventBus.getDefault().post(audioEvent);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            AudioEvent audioEvent = new AudioEvent(AudioEvent.AUDIO_SEEK_TO);
            audioEvent.setProgress(sb_progress.getProgress() / 1000f);
            EventBus.getDefault().post(audioEvent);
//            canUpdate = true;
        }
    }

    public void loadRightFragment(Fragment fragment) {
        Fragment oldFragment = getSupportFragmentManager().findFragmentByTag(RIGHT_FRAGMENT_TAG);
        if (oldFragment != null) {
            removeFragment(RIGHT_FRAGMENT_TAG);
        }
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.silde_from_right, R.anim.silde_to_right, R.anim.silde_from_right, R.anim.silde_to_right)
                .replace(R.id.ly_right_list, fragment, RIGHT_FRAGMENT_TAG);
//            transaction.addToBackStack(PLAY_LIST_FRAGMENT_NAME);
        transaction.commit();
//            getSupportFragmentManager().popBackStack(LOCAL_AUDIO_LIST_FRAGMENT_TAG, 0);
    }

    public void removeFragment(String tag) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment != null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.remove(fragment);
            fragmentTransaction.commit();
        }
    }

    public void popFragment() {

        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            Log.d(TAG, "popFragment: 0");
            getSupportFragmentManager().popBackStack();
        }
    }

    public void popAllFragment(){
        while (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            Log.d(TAG, "popFragment: 0");
            getSupportFragmentManager().popBackStack();
        }
    }

    public void loadLeftFragment(Fragment fragment) {
        Fragment newFragment = getSupportFragmentManager().findFragmentByTag(LEFT_FRAGMENT_TAG);
        if (newFragment != null) {
            removeFragment(LEFT_FRAGMENT_TAG);
        }
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                .replace(R.id.ly_left_list, fragment, LEFT_FRAGMENT_TAG);
//            transaction.addToBackStack(PLAY_LIST_FRAGMENT_NAME);
        transaction.commit();
//        else {
//            getSupportFragmentManager().popBackStack(PLAY_LIST_FRAGMENT_NAME, 0);
//        }
    }

    public void loadLeftFragmentWithStack(Fragment fragment, String name) {
        Fragment newFragment = getSupportFragmentManager().findFragmentByTag(LEFT_FRAGMENT_TAG);
        if (newFragment != null) {
            getSupportFragmentManager().popBackStack(name, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.silde_from_right,R.anim.slide_to_left, R.anim.silde_from_right,R.anim.slide_to_left)
                .replace(R.id.ly_left_list, fragment, LEFT_FRAGMENT_TAG);
        transaction.addToBackStack(name);
        transaction.commit();
//        else {
//            getSupportFragmentManager().popBackStack(PLAY_LIST_FRAGMENT_NAME, 0);
//        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        MyApplication.closeDB();
    }
}