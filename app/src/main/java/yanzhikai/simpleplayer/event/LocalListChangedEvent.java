package yanzhikai.simpleplayer.event;

/**
 * author : yany
 * e-mail : yanzhikai_yjk@qq.com
 * time   : 2018/01/27
 * desc   :
 */

public class LocalListChangedEvent {
    public static final int SEARCH_FINISH = 0;
    public static final int AUDIO_ADDED = 1;

    private int mType = SEARCH_FINISH;

    public LocalListChangedEvent(int type){
        this.mType = type;
    }

    public void setType(int Type) {
        this.mType = Type;
    }

    public int getType() {
        return mType;
    }
}
