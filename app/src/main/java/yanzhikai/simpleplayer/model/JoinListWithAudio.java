package yanzhikai.simpleplayer.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * author : yany
 * e-mail : yanzhikai_yjk@qq.com
 * time   : 2018/01/24
 * desc   :
 */

@Entity
public class JoinListWithAudio {
    @Id
    private String joinMD5;
    private String  listName;
    private String  audioHash;
    @Generated(hash = 1619268514)
    public JoinListWithAudio(String joinMD5, String listName, String audioHash) {
        this.joinMD5 = joinMD5;
        this.listName = listName;
        this.audioHash = audioHash;
    }
    @Generated(hash = 1511633278)
    public JoinListWithAudio() {
    }
    public String getJoinMD5() {
        return this.joinMD5;
    }
    public void setJoinMD5(String joinMD5) {
        this.joinMD5 = joinMD5;
    }
    public String getListName() {
        return this.listName;
    }
    public void setListName(String listName) {
        this.listName = listName;
    }
    public String getAudioHash() {
        return this.audioHash;
    }
    public void setAudioHash(String audioHash) {
        this.audioHash = audioHash;
    }




}
