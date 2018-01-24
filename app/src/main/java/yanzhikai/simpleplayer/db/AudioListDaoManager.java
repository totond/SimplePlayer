package yanzhikai.simpleplayer.db;

import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.util.Log;

import com.jian.greendao.gen.AudioInfoDao;
import com.jian.greendao.gen.AudioListInfoDao;
import com.jian.greendao.gen.DaoMaster;
import com.jian.greendao.gen.DaoSession;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

import yanzhikai.simpleplayer.model.AudioInfo;
import yanzhikai.simpleplayer.model.AudioListInfo;

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


    public static AudioListDaoManager getInstance(){
        return mManager;
    }

    public void init(Context context){
        mContext = context;
//        initLocalAudioList();
//        getDaoSession();
    }

    public DaoMaster getDaoMaster(){
        if(sDaoMaster == null) {
            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(mContext, DB_NAME, null);
            sDaoMaster = new DaoMaster(helper.getWritableDatabase());
        }
        return sDaoMaster;
    }

    public DaoSession getDaoSession(){
        if(sDaoSession == null){
            if(sDaoMaster == null){
                sDaoMaster = getDaoMaster();
            }
            sDaoSession = sDaoMaster.newSession();
        }
        return sDaoSession;
    }

    private void initLocalAudioList(){
        if (!isExist(LOCAL_LIST_NAME)) {
            AudioListInfo audioListInfo = new AudioListInfo();
            audioListInfo.setAudioListName(LOCAL_LIST_NAME);
            insertList(audioListInfo);
            Log.d(TAG, "initLocalAudioList: create");
        }
        Log.d(TAG, "initLocalAudioList: size: " + queryAudioByQueryBuilder(LOCAL_LIST_NAME).size());
        mLocalListInfo = queryAudioByQueryBuilder(LOCAL_LIST_NAME).get(0);
        Log.d(TAG, "initLocalAudioList: get:" + mLocalListInfo.getInfoList().size());
    }

    /**
     * 打开输出日志，默认关闭
     */
    public void setDebug(){
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;
    }

    /**
     * 关闭所有的操作，数据库开启后，使用完毕要关闭
     */
    public void closeConnection(){
        closeHelper();
        closeDaoSession();
    }

    public void closeHelper(){
        if(sHelper != null){
            sHelper.close();
            sHelper = null;
        }
    }

    public void closeDaoSession(){
        if(sDaoSession != null){
            sDaoSession.clear();
            sDaoSession = null;
        }
    }

    public boolean insertList(AudioListInfo listInfo){
        boolean flag = false;
        try {
            flag = getDaoSession().getAudioListInfoDao().insert(listInfo) != -1;
        }catch (SQLiteConstraintException e){
            e.printStackTrace();
            return false;
        }
        Log.i(TAG, "insert Audio :" + flag + "-->" + listInfo.toString());
        return flag;
    }

    /**
     * 插入多条数据，在子线程操作
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
     * @param listInfo
     * @return
     */
    public boolean updateList(AudioListInfo listInfo){
        boolean flag = false;
        try {
            getDaoSession().update(listInfo);
            flag = true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 删除单条记录
     * @param listInfo
     * @return
     */
    public boolean deleteAudio(AudioListInfo listInfo){
        boolean flag = false;
        try {
            //按照id删除
            getDaoSession().delete(listInfo);
            flag = true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 删除所有记录
     * @return
     */
    public boolean deleteAll(){
        boolean flag = false;
        try {
            //按照id删除
            getDaoSession().deleteAll(AudioListInfo.class);
            flag = true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 查询所有记录
     * @return
     */
    public List<AudioListInfo> queryAllList(){
        return getDaoSession().loadAll(AudioListInfo.class);
    }

    /**
     * 根据主键id查询记录
     * @param key
     * @return
     */
    public AudioListInfo queryAudioById(long key){
        return getDaoSession().load(AudioListInfo.class, key);
    }

    /**
     * 使用native sql进行查询操作
     */
    public List<AudioListInfo> queryAudioByNativeSql(String sql, String[] conditions){
        return getDaoSession().queryRaw(AudioListInfo.class, sql, conditions);
    }

    /**
     * 使用queryBuilder进行查询
     * @return
     */
    public List<AudioListInfo> queryAudioByQueryBuilder(String  name){
        QueryBuilder<AudioListInfo> queryBuilder = getDaoSession().queryBuilder(AudioListInfo.class);
        return queryBuilder.where(AudioListInfoDao.Properties.AudioListName.eq(name)).list();
    }

    public boolean isExist(String name){
        QueryBuilder<AudioListInfo> queryBuilder = getDaoSession().queryBuilder(AudioListInfo.class);
        if(queryBuilder.where(AudioListInfoDao.Properties.AudioListName.eq(name)).list().size() > 0){
            return true;
        }else {
            return false;
        }
    }


    /*
    ----------------本地歌曲数据表-------------------
     */

    public AudioListInfo getLocalListInfo(){
        return queryAudioByQueryBuilder(LOCAL_LIST_NAME).get(0);
    }

    public void insertLocalAudio(AudioInfo audioInfo){
        try {
            AudioListInfo audioListInfo = getLocalListInfo();
            audioListInfo.insertAudio(audioInfo);
            audioListInfo.update();
//            Log.d(TAG, "insertLocalAudio: " + updateList(audioListInfo));
        }catch (SQLiteConstraintException e){
            e.printStackTrace();
        }
    }

    /**
     * 插入多条数据，在子线程操作
     * @param audioInfos
     * @return
     */
    public void insertMultiAudio(final List<AudioInfo> audioInfos) {
        try {
            mLocalListInfo.getInfoList().addAll(audioInfos);
            mLocalListInfo.update();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改一条数据
     * @param audioInfo
     * @return
     */
    public boolean updateAudio(AudioInfo audioInfo){
        boolean flag = false;
        try {
            getDaoSession().update(audioInfo);
            flag = true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 删除单条记录
     * @param meizi
     * @return
     */
    public boolean deleteAudio(AudioInfo meizi){
        boolean flag = false;
        try {
            //按照id删除
            getDaoSession().delete(meizi);
            flag = true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 删除所有记录
     * @return
     */
    public void deleteAllLocalAudio(){
        try {
            mLocalListInfo.getInfoList().clear();
            mLocalListInfo.update();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 查询所有记录
     * @return
     */
    public List<AudioInfo> queryAllLocalAudio(){
        return getLocalListInfo().getInfoList();
    }




    public boolean isExistInLocalList(String hash){
        return getLocalListInfo().isExist(hash);
    }
}
