package yanzhikai.simpleplayer.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import yanzhikai.simpleplayer.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoaclAudioListFragment extends Fragment {


    public LoaclAudioListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_loacl_audio_list, container, false);
    }

}
