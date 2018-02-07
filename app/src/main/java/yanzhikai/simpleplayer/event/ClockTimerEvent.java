package yanzhikai.simpleplayer.event;

/**
 * author : yany
 * e-mail : yanzhikai_yjk@qq.com
 * time   : 2018/02/07
 * desc   :
 */

public class ClockTimerEvent {
    public static final int TIMER_START = 0;
    public static final int TIMER_STOP = 1;

    public int type = TIMER_START;
    public long duration,interval;

    public ClockTimerEvent(long duration, long interval, int type){
        this.duration = duration;
        this.interval = interval;
        this.type = type;
    }
}
