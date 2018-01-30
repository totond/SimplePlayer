package yanzhikai.simpleplayer.model;

import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import yanzhikai.simpleplayer.db.PlayListAudioDaoManager;
import yanzhikai.simpleplayer.event.AudioEvent;

/**
 * author : yany
 * e-mail : yanzhikai_yjk@qq.com
 * time   : 2018/01/12
 * desc   :
 */

public class PlayList {
    public static final String TAG = "yjkPlayList";
    public static final int PLAY_ORDER = 0;
    public static final int PLAY_RANDOM = 1;
    public static final int PLAY_SINGLE = 2;

    private int mPlayModel = PLAY_ORDER;
    private int mCurrentIndex = -1;
    private AudioInfo mCurrentAudio;



    private ArrayList<AudioInfo> mAudioList;

    private PlayList() {
        initData();
    }

    private void initData() {
        mAudioList = new ArrayList<>(PlayListAudioDaoManager.getInstance().queryAllAudio());
        if (PlayListAudioDaoManager.getInstance().queryPlayingAudio().size() > 0) {
            setCurrentIndex(PlayListAudioDaoManager.getInstance().queryPlayingAudio().get(0).getIndex());
        }
    }

    private static final PlayList instance = new PlayList();

    public static PlayList getInstance() {
        return instance;
    }

    public boolean add(AudioInfo info) {
        if (PlayListAudioDaoManager.getInstance().insertAudio(info)) {
            mAudioList.add(info);
            return true;
        }
        return false;
    }

//    public void add(int index, AudioInfo... infos){
//        for (AudioInfo info : infos){
//            mAudioList.add(index,info);
//        }
//    }

    public boolean add(List<AudioInfo> infos) {
        boolean flag = true;
        for (AudioInfo info : infos) {
//            if (!isExist(info)) {
                if (PlayListAudioDaoManager.getInstance().insertAudio(info)) {
                    mAudioList.add(info);
                    Log.d(TAG, "add: not exist");
                    flag &= true;
                }else {
                    flag &= false;
                }
//            }
        }

        return flag;

    }

    public boolean remove(AudioInfo info) {
        if (isExist(info)) {
            PlayListAudioDaoManager.getInstance().deleteAudio(info);
            return mAudioList.remove(info);
        }
        return false;
    }

    public void remove(int index) {
        AudioInfo audioInfo = mAudioList.get(index);
        if (!isExist(audioInfo)) {
            PlayListAudioDaoManager.getInstance().deleteAudio(audioInfo);
            mAudioList.remove(index);
        }
    }

    public void clear() {
        mAudioList.clear();
        PlayListAudioDaoManager.getInstance().deleteAll();
        EventBus.getDefault().post(new AudioEvent(AudioEvent.AUDIO_NULL));
    }

    public void noMusic(){
        mCurrentIndex = -1;
        mCurrentAudio = null;
    }

    public Boolean isExist(AudioInfo info) {
        return PlayListAudioDaoManager.getInstance().isExist(info.getAudioHash());
    }

    //设置当前歌曲信息
    public void setCurrentAudio(AudioInfo audioInfo,int index) {
        if (audioInfo != null) {
            PlayingAudioInfo playingAudioInfo = new PlayingAudioInfo(audioInfo);
            playingAudioInfo.setCurrentTime(0);
            playingAudioInfo.setCurrentTimeText("00:00");
            playingAudioInfo.setIndex(index);
            PlayListAudioDaoManager.getInstance().setPlayingAudio(playingAudioInfo);
        }
        mCurrentIndex = index;
        mCurrentAudio = audioInfo;

    }

    public AudioInfo getNextAudio(boolean isPre) {
        if (mAudioList.size() == 0){
            return null;
        }
        int nextIndex = 0;
        switch (mPlayModel) {
            case PLAY_ORDER:
                if (!isPre) {
                    nextIndex = mCurrentIndex + 1;
                    if (nextIndex >= mAudioList.size()) {
                        setCurrentAudio(mAudioList.get(0),0);
                    } else {
                        setCurrentAudio(mAudioList.get(nextIndex),nextIndex);
                    }
                } else {
                    nextIndex = mCurrentIndex - 1;
                    if (nextIndex >= 0) {
                        setCurrentAudio(mAudioList.get(nextIndex),nextIndex);
                    } else {
                        setCurrentAudio(mAudioList.get(mAudioList.size() - 1),mAudioList.size() - 1);
                    }
                }
                break;
            case PLAY_RANDOM:
                Random random = new Random();
                nextIndex = mCurrentIndex;
                while (nextIndex == mCurrentIndex) {
                    nextIndex = random.nextInt(mAudioList.size());
                }
                setCurrentAudio(mAudioList.get(nextIndex),nextIndex);
                break;
            case PLAY_SINGLE:
                mCurrentAudio = mAudioList.get(mCurrentIndex);
                break;
        }
        return mCurrentAudio;
    }

    public ArrayList<AudioInfo> getAudioList() {
        return mAudioList;
    }

    public int getPlayModel() {
        return mPlayModel;
    }

    public void setPlayModel(int playModel) {
        this.mPlayModel = playModel;
    }

    public int getCurrentIndex() {
        if (mCurrentAudio == null){
            mCurrentIndex = -1;
        }else {
            mCurrentIndex = mAudioList.indexOf(mCurrentAudio);
        }

        return mCurrentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
        this.mCurrentIndex = currentIndex;
    }

    public AudioInfo getCurrentAudio(boolean canNull) {
        if (mCurrentAudio == null && mAudioList.size() > 0 && !canNull){
            setCurrentAudio(mAudioList.get(0),0);
        }
        return mCurrentAudio;
    }
}
