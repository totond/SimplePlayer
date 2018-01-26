package yanzhikai.simpleplayer.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

/**
 * author : yany
 * e-mail : yanzhikai_yjk@qq.com
 * time   : 2018/01/16
 * desc   :
 */
@Entity(nameInDb = "PlayingAudioInfo")
public class PlayingAudioInfo {
    @Id(autoincrement = true)
    private Long _id;


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
     * 当前播放时间
     */
    private long currentTime;
    private String currentTimeText;
    @Generated(hash = 824445100)
    public PlayingAudioInfo(Long _id, int index, String songName, String singerName,
            String hash, String fileExt, long fileSize, String fileSizeText,
            String filePath, long duration, String durationText, long currentTime,
            String currentTimeText) {
        this._id = _id;
        this.index = index;
        this.songName = songName;
        this.singerName = singerName;
        this.hash = hash;
        this.fileExt = fileExt;
        this.fileSize = fileSize;
        this.fileSizeText = fileSizeText;
        this.filePath = filePath;
        this.duration = duration;
        this.durationText = durationText;
        this.currentTime = currentTime;
        this.currentTimeText = currentTimeText;
    }

    public PlayingAudioInfo(AudioInfo audioInfo){
//        this._id = audioInfo.get_id();
        this.songName = audioInfo.getSongName();
        this.singerName = audioInfo.getSingerName();
        this.hash = audioInfo.getAudioHash();
        this.fileExt = audioInfo.getFileExt();
        this.fileSize = audioInfo.getFileSize();
        this.fileSizeText = audioInfo.getFileSizeText();
        this.filePath = audioInfo.getFilePath();
        this.duration = audioInfo.getDuration();
        this.durationText = audioInfo.getDurationText();
    }
    @Generated(hash = 1603176217)
    public PlayingAudioInfo() {
    }
    public Long get_id() {
        return this._id;
    }
    public void set_id(Long _id) {
        this._id = _id;
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
    public String getHash() {
        return this.hash;
    }
    public void setHash(String hash) {
        this.hash = hash;
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
    public long getCurrentTime() {
        return this.currentTime;
    }
    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }
    public String getCurrentTimeText() {
        return this.currentTimeText;
    }
    public void setCurrentTimeText(String currentTimeText) {
        this.currentTimeText = currentTimeText;
    }

    public int getIndex() {
        return this.index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
