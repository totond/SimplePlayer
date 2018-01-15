package yanzhikai.simpleplayer.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * author : yany
 * e-mail : yanzhikai_yjk@qq.com
 * time   : 2018/01/12
 * desc   :
 */

public class PlayList {
    public static final int PLAY_ORDER = 0;
    public static final int PLAY_RANDOM = 1;
    public static final int PLAY_SINGLE = 2;

    private int mPlayModel = PLAY_ORDER;
    private int mCurrentIndex = 0;
    private AudioInfo mCurrentAudio;


    private ArrayList<AudioInfo> mAudioList;

    private PlayList(){
        mAudioList = new ArrayList<>();
    }

    private static final PlayList instance = new PlayList();

    public static PlayList getInstance() {
        return instance;
    }

    public void add(AudioInfo... infos){
        mAudioList.addAll(Arrays.asList(infos));
    }

    public void add(int index, AudioInfo... infos){
        for (AudioInfo info : infos){
            mAudioList.add(index,info);
        }
    }

    public boolean remove(AudioInfo info){
        return mAudioList.remove(info);
    }

    public void remove(int index){
        mAudioList.remove(index);
    }

    public void clear(){
        mAudioList.clear();
    }

    public void update(){

    }

    public AudioInfo getNextAudio(){
        int nextIndex = 0;
        switch (mPlayModel){
            case PLAY_ORDER:
                nextIndex = mCurrentIndex + 1;
                if (nextIndex > mAudioList.size()){
                    mCurrentAudio = null;
                }else {
                    mCurrentAudio = mAudioList.get(nextIndex);
                }
                break;
            case PLAY_RANDOM:
                Random random = new Random();
                nextIndex = mCurrentIndex;
                while (nextIndex == mCurrentIndex){
                    nextIndex = random.nextInt(mAudioList.size());
                }
                mCurrentAudio = mAudioList.get(nextIndex);
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
        return mCurrentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
        this.mCurrentIndex = currentIndex;
    }
}
