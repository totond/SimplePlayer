package yanzhikai.simpleplayer.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.bigkoo.pickerview.TimePickerView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import yanzhikai.simpleplayer.MainActivity;
import yanzhikai.simpleplayer.MyApplication;
import yanzhikai.simpleplayer.R;
import yanzhikai.simpleplayer.db.AudioListDaoManager;
import yanzhikai.simpleplayer.event.ClockStopEvent;
import yanzhikai.simpleplayer.event.ClockTimerEvent;
import yanzhikai.simpleplayer.event.OpenClockEvent;
import yanzhikai.simpleplayer.model.AlarmInfo;
import yanzhikai.simpleplayer.ui.view.ClockItemLayout;
import yanzhikai.simpleplayer.utils.DateUtil;
import yanzhikai.simpleplayer.utils.EventUtil;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AlarmFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AlarmFragment extends Fragment implements View.OnClickListener {
    private ClockItemLayout cl_play_switch, cl_stop_switch;
    private ImageView iv_back;
    private TimePickerView tpv;


    public AlarmFragment() {
    }

    public static AlarmFragment newInstance() {
        AlarmFragment fragment = new AlarmFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventUtil.register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventUtil.unregister(this);
    }

    private AlarmInfo getInfo() {
        return AudioListDaoManager.getInstance().queryAlarmInfo();
    }

    private void updateClockOpenedState(boolean isOpen) {
        AlarmInfo alarmInfo = getInfo();
        alarmInfo.setIsOpen(isOpen);
        AudioListDaoManager.getInstance().updateAlarm(alarmInfo);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_alarm, container, false);
        cl_play_switch = rootView.findViewById(R.id.cl_play_switch);
        cl_stop_switch = rootView.findViewById(R.id.cl_stop_switch);
        iv_back = rootView.findViewById(R.id.iv_back);

        cl_play_switch.setOnClickListener(this);
        cl_stop_switch.setOnClickListener(this);
        iv_back.setOnClickListener(this);

        cl_play_switch.getSwitch().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateClockOpenedState(isChecked);
                if (isChecked) {
                    getInfo().setClock(getActivity());
                } else {
                    getInfo().cancelClock(getActivity());
                }
            }
        });

        cl_stop_switch.getSwitch().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateClockOpenedState(isChecked);
                if (isChecked) {
                    cl_stop_switch.setSwitchClickable(false);
                } else {
                    cl_stop_switch.setSwitchClickable(true);
                    EventUtil.post(new ClockTimerEvent(0, 0, ClockTimerEvent.TIMER_STOP));
                }
            }
        });
        if (MyApplication.countDowning) {
            cl_stop_switch.setSwitch(true);
        }
        initPickerViews();
        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();
        cl_play_switch.setSwitch(getInfo().getIsOpen());
    }

    private void initPickerViews() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(0, 0, 0, 0, 1, 0);
        tpv = new TimePickerView
                .Builder(getContext(), new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                String time = format.format(date);
                long duration = DateUtil.getMillisecondFromString(time);
                if (duration != 0) {
                    cl_stop_switch.setContentText(time);
                    EventUtil.post(new ClockTimerEvent(duration, 1000, ClockTimerEvent.TIMER_START));
                    cl_stop_switch.setSwitch(true);
                }
            }
        })
                .setType(new boolean[]{false, false, false, true, true, false})
                .setSubmitText(getString(R.string.yes))
                .setCancelText(getString(R.string.no))
                .setContentSize(26)
                .setDate(calendar)
                .isDialog(true)
                .build();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleClockStop(ClockStopEvent clockStopEvent) {
        switch (clockStopEvent.type) {
            case ClockStopEvent.CLOCK_TICK:
                cl_stop_switch.setContentText(clockStopEvent.time);
                break;
            case ClockStopEvent.CLOCK_FINISH:
                cl_stop_switch.setSwitch(false);
                cl_stop_switch.setContentText("");
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.showClockDialog(getString(R.string.clock_timer_finish));
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cl_play_switch:
                EventUtil.post(new OpenClockEvent());
                break;
            case R.id.cl_stop_switch:
                tpv.show();
                break;
            case R.id.iv_back:
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.popFragment();
                break;
        }
    }
}
