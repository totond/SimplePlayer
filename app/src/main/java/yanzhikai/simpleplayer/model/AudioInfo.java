package yanzhikai.simpleplayer.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.JoinEntity;
import org.greenrobot.greendao.annotation.ToMany;
import org.greenrobot.greendao.annotation.Unique;

import java.io.Serializable;
import java.util.List;

import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;
import com.jian.greendao.gen.DaoSession;
import com.jian.greendao.gen.AudioListInfoDao;
import com.jian.greendao.gen.AudioInfoDao;

/**
 * Created by zhangliangming on 2017/8/4.
 */
@Entity(nameInDb = "AudioInfo")
public class AudioInfo implements Serializable {

    public static final String KEY = "com.zlm.hp.ai.key";

    private static final long serialVersionUID = 42L;

    /**
     * 状态
     */
    public static final int FINISH = 0;
    public static final int DOWNLOADING = 1;
    public static final int INIT = 2;
    /**
     * 类型
     */
    public static final int LOCAL = 0;
    public static final int DOWNLOAD = 1;
    public static final int NET = 2;
    //最近-本地
    public static final int RECENT_LOCAL = 3;
    //最近-网络
    public static final int RECENT_NET = 4;

    /**
     * 喜欢（网络-本地）
     */
    public static final int LIKE_LOCAL = 5;
    public static final int LIKE_NET = 6;

    private int index;

    /**
     * 歌曲名称
     */
    private String songName;
    /**
     * 歌手名称
     */
    private String singerName;
    /**
     *
     */
    @Id
    private String audioHash;
    /**
     * 歌曲后缀名
     */
    private String fileExt;
    /**
     * 文件大小
     */
    private long fileSize;
    private String fileSizeText;
    /**
     * 文件路径
     */
    private String filePath;
    /**
     * 时长
     */
    private long duration;
    private String durationText;

    /**
     * 文件下载路径
     */
    private String downloadUrl;

    /**
     * 添加时间
     */
    private String createTime;
    /**
     * 状态：0是完成，1是未完成
     */
    private int status = FINISH;

    /**
     * 类型
     */
    private int type = LOCAL;
    /**
     * 分类索引
     */
    private String category;
    private String childCategory;

    @ToMany
    @JoinEntity(
            entity = JoinListWithAudio.class,
            sourceProperty = "audioHash",
            targetProperty = "listName"
    )
    private List<AudioListInfo> audioListInfoList;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 756689202)
    private transient AudioInfoDao myDao;

    @Generated(hash = 264410229)
    public AudioInfo(int index, String songName, String singerName,
            String audioHash, String fileExt, long fileSize, String fileSizeText,
            String filePath, long duration, String durationText, String downloadUrl,
            String createTime, int status, int type, String category,
            String childCategory) {
        this.index = index;
        this.songName = songName;
        this.singerName = singerName;
        this.audioHash = audioHash;
        this.fileExt = fileExt;
        this.fileSize = fileSize;
        this.fileSizeText = fileSizeText;
        this.filePath = filePath;
        this.duration = duration;
        this.durationText = durationText;
        this.downloadUrl = downloadUrl;
        this.createTime = createTime;
        this.status = status;
        this.type = type;
        this.category = category;
        this.childCategory = childCategory;
    }

    @Generated(hash = 2083697945)
    public AudioInfo() {
    }

    public int getIndex() {
        return this.index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getSongName() {
        return this.songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getSingerName() {
        return this.singerName;
    }

    public void setSingerName(String singerName) {
        this.singerName = singerName;
    }

    public String getAudioHash() {
        return this.audioHash;
    }

    public void setAudioHash(String audioHash) {
        this.audioHash = audioHash;
    }

    public String getFileExt() {
        return this.fileExt;
    }

    public void setFileExt(String fileExt) {
        this.fileExt = fileExt;
    }

    public long getFileSize() {
        return this.fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileSizeText() {
        return this.fileSizeText;
    }

    public void setFileSizeText(String fileSizeText) {
        this.fileSizeText = fileSizeText;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getDuration() {
        return this.duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getDurationText() {
        return this.durationText;
    }

    public void setDurationText(String durationText) {
        this.durationText = durationText;
    }

    public String getDownloadUrl() {
        return this.downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getChildCategory() {
        return this.childCategory;
    }

    public void setChildCategory(String childCategory) {
        this.childCategory = childCategory;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 958893615)
    public List<AudioListInfo> getAudioListInfoList() {
        if (audioListInfoList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            AudioListInfoDao targetDao = daoSession.getAudioListInfoDao();
            List<AudioListInfo> audioListInfoListNew = targetDao
                    ._queryAudioInfo_AudioListInfoList(audioHash);
            synchronized (this) {
                if (audioListInfoList == null) {
                    audioListInfoList = audioListInfoListNew;
                }
            }
        }
        return audioListInfoList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 365834644)
    public synchronized void resetAudioListInfoList() {
        audioListInfoList = null;
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 851685971)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getAudioInfoDao() : null;
    }



}
