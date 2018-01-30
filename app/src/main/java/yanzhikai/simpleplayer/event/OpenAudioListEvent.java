package yanzhikai.simpleplayer.event;

/**
 * author : yany
 * e-mail : yanzhikai_yjk@qq.com
 * time   : 2018/01/30
 * desc   :
 */

public class OpenAudioListEvent {
    private String mListName;

    public OpenAudioListEvent(String listName){
        mListName = listName;
    }

    public String getListName() {
        return mListName;
    }
}
