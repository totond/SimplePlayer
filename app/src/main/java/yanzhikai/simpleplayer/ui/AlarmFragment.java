package yanzhikai.simpleplayer.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.TimePickerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import yanzhikai.simpleplayer.MainActivity;
import yanzhikai.simpleplayer.R;
import yanzhikai.simpleplayer.db.AudioListDaoManager;
import yanzhikai.simpleplayer.event.OpenClockEvent;
import yanzhikai.simpleplayer.model.AlarmInfo;
import yanzhikai.simpleplayer.model.AudioListInfo;
import yanzhikai.simpleplayer.ui.view.ClockItemLayout;
import yanzhikai.simpleplayer.utils.EventUtil;

import static yanzhikai.simpleplayer.model.AlarmInfo.EVERY;
import static yanzhikai.simpleplayer.model.AlarmInfo.ONCE;
import static yanzhikai.simpleplayer.model.AlarmInfo.WEEKEND;
import static yanzhikai.simpleplayer.model.AlarmInfo.WORKDAY;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AlarmFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AlarmFragment extends Fragment implements View.OnClickListener {
    private ClockItemLayout cl_play_switch,cl_stop_switch;
    private ImageView iv_back;


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
    }

    private AlarmInfo getInfo(){
        return AudioListDaoManager.getInstance().queryAlarmInfo();
    }

    private void updateClockOpenedState(boolean isOpen){
        AlarmInfo alarmInfo= getInfo();
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
                }else {
                    getInfo().cancelClock(getActivity());
                }
            }
        });
        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();
        cl_play_switch.setSwitch(getInfo().getIsOpen());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cl_play_switch:
                EventUtil.post(new OpenClockEvent());
                break;
            case R.id.cl_stop_switch:
                cl_stop_switch.setSwitch(!cl_stop_switch.getSwitch().isChecked());
                break;
            case R.id.iv_back:
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.popFragment();
                break;
        }
    }
}
