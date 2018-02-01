package yanzhikai.simpleplayer.db;

import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.util.Log;

import com.jian.greendao.gen.AudioInfoDao;
import com.jian.greendao.gen.AudioListInfoDao;
import com.jian.greendao.gen.DaoMaster;
import com.jian.greendao.gen.DaoSession;
import com.jian.greendao.gen.JoinListWithAudioDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.Collections;
import java.util.List;

import yanzhikai.simpleplayer.model.AudioInfo;
import yanzhikai.simpleplayer.model.AudioListInfo;
import yanzhikai.simpleplayer.model.JoinListWithAudio;
import yanzhikai.simpleplayer.utils.MD5Util;

/**
 * author : yany
 * e-mail : yanzhikai_yjk@qq.com
 * time   : 2018/01/15
 * desc   :
 */

public class AudioListDaoManager {
    public static final String TAG = "yjkAudioListDaoManager";
    private static final String DB_NAME = "AudioList";
    public static final String LOCAL_LIST_NAME = "_LocalList";
    private volatile static AudioListDaoManager mManager = new AudioListDaoManager();
    private static DaoMaster sDaoMaster;
    private static DaoMaster.DevOpenHelper sHelper;
    private static DaoSession sDaoSession;

    private Context mContext;

    private AudioListInfo mLocalListInfo;


    public static AudioListDaoManager getInstance() {
        return mManager;
    }

    public void init(Context context) {
        mContext = context;
        initLocalAudioList();
    }


    public DaoMaster getDaoMaster() {
        if (sDaoMaster == null) {
            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(mContext, DB_NAME, null);
            sDaoMaster = new DaoMaster(helper.getWritableDatabase());
        }
        return sDaoMaster;
    }

    //后面使用的时候要用这个方法，不能直接调用本体，因为有可能已经会被关掉
    public DaoSession getDaoSession() {
        if (sDaoSession == null) {
            if (sDaoMaster == null) {
                sDaoMaster = getDaoMaster();
            }
            sDaoSession = sDaoMaster.newSession();
        }
        return sDaoSession;
    }

    private void initLocalAudioList() {
        if (!isAudioListExist(LOCAL_LIST_NAME)) {
            AudioListInfo audioListInfo = new AudioListInfo();
            audioListInfo.setListName(LOCAL_LIST_NAME);
            insertList(audioListInfo);
            Log.d(TAG, "initLocalAudioList: create");
        }
        Log.d(TAG, "initLocalAudioList: size: " + queryAudioListByName(LOCAL_LIST_NAME).getInfoList().size());
        mLocalListInfo = queryAudioListByName(LOCAL_LIST_NAME);
        Log.d(TAG, "initLocalAudioList: get:" + mLocalListInfo.getInfoList().size());
    }

    /**
     * 打开输出日志，默认关闭
     */
    public void setDebug() {
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;
    }

    /**
     * 关闭所有的操作，数据库开启后，使用完毕要关闭
     */
    public void closeConnection() {
        closeHelper();
        closeDaoSession();
    }

    public void closeHelper() {
        if (sHelper != null) {
            sHelper.close();
            sHelper = null;
        }
    }

    public void closeDaoSession() {
        if (sDaoSession != null) {
            sDaoSession.clear();
            sDaoSession = null;
        }
    }

    public boolean insertList(AudioListInfo listInfo) {
        boolean flag = false;
        try {
            flag = getDaoSession().getAudioListInfoDao().insert(listInfo) != -1;
        } catch (SQLiteConstraintException e) {
            e.printStackTrace();
            return false;
        }
        return flag;
    }


    public boolean insertAudio(AudioInfo audioInfo) {
        boolean flag = false;
        try {
            flag = getDaoSession().getAudioInfoDao().insert(audioInfo) != -1;
        } catch (SQLiteConstraintException e) {
            e.printStackTrace();
            return false;
        }
        return flag;
    }

    public boolean insertJoin(JoinListWithAudio joinListWithAudio) {
        boolean flag = false;
        try {
            flag = getDaoSession().getJoinListWithAudioDao().insert(joinListWithAudio) != -1;
        } catch (SQLiteConstraintException e) {
            e.printStackTrace();
            return false;
        }
        return flag;
    }

    /**
     * 插入多条数据，在子线程操作
     *
     * @param listInfos
     * @return
     */
    public boolean insertMultiList(final List<AudioListInfo> listInfos) {
        boolean flag = false;
        try {
            getDaoSession().runInTx(new Runnable() {
                @Override
                public void run() {
                    for (AudioListInfo info : listInfos) {
                        getDaoSession().insertOrReplace(info);
                    }
                }
            });
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 修改一条数据
     *
     * @param listInfo
     * @return
     */
    public boolean updateList(AudioListInfo listInfo) {
        boolean flag = false;
        try {
            getDaoSession().update(listInfo);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 删除单条记录
     */
    public boolean deleteAudioList(AudioListInfo listInfo) {
        boolean flag = false;
        try {
            getDaoSession().getAudioListInfoDao().delete(listInfo);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 删除多条记录
     */
    public boolean deleteAudioList(List<AudioListInfo> listInfos) {
        boolean flag = false;
        try {
            for (AudioListInfo listInfo : listInfos) {
                getDaoSession().getAudioListInfoDao().delete(listInfo);
            }
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 查询所有记录
     *
     * @return
     */
    public List<AudioListInfo> queryAllList() {
        return getDaoSession().loadAll(AudioListInfo.class);
    }

    /**
     * 根据主键id查询记录
     *
     * @param name
     * @return
     */
    public AudioListInfo queryAudioListByName(String name) {
        return getDaoSession().load(AudioListInfo.class, name);
    }


    /**
     * 使用queryBuilder进行查询
     *
     * @return
     */
    public List<AudioListInfo> queryAudioListByQueryBuilder(String name) {
        QueryBuilder<AudioListInfo> queryBuilder = getDaoSession().queryBuilder(AudioListInfo.class);
        return queryBuilder.where(AudioListInfoDao.Properties.ListName.eq(name)).list();
    }

    public boolean isAudioListExist(String name) {
        QueryBuilder<AudioListInfo> queryBuilder = getDaoSession().queryBuilder(AudioListInfo.class);
        if (queryBuilder.where(AudioListInfoDao.Properties.ListName.eq(name)).list().size() > 0) {
            return true;
        } else {
            return false;
        }
    }


    public boolean isAudioExist(String hash) {
        QueryBuilder<AudioInfo> queryBuilder = getDaoSession().queryBuilder(AudioInfo.class);
        if (queryBuilder.where(AudioInfoDao.Properties.AudioHash.eq(hash)).list().size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean insertAudioToList(AudioInfo audioInfo, AudioListInfo audioListInfo) {
        String joinMd5 = MD5Util.encryptMD5ToString(audioInfo.getAudioHash() + audioListInfo.getListName());
        QueryBuilder<AudioInfo> queryBuilder = getDaoSession().queryBuilder(AudioInfo.class);
        List<AudioInfo> audioInfos = queryBuilder.where(AudioInfoDao.Properties.AudioHash.eq(audioInfo.getAudioHash())).list();
        if (audioInfos.size() <= 0) {
            //未存在歌曲
            insertAudio(audioInfo);
        }

//        QueryBuilder<JoinListWithAudio> joinQueryBuilder = getDaoSession().queryBuilder(JoinListWithAudio.class);
//        List<JoinListWithAudio> joinListWithAudios = joinQueryBuilder.where(JoinListWithAudioDao.Properties.JoinMD5.eq(joinMd5)).list();
        JoinListWithAudio joinListWithAudio = queryJoin(joinMd5);
        if (joinListWithAudio == null) {
            JoinListWithAudio join = new JoinListWithAudio(
                    joinMd5
                    , audioListInfo.getListName()
                    , audioInfo.getAudioHash()
            );
            insertJoin(join);
            return true;
        }else {
            Log.d(TAG, "insertAudioToList: 已存在关系");
            return false;
        }
    }

    public boolean insertAudiosToList(List<AudioInfo> infoList, AudioListInfo audioListInfo){
        boolean flag = true;
        for (AudioInfo audioInfo : infoList){
            flag &= insertAudioToList(audioInfo,audioListInfo);
        }
        return flag;
    }

    public JoinListWithAudio queryJoin(String md5){
        return getDaoSession().getJoinListWithAudioDao().load(md5);
    }

    public boolean deleteAudioInList(AudioInfo audioInfo, String listName){
        String  md5 = MD5Util.encryptMD5ToString(audioInfo.getAudioHash() + listName);
        JoinListWithAudio joinListWithAudio = queryJoin(md5);
        if (joinListWithAudio != null) {
            getDaoSession().getJoinListWithAudioDao().delete(joinListWithAudio);
            return true;
        }else {
            Log.w(TAG, "deleteAudioInList: failed, can not found!" );
            return false;
        }
    }

    public void deleteAudioInLocalList(AudioInfo audioInfo){
        deleteAudioInList(audioInfo,LOCAL_LIST_NAME);
    }

    /*
    ----------------本地歌曲数据表-------------------
     */

    public AudioListInfo getLocalListInfo() {
//        return mLocalListInfo;
        return queryAudioListByName(LOCAL_LIST_NAME);
    }

    public List<AudioListInfo> getRefreshListInfos(){
        List<AudioListInfo> listInfos = queryAllList();
        listInfos.remove(getLocalListInfo());
        return listInfos;
    }

    public void insertLocalAudio(AudioInfo... audioInfo) {
        try {
            AudioListInfo audioListInfo = getLocalListInfo();
            Collections.addAll(audioListInfo.getInfoList(), audioInfo);
            Log.d(TAG, "insertLocalAudio: " + audioListInfo.getInfoList().size());
            audioListInfo.update();
//            Log.d(TAG, "insertLocalAudio: " + updateList(audioListInfo));
        } catch (SQLiteConstraintException e) {
            e.printStackTrace();
        }
    }

    /**
     * 插入多条数据，在子线程操作
     *
     * @param audioInfos
     * @return
     */
    public void insertMultiLocalAudio(final List<AudioInfo> audioInfos) {
        try {
            getLocalListInfo().getInfoList().addAll(audioInfos);
            getLocalListInfo().update();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改一条数据
     *
     * @param audioInfo
     * @return
     */
    public boolean updateAudio(AudioInfo audioInfo) {
        boolean flag = false;
        try {
            getDaoSession().update(audioInfo);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }


    /**
     * 删除所有记录
     *
     * @return
     */
    public void deleteAllLocalAudio() {
        try {
            mLocalListInfo.getInfoList().clear();
            mLocalListInfo.update();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询所有记录
     *
     * @return
     */
    public List<AudioInfo> queryAllLocalAudio() {
        return getLocalListInfo().getInfoList();
    }


//    public boolean isExistInLocalList(String hash){
//        return getLocalListInfo().isExist(hash);
//    }
}
