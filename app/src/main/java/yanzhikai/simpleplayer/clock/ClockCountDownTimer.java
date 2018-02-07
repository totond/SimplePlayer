package yanzhikai.simpleplayer.clock;

import android.os.CountDownTimer;
import android.util.Log;

import yanzhikai.simpleplayer.MyApplication;
import yanzhikai.simpleplayer.event.ClockStopEvent;
import yanzhikai.simpleplayer.utils.DateUtil;
import yanzhikai.simpleplayer.utils.EventUtil;

/**
 * author : yany
 * e-mail : yanzhikai_yjk@qq.com
 * time   : 2018/02/07
 * desc   :
 */

public class ClockCountDownTimer extends CountDownTimer {
    private ClockStopEvent mStopEvent = new ClockStopEvent();

    /**
     * @param millisInFuture    The number of millis in the future from the call
     *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
     *                          is called.
     * @param countDownInterval The interval along the way to receive
     *                          {@link #onTick(long)} callbacks.
     */
    public ClockCountDownTimer(long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
    }

    @Override
    public void onTick(long millisUntilFinished) {
        mStopEvent.type = ClockStopEvent.CLOCK_TICK;
        mStopEvent.time = DateUtil.getTimeFromMillisecond(millisUntilFinished);
        Log.d("yjk", "onTick: " + millisUntilFinished);
        Log.d("yjk", "onTick: "+ mStopEvent.time);
        EventUtil.post(mStopEvent);

    }



    @Override
    public void onFinish() {
        mStopEvent.type = ClockStopEvent.CLOCK_FINISH;
        MyApplication.countDowning = false;
        EventUtil.post(mStopEvent);
        Log.d("yjk", "onFinish: ");
    }
}
