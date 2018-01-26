package yanzhikai.simpleplayer.event;

import android.util.Log;

/**
 * author : yany
 * e-mail : yanzhikai_yjk@qq.com
 * time   : 2018/01/26
 * desc   :
 */

public class PlayListChangedEvent {
    public static final int CURRENT_AUDIO_CHANGED = 0;
    public static final int ITEM_ADDED = 1;

    private int mType = CURRENT_AUDIO_CHANGED;

    public PlayListChangedEvent(int type){
        this.mType = type;
        Log.d("yjk", "PlayListChangedEvent: " + type);
    }

    public void setType(int Type) {
        this.mType = Type;
    }

    public int getType() {
        return mType;
    }
}
