package yanzhikai.simpleplayer.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import yanzhikai.alarmclock.AlarmManagerUtil;
import yanzhikai.simpleplayer.db.AudioListDaoManager;
import yanzhikai.simpleplayer.event.ClockPlayEvent;
import yanzhikai.simpleplayer.utils.EventUtil;
import yanzhikai.simpleplayer.utils.ToastUtil;

/**
 * author : yany
 * e-mail : yanzhikai_yjk@qq.com
 * time   : 2018/02/06
 * desc   :
 */

public class ClockReceiver extends BroadcastReceiver {
    public static final String TAG = "yjkClockReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: ");

        long intervalMillis = intent.getLongExtra("intervalMillis", 0);
        Log.d("yjk", "onReceive:intervalMillis " + intervalMillis);
        if (intervalMillis != 0) {
            AlarmManagerUtil.setAlarmTime(context, System.currentTimeMillis() + intervalMillis,
                    intent);
        }else {
            AudioListDaoManager.getInstance().queryAlarmInfo().setIsOpen(false);
        }
        String listName = intent.getStringExtra("listName");
        EventUtil.post(new ClockPlayEvent(listName));
    }
}
