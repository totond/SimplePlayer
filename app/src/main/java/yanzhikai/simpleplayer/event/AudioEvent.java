package yanzhikai.simpleplayer.event;

import yanzhikai.simpleplayer.model.AudioInfo;

/**
 * author : yany
 * e-mail : yanzhikai_yjk@qq.com
 * time   : 2018/01/11
 * desc   :
 */

public class AudioEvent {
    public static final int AUDIO_NULL = 0;
    public static final int AUDIO_PLAY = 1;
    public static final int AUDIO_PAUSE = 2;
    public static final int AUDIO_PRE = 3;
    public static final int AUDIO_NEXT = 4;
    public static final int AUDIO_PLAY_CHOSEN = 5;
    public static final int AUDIO_SEEK_START = 6;
    public static final int AUDIO_SEEK_TO = 7;

    private int mType = AUDIO_NULL;

    private AudioInfo mInfo;
    private int mAudioIndex = -1;

    private float mProgress;

    public AudioEvent(int type){
        mType = type;
    }

    public void setInfo(AudioInfo info) {
        this.mInfo = info;
    }

    public AudioInfo getInfo() {
        return mInfo;
    }

    public void setAudioIndex(int audioIndex) {
        this.mAudioIndex = audioIndex;
    }

    public int getAudioIndex() {
        return mAudioIndex;
    }


    public int getType() {
        return mType;
    }

    public void setType(int type) {
        this.mType = type;
    }

    public void setProgress(float progress) {
        this.mProgress = progress;
    }

    public float getProgress() {
        return mProgress;
    }
}
