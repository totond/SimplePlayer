package yanzhikai.simpleplayer.event;

import java.util.List;

import yanzhikai.simpleplayer.model.AudioInfo;

/**
 * author : yany
 * e-mail : yanzhikai_yjk@qq.com
 * time   : 2018/01/30
 * desc   :
 */

public class AudioItemAddEvent {
    private List<AudioInfo> mInfoList;

    public AudioItemAddEvent(List<AudioInfo> infoList){
        mInfoList = infoList;
    }

    public List<AudioInfo> getInfoList() {
        return mInfoList;
    }
}
