package yanzhikai.simpleplayer.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import yanzhikai.simpleplayer.MainActivity;
import yanzhikai.simpleplayer.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AlarmFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AlarmFragment extends Fragment implements View.OnClickListener{
    private ClockItemLayout cl_play_switch,cl_play_time,cl_play_date,cl_play_audios,cl_stop_switch;
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

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cl_play_switch:
                cl_play_switch.setSwitch(!cl_play_switch.getSwitch());
                break;
            case R.id.cl_play_time:

                break;
            case R.id.cl_play_date:

                break;
            case R.id.cl_play_audios:

                break;
            case R.id.cl_stop_switch:
                cl_stop_switch.setSwitch(!cl_stop_switch.getSwitch());
                break;
            case R.id.iv_back:
                MainActivity mainActivity = (MainActivity)getActivity();
                mainActivity.popFragment();
                break;
        }
    }
}
