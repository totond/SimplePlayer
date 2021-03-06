package yanzhikai.simpleplayer.base;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * author : yany
 * e-mail : yanzhikai_yjk@qq.com
 * time   : 2018/01/10
 * desc   :
 */

public interface BaseAudioPlayerListener extends IMediaPlayer.OnBufferingUpdateListener, IMediaPlayer.OnCompletionListener, IMediaPlayer.OnPreparedListener, IMediaPlayer.OnInfoListener, IMediaPlayer.OnVideoSizeChangedListener, IMediaPlayer.OnErrorListener, IMediaPlayer.OnSeekCompleteListener, IMediaPlayer.OnTimedTextListener {
    void onNewPlayer();
}
