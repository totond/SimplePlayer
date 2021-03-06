package yanzhikai.simpleplayer.model;

import android.content.Context;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import yanzhikai.alarmclock.AlarmManagerUtil;

import static yanzhikai.alarmclock.AlarmManagerUtil.ALARM_ACTION;

/**
 * author : yany
 * e-mail : yanzhikai_yjk@qq.com
 * time   : 2018/02/05
 * desc   :
 */

@Entity
public class AlarmInfo {
    public static final String ONCE = "只播一次";
    public static final String EVERY = "每日";
    public static final String WORKDAY = "周一到周五";
    public static final String WEEKEND = "周六日";
    @Id
    private String name;

    private String time;

    private String repeat;

    private String audioList;

    private boolean isOpen;

    @Generated(hash = 1649922400)
    public AlarmInfo(String name, String time, String repeat, String audioList, boolean isOpen) {
        this.name = name;
        this.time = time;
        this.repeat = repeat;
        this.audioList = audioList;
        this.isOpen = isOpen;
    }

    @Generated(hash = 212221696)
    public AlarmInfo() {
    }

    public void cancelClock(Context context){
        AlarmManagerUtil.cancelAlarm(context,ALARM_ACTION,0);
    }

    public void setClock(Context context){
        if (!checkClock()){
            return;
        }

        String[] times = time.split(":");
        switch (repeat){
            case ONCE:
                AlarmManagerUtil.setAlarm(context, 0, Integer.parseInt(times[0]), Integer.parseInt
                        (times[1]), 0, 0, "闹钟响了", 1,audioList);
                break;
            case EVERY:
                AlarmManagerUtil.setAlarm(context, 1, Integer.parseInt(times[0]), Integer.parseInt
                        (times[1]), 0, 0, "闹钟响了", 1,audioList);
                break;
        }
    }

    private boolean checkClock(){
        if (time == null || time.length() < 0){
            return false;
        }
        if (repeat == null || repeat.length() < 0){
            return false;
        }
        if (audioList == null || audioList.length() < 0){
            return false;
        }
        return isOpen;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return this.time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getRepeat() {
        return this.repeat;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }

    public String getAudioList() {
        return this.audioList;
    }

    public void setAudioList(String audioList) {
        this.audioList = audioList;
    }

    public boolean getIsOpen() {
        return this.isOpen;
    }

    public void setIsOpen(boolean isOpen) {
        this.isOpen = isOpen;
    }



}
