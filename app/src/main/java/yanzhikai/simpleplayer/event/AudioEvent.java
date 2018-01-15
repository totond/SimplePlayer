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

    public int type = AUDIO_NULL;

    private AudioInfo audioInfo;

    public void setAudioInfo(AudioInfo audioInfo) {
        this.audioInfo = audioInfo;
    }

    public AudioInfo getAudioInfo() {
        return audioInfo;
    }
}
