package yanzhikai.simpleplayer.db;

import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.util.Log;

import com.jian.greendao.gen.AudioInfoDao;
import com.jian.greendao.gen.DaoMaster;
import com.jian.greendao.gen.DaoSession;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

import yanzhikai.simpleplayer.model.AudioInfo;
import yanzhikai.simpleplayer.model.PlayingAudioInfo;

/**
 * author : yany
 * e-mail : yanzhikai_yjk@qq.com
 * time   : 2018/01/15
 * desc   :
 */

public class PlayListAudioDaoManager {
    public static final String TAG = "yjkPlayListAudioManager";
    private static final String DB_NAME = "PlayListAudio";
    private volatile static PlayListAudioDaoManager mManager = new PlayListAudioDaoManager();
    private static DaoMaster sDaoMaster;
    private static DaoMaster.DevOpenHelper sHelper;
    private static DaoSession sDaoSession;

    private Context mContext;


    public static PlayListAudioDaoManager getInstance(){
        return mManager;
    }

    public void init(Context context){
        mContext = context;
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

    public boolean insertAudio(AudioInfo audioInfo){
        boolean flag = false;
        try {
            flag = getDaoSession().getAudioInfoDao().insert(audioInfo) != -1;
        }catch (SQLiteConstraintException e){
            e.printStackTrace();
            return false;
        }
        Log.i(TAG, "insert Audio :" + flag + "-->" + audioInfo.toString());
        return flag;
    }


    /**
     * 插入多条数据，在子线程操作
     * @param audioInfos
     * @return
     */
    public boolean insertMultiAudio(final List<AudioInfo> audioInfos) {
        boolean flag = false;
        try {
            getDaoSession().runInTx(new Runnable() {
                @Override
                public void run() {
                    for (AudioInfo info : audioInfos) {
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
     * @param info
     * @return
     */
    public boolean deleteAudio(AudioInfo info){
        boolean flag = false;
        try {
            //按照id删除
            getDaoSession().delete(info);
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
            getDaoSession().deleteAll(AudioInfo.class);
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
    public List<AudioInfo> queryAllAudio(){
        return getDaoSession().loadAll(AudioInfo.class);
    }

    /**
     * 根据主键id查询记录
     * @param key
     * @return
     */
    public AudioInfo queryAudioById(long key){
        return getDaoSession().load(AudioInfo.class, key);
    }

    /**
     * 使用native sql进行查询操作
     */
    public List<AudioInfo> queryAudioByNativeSql(String sql, String[] conditions){
        return mManager.getDaoSession().queryRaw(AudioInfo.class, sql, conditions);
    }

    /**
     * 使用queryBuilder进行查询
     * @return
     */
    public List<AudioInfo> queryAudioByQueryBuilder(String hash){
        QueryBuilder<AudioInfo> queryBuilder = getDaoSession().queryBuilder(AudioInfo.class);
        return queryBuilder.where(AudioInfoDao.Properties.Hash.eq(hash)).list();
    }

    public boolean isExist(String hash){
        QueryBuilder<AudioInfo> queryBuilder = getDaoSession().queryBuilder(AudioInfo.class);
        if(queryBuilder.where(AudioInfoDao.Properties.Hash.eq(hash)).list().size() > 0){
            return true;
        }else {
            return false;
        }
    }

    public boolean setPlayingAudio(PlayingAudioInfo playingInfo){
        deleteAllPlayingAudio();
        boolean flag = false;
        try {
            flag = getDaoSession().getPlayingAudioInfoDao().insert(playingInfo) != -1;
        }catch (SQLiteConstraintException e){
            e.printStackTrace();
            return false;
        }
        Log.i(TAG, "insert playing Audio :" + flag + "-->" + playingInfo.getSongName());
        return flag;
    }

    /**
     * 删除所有记录
     * @return
     */
    public boolean deleteAllPlayingAudio(){
        boolean flag = false;
        try {
            //按照id删除
            getDaoSession().deleteAll(PlayingAudioInfo.class);
            flag = true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 查询所有Playing记录
     * @return
     */
    public List<PlayingAudioInfo> queryPlayingAudio(){
        return getDaoSession().loadAll(PlayingAudioInfo.class);
    }
}
