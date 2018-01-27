package yanzhikai.simpleplayer.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.JoinEntity;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Unique;
import com.jian.greendao.gen.DaoSession;
import com.jian.greendao.gen.AudioInfoDao;
import com.jian.greendao.gen.AudioListInfoDao;

/**
 * author : yany
 * e-mail : yanzhikai_yjk@qq.com
 * time   : 2018/01/24
 * desc   : 歌单
 */

@Entity(nameInDb = "AudioListInfo")
public class AudioListInfo {

    @Unique
    @Id
    private String listName;

    @ToMany
    @JoinEntity(
            entity = JoinListWithAudio.class,
            sourceProperty = "listName",
            targetProperty = "audioHash"
    )
    private List<AudioInfo> infoList;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 2073011157)
    private transient AudioListInfoDao myDao;

    @Generated(hash = 1898050267)
    public AudioListInfo(String listName) {
        this.listName = listName;
    }

    @Generated(hash = 1163217503)
    public AudioListInfo() {
    }

    public String getListName() {
        return this.listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public List<AudioInfo> getRefreshList(){
        resetInfoList();
        return getInfoList();
    }
    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 961170995)
    public List<AudioInfo> getInfoList() {
        if (infoList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            AudioInfoDao targetDao = daoSession.getAudioInfoDao();
            List<AudioInfo> infoListNew = targetDao
                    ._queryAudioListInfo_InfoList(listName);
            synchronized (this) {
                if (infoList == null) {
                    infoList = infoListNew;
                }
            }
        }
        return infoList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 97028372)
    public synchronized void resetInfoList() {
        infoList = null;
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
    @Generated(hash = 1901223013)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getAudioListInfoDao() : null;
    }



}
