package yanzhikai.simpleplayer.event;


/**
 * author : yany
 * e-mail : yanzhikai_yjk@qq.com
 * time   : 2018/01/30
 * desc   :
 */

public class AudioListChangedEvent {
    public static final int ITEM_ADDED = 1;

    private int mType = ITEM_ADDED;

    public AudioListChangedEvent(int type){
        this.mType = type;
    }

    public void setType(int Type) {
        this.mType = Type;
    }

    public int getType() {
        return mType;
    }
}
