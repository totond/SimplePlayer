package yanzhikai.simpleplayer.ui;


import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import yanzhikai.simpleplayer.MainActivity;
import yanzhikai.simpleplayer.R;
import yanzhikai.simpleplayer.adapter.BaseOnItemClickListener;
import yanzhikai.simpleplayer.adapter.ShowListAdapter;
import yanzhikai.simpleplayer.db.AudioListDaoManager;
import yanzhikai.simpleplayer.event.ListSizeChangedEvent;
import yanzhikai.simpleplayer.event.OpenAudioListEvent;
import yanzhikai.simpleplayer.model.AudioListInfo;
import yanzhikai.simpleplayer.utils.EventUtil;
import yanzhikai.simpleplayer.utils.ToastUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShowListsFragment extends Fragment implements View.OnClickListener {
    public static final String TAG = "yjkShowListsFragment";

    private RecyclerView rv_show_list;
    private ShowListAdapter mShowListAdapter;
    private TextView tv_delete, tv_new;
    private ImageView iv_back;

    public ShowListsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventUtil.unregister(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        EventUtil.register(this);
        View rootView = inflater.inflate(R.layout.fragment_show_lists, container, false);
        tv_new = rootView.findViewById(R.id.tv_new);
        tv_delete = rootView.findViewById(R.id.tv_delete);
        iv_back = rootView.findViewById(R.id.iv_back);

        tv_new.setOnClickListener(this);
        tv_delete.setOnClickListener(this);
        iv_back.setOnClickListener(this);

        rv_show_list = rootView.findViewById(R.id.rv_show_list);
        rv_show_list.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration divider = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        mShowListAdapter = new ShowListAdapter(getContext(), AudioListDaoManager.getInstance().getRefreshListInfos());
        mShowListAdapter.setOnClickListener(new ShowListItemClickListener());
        rv_show_list.setAdapter(mShowListAdapter);
        rv_show_list.addItemDecoration(divider);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_new:
                Log.d(TAG, "onClick: tv_new");
                showInputDialog();
                break;
            case R.id.tv_delete:
                if (mShowListAdapter.getEditMode()) {
                    mShowListAdapter.setEditMode(false);
                    Log.d(TAG, "delete: " + mShowListAdapter.getSelectedItem().size());
                    if (AudioListDaoManager.getInstance().deleteAudioList(mShowListAdapter.getSelectedItem())) {
                        Log.d(TAG, "delete: succeeded");
                    } else {
                        Log.d(TAG, "delete: failed");
                    }
                    mShowListAdapter.clearSelected();
                    mShowListAdapter.setAudioListInfos(AudioListDaoManager.getInstance().getRefreshListInfos());
                } else {
                    mShowListAdapter.setEditMode(true);
                }
                mShowListAdapter.notifyDataSetChanged();
                break;
            case R.id.iv_back:
                MainActivity mainActivity = (MainActivity)getActivity();
                mainActivity.popFragment();
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleSizeChanged(ListSizeChangedEvent listSizeChangedEvent){
        mShowListAdapter.notifyDataSetChanged();
    }

    //弹出输入框Dialog
    private void showInputDialog() {

        final EditText editText = new EditText(getActivity());
        final AlertDialog.Builder inputDialog =
                new AlertDialog.Builder(getActivity());
        inputDialog.setTitle(R.string.audio_show_add_list_ask).setView(editText);
        inputDialog.setPositiveButton(R.string.yes,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AudioListInfo listInfo = new AudioListInfo(editText.getText().toString());
                        String str = String.format(getString(R.string.audio_show_list_succeed), listInfo.getListName());

                        if (AudioListDaoManager.getInstance().insertList(listInfo)) {
                            ToastUtil.makeShortToast(ShowListsFragment.this.getActivity(), str);
                            mShowListAdapter.setAudioListInfos(AudioListDaoManager.getInstance().getRefreshListInfos());
                            mShowListAdapter.notifyDataSetChanged();
                        }else {
                            ToastUtil.makeShortToast(ShowListsFragment.this.getContext(),getString(R.string.audio_show_list_existed));
                        }

                    }
                });
        inputDialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        inputDialog.show();
    }

    private class ShowListItemClickListener implements BaseOnItemClickListener{

        @Override
        public void onItemClick(int index) {
            EventUtil.post(new OpenAudioListEvent(mShowListAdapter.getAudioListInfos().get(index).getListName()));
        }
    }
}
