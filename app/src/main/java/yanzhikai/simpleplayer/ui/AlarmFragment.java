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
import yanzhikai.simpleplayer.model.AudioListInfo;
import yanzhikai.simpleplayer.ui.view.ClockItemLayout;

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
    public static final String[] date_repeats = {ONCE,EVERY,WORKDAY,WEEKEND};
    private ClockItemLayout cl_play_switch, cl_play_time, cl_play_date, cl_play_audios, cl_stop_switch;
    private ImageView iv_back;
    private TimePickerView tpv;
    private OptionsPickerView opv_repeat,opv_audios;
    private ArrayList<String> mOptionsList = new ArrayList<>();
    private ArrayList<String> mAudiosList = new ArrayList<>();


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
        initOptions();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_alarm, container, false);
        cl_play_switch = rootView.findViewById(R.id.cl_play_switch);
        cl_play_time = rootView.findViewById(R.id.cl_play_time);
        cl_play_date = rootView.findViewById(R.id.cl_play_date);
        cl_play_audios = rootView.findViewById(R.id.cl_play_audios);
        cl_stop_switch = rootView.findViewById(R.id.cl_stop_switch);
        iv_back = rootView.findViewById(R.id.iv_back);

        cl_play_switch.setOnClickListener(this);
        cl_play_time.setOnClickListener(this);
        cl_play_date.setOnClickListener(this);
        cl_play_audios.setOnClickListener(this);
        cl_stop_switch.setOnClickListener(this);
        iv_back.setOnClickListener(this);

        cl_play_switch.getSwitch().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cl_play_time.setItemEnable(true);
                    cl_play_date.setItemEnable(true);
                    cl_play_audios.setItemEnable(true);
                } else {
                    cl_play_time.setItemEnable(false);
                    cl_play_date.setItemEnable(false);
                    cl_play_audios.setItemEnable(false);
                }
            }
        });

        initPickerViews();

        return rootView;
    }

    private void initOptions(){
        mOptionsList.addAll(Arrays.asList(date_repeats));
    }

    private void refreshAudios(){
        mAudiosList.clear();
        mAudiosList.add(getString(R.string.play_list_title));
        List<AudioListInfo> infos = AudioListDaoManager.getInstance().getRefreshListInfos();
        for (AudioListInfo info : infos){
            mAudiosList.add(info.getListName());
        }
    }

    private void initPickerViews(){
        tpv = new TimePickerView
                .Builder(getContext(), new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                cl_play_time.setContentText(format.format(date));
            }
        })
                .setType(new boolean[]{false, false, false, true, true, false})
                .setSubmitText(getString(R.string.yes))
                .setCancelText(getString(R.string.no))
                .isCyclic(true)
                .setDate(Calendar.getInstance())
                .setContentSize(26)
                .isDialog(true)
                .build();

        refreshAudios();
        opv_repeat = new OptionsPickerView.Builder(getContext(), new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                cl_play_date.setContentText(mOptionsList.get(options1));
            }
        })
                .setSubmitText(getString(R.string.yes))
                .setCancelText(getString(R.string.no))
                .setContentTextSize(26)
                .isDialog(true)
                .build();
        opv_repeat.setPicker(mOptionsList);

        opv_audios = new OptionsPickerView.Builder(getContext(), new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                cl_play_audios.setContentText(mAudiosList.get(options1));
            }
        })
                .setSubmitText(getString(R.string.yes))
                .setCancelText(getString(R.string.no))
                .setContentTextSize(26)
                .isDialog(true)
                .build();
        opv_audios.setPicker(mAudiosList);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cl_play_switch:
                cl_play_switch.setSwitch(!cl_play_switch.getSwitch().isChecked());
                break;
            case R.id.cl_play_time:
                tpv.show();
                break;
            case R.id.cl_play_date:
                opv_repeat.show();
                break;
            case R.id.cl_play_audios:
                opv_audios.show();
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
