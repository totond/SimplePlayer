package yanzhikai.simpleplayer.event;

/**
 * author : yany
 * e-mail : yanzhikai_yjk@qq.com
 * time   : 2018/01/18
 * desc   :
 */

public class AudioStartPauseEvent {
    private boolean mIsPause = false;
    public AudioStartPauseEvent(boolean isPause){
        mIsPause = isPause;
    }

    public boolean getIsPause(){
        return mIsPause;
    }

}
