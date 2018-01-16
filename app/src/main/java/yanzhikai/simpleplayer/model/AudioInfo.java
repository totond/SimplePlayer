package yanzhikai.simpleplayer.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;

import java.io.Serializable;
import org.greenrobot.greendao.annotation.Generated;

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

    @Id(autoincrement = true)
    private Long _id;


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
    @Unique
    private String hash;
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


    public AudioInfo() {

    }

    @Generated(hash = 1838736429)
    public AudioInfo(Long _id, String songName, String singerName, String hash,
            String fileExt, long fileSize, String fileSizeText, String filePath,
            long duration, String durationText, String downloadUrl,
            String createTime, int status, int type, String category,
            String childCategory) {
        this._id = _id;
        this.songName = songName;
        this.singerName = singerName;
        this.hash = hash;
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

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getSingerName() {
        return singerName;
    }

    public void setSingerName(String singerName) {
        this.singerName = singerName;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getFileExt() {
        return fileExt;
    }

    public void setFileExt(String fileExt) {
        this.fileExt = fileExt;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileSizeText() {
        return fileSizeText;
    }

    public void setFileSizeText(String fileSizeText) {
        this.fileSizeText = fileSizeText;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getDurationText() {
        return durationText;
    }

    public void setDurationText(String durationText) {
        this.durationText = durationText;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getChildCategory() {
        return childCategory;
    }

    public void setChildCategory(String childCategory) {
        this.childCategory = childCategory;
    }

    @Override
    public String toString() {
        return "AudioInfo{" +
                "songName='" + songName + '\'' +
                ", singerName='" + singerName + '\'' +
                ", hash='" + hash + '\'' +
                ", fileExt='" + fileExt + '\'' +
                ", fileSize=" + fileSize +
                ", fileSizeText='" + fileSizeText + '\'' +
                ", filePath='" + filePath + '\'' +
                ", duration=" + duration +
                ", durationText='" + durationText + '\'' +
                ", downloadUrl='" + downloadUrl + '\'' +
                ", createTime='" + createTime + '\'' +
                ", status=" + status +
                ", mType=" + type +
                ", category='" + category + '\'' +
                ", childCategory='" + childCategory + '\'' +
                '}';
    }

    public Long get_id() {
        return this._id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }
}
