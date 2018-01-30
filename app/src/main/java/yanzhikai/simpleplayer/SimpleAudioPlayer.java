package yanzhikai.simpleplayer;

import android.content.Context;
import android.media.AudioManager;

import java.io.IOException;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
/**
 * author : yany
 * e-mail : yanzhikai_yjk@qq.com
 * time   : 2018/01/10
 * desc   :
 */

public class SimpleAudioPlayer {
    public static final String TAG = "SimpleAudioPlayer";

    private IMediaPlayer mMediaPlayer = null;

    private AudioPlayerListener mListener;

    private Context mContext;

    private String mPath;

    public SimpleAudioPlayer(Context context){
        mContext = context;
    }


    /**
     * 加载音频
     */
    public void prepareAsync() {
        //每次都要重新创建IMediaPlayer
        createPlayer();
        try {
            mMediaPlayer.setDataSource(mPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mMediaPlayer.prepareAsync();
    }

    /**
     * 创建一个新的player
     */
    private void createPlayer() {
        release();

        IjkMediaPlayer ijkMediaPlayer = new IjkMediaPlayer();
        ijkMediaPlayer.native_setLogLevel(IjkMediaPlayer.IJK_LOG_DEBUG);

//        //开启硬解码
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1);

        mMediaPlayer = ijkMediaPlayer;
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        if (mListener != null) {
            mMediaPlayer.setOnPreparedListener(mListener);
            mMediaPlayer.setOnInfoListener(mListener);
            mMediaPlayer.setOnSeekCompleteListener(mListener);
            mMediaPlayer.setOnBufferingUpdateListener(mListener);
            mMediaPlayer.setOnErrorListener(mListener);
            mMediaPlayer.setOnCompletionListener(mListener);
            mMediaPlayer.setOnTimedTextListener(mListener);
            mListener.onNewPlayer();
        }
    }


    public void start() {
        if (mMediaPlayer != null) {
            mMediaPlayer.start();
        }
    }

    public void release() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            //mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        System.gc();
    }

    public void pause() {
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
        }
    }

    public void stop() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
        }
    }


    public boolean isPlaying() {
        return mMediaPlayer != null && mMediaPlayer.isPlaying();
    }

    public void reset() {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
        }
    }


    public long getDuration() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getDuration();
        } else {
            return 0;
        }
    }


    public long getCurrentPosition() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getCurrentPosition();
        } else {
            return 0;
        }
    }


    public void seekTo(long l) {
        if (mMediaPlayer != null) {
            mMediaPlayer.seekTo(l);
        }
    }


    public boolean hasPath(){
        return mPath == null;
    }

    public void setPath(String path) {
        this.mPath = path;
    }

    public void setAudioListener(AudioPlayerListener listener) {
        this.mListener = listener;
    }
}
