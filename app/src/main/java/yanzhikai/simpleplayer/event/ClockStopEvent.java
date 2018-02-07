package yanzhikai.simpleplayer.event;

/**
 * author : yany
 * e-mail : yanzhikai_yjk@qq.com
 * time   : 2018/02/07
 * desc   :
 */

public class ClockStopEvent {
    public static final int CLOCK_TICK = 0;
    public static final int CLOCK_FINISH = 1;

    public int type = CLOCK_TICK;
    public String time;
}
