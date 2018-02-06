package yanzhikai.simpleplayer.ui;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.TimePickerView;

import java.text.ParseException;
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
import yanzhikai.simpleplayer.utils.ToastUtil;

import static yanzhikai.simpleplayer.model.AlarmInfo.EVERY;
import static yanzhikai.simpleplayer.model.AlarmInfo.ONCE;
import static yanzhikai.simpleplayer.model.AlarmInfo.WEEKEND;
import static yanzhikai.simpleplayer.model.AlarmInfo.WORKDAY;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ClockFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ClockFragment extends Fragment implements View.OnClickListener {
    public static final String[] date_repeats = {ONCE, EVERY};
    private ClockItemLayout cl_play_time, cl_play_date, cl_play_audios;
    private ImageView iv_back;
    private TextView tv_save;
    private TimePickerView tpv;
    private OptionsPickerView opv_repeat, opv_audios;
    private ArrayList<String> mOptionsList = new ArrayList<>();
    private ArrayList<String> mAudiosList = new ArrayList<>();
    private AlarmInfo mAlarmInfo;
    private boolean isChanged = false;


    public ClockFragment() {
    }

    public static ClockFragment newInstance() {
        ClockFragment fragment = new ClockFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initOptions();
        isChanged = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_clock, container, false);
        cl_play_time = rootView.findViewById(R.id.cl_play_time);
        cl_play_date = rootView.findViewById(R.id.cl_play_date);
        cl_play_audios = rootView.findViewById(R.id.cl_play_audios);
        iv_back = rootView.findViewById(R.id.iv_back);
        tv_save = rootView.findViewById(R.id.tv_save);

        cl_play_time.setOnClickListener(this);
        cl_play_date.setOnClickListener(this);
        cl_play_audios.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        tv_save.setOnClickListener(this);

        initData();
        initPickerViews();

        return rootView;
    }

    private void initOptions() {
        mOptionsList.addAll(Arrays.asList(date_repeats));
    }

    private void initData() {
        mAlarmInfo = AudioListDaoManager.getInstance().queryAlarmInfo();
    }

    private void refreshAudios() {
        mAudiosList.clear();
        mAudiosList.add(getString(R.string.play_list_title));
        List<AudioListInfo> infos = AudioListDaoManager.getInstance().getRefreshListInfos();
        for (AudioListInfo info : infos) {
            mAudiosList.add(info.getListName());
        }
    }

    private void initPickerViews() {
        String[] times = mAlarmInfo.getTime().split(":");
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get
                (Calendar.DAY_OF_MONTH), Integer.parseInt(times[0]), Integer.parseInt(times[1]), 0);
        tpv = new TimePickerView
                .Builder(getContext(), new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                mAlarmInfo.setTime(format.format(date));
                cl_play_time.setContentText(mAlarmInfo.getTime());
                isChanged = true;
            }
        })
                .setType(new boolean[]{false, false, false, true, true, false})
                .setSubmitText(getString(R.string.yes))
                .setCancelText(getString(R.string.no))
                .isCyclic(true)
                .setDate(calendar)
                .setContentSize(26)
                .isDialog(true)
                .build();
        refreshAudios();
        opv_repeat = new OptionsPickerView.Builder(getContext(), new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                mAlarmInfo.setRepeat(mOptionsList.get(options1));
                cl_play_date.setContentText(mAlarmInfo.getRepeat());
                isChanged = true;
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
                mAlarmInfo.setAudioList(mAudiosList.get(options1));
                cl_play_audios.setContentText(mAlarmInfo.getAudioList());
                isChanged = true;
            }
        })
                .setSubmitText(getString(R.string.yes))
                .setCancelText(getString(R.string.no))
                .setContentTextSize(26)
                .isDialog(true)
                .build();
        opv_audios.setPicker(mAudiosList);

        cl_play_time.setContentText(mAlarmInfo.getTime());
        cl_play_date.setContentText(mAlarmInfo.getRepeat());
        cl_play_audios.setContentText(mAlarmInfo.getAudioList());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cl_play_time:
                tpv.show();
                break;
            case R.id.cl_play_date:
                opv_repeat.show();
                break;
            case R.id.cl_play_audios:
                opv_audios.show();
                break;
            case R.id.iv_back:
                if (isChanged) {
                    showSaveDialog();
                }else {
                    exit();
                }
                break;
            case R.id.tv_save:
                saveInfo();
                ToastUtil.makeShortToast(getActivity(), "闹钟设置成功");
                exit();
                break;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        if (isChanged) {
            showSaveDialog();
        }
        super.onDestroyView();
    }

    private void showSaveDialog() {
        isChanged = false;
        AlertDialog.Builder saveDialogBuilder = new AlertDialog.Builder(getActivity());
        saveDialogBuilder
                .setTitle(R.string.alarm_save_ask)
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        exit();
                    }
                })
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveInfo();
                        exit();
                    }
                })
                .setCancelable(false)
                .show();
    }

    private void saveInfo() {
        mAlarmInfo.setIsOpen(true);
        AudioListDaoManager.getInstance().updateAlarm(mAlarmInfo);
        isChanged = false;
        mAlarmInfo.setClock(getActivity());
    }

    private void exit() {
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.popFragment();
    }

}
