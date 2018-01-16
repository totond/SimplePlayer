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

    private int mType = AUDIO_NULL;

    private AudioInfo mInfo;

    public AudioEvent(AudioInfo info, int type){
        mInfo = info;
        mType = type;
    }

    public void setInfo(AudioInfo info) {
        this.mInfo = info;
    }

    public AudioInfo getInfo() {
        return mInfo;
    }

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        this.mType = type;
    }
}
