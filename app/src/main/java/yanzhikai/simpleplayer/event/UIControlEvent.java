package yanzhikai.simpleplayer.event;

/**
 * author : yany
 * e-mail : yanzhikai_yjk@qq.com
 * time   : 2018/01/17
 * desc   :
 */

public class UIControlEvent {
    public static final int AUDIO_SEEK_COMPLETED = 0;
    private int mType;

    public UIControlEvent(int type){
        mType = type;
    }

    public void setType(int type) {
        this.mType = type;
    }

    public int getType() {
        return mType;
    }
}
