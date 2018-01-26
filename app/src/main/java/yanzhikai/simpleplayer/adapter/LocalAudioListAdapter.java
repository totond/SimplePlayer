package yanzhikai.simpleplayer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import yanzhikai.simpleplayer.R;
import yanzhikai.simpleplayer.model.AudioInfo;
import yanzhikai.simpleplayer.model.PlayList;

/**
 * author : yany
 * e-mail : yanzhikai_yjk@qq.com
 * time   : 2018/01/12
 * desc   :
 */

public class LocalAudioListAdapter extends RecyclerView.Adapter {
    public static final String TAG = "yjkAdapter";
    private Context mContext;
    private List<AudioInfo> mAudioInfos;
    private LocalListItemOnClickListener mListener;
    private int mCurrentIndex = -1;

    private boolean isEditMode = false;
    private SparseBooleanArray mSelectedPositions = new SparseBooleanArray();
    private ListOnSelectListener mSelectListener;

    public LocalAudioListAdapter(Context context, List<AudioInfo> audioInfos) {
        mContext = context;
        mAudioInfos = audioInfos;
    }

    private void setItemChecked(int position, boolean isChecked) {
        mSelectedPositions.put(position, isChecked);
    }

    //根据位置判断条目是否选中
    private boolean isItemChecked(int position) {
        return mSelectedPositions.get(position);
    }

    public void clearSelected(){
        mSelectedPositions.clear();
    }

    //全选
    public void selectAllItems(){
        for (int i = 0; i < mSelectedPositions.size(); i++){
            mSelectedPositions.put(i, true);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: ");
        View view = LayoutInflater.from(mContext).inflate(R.layout.play_list_item, null, false);
        return new LocalAudioListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final LocalAudioListViewHolder localAudioListViewHolder = (LocalAudioListViewHolder) holder;
        localAudioListViewHolder.tv_song_name.setText(mAudioInfos.get(position).getSongName());
        localAudioListViewHolder.tv_singer_name.setText(mAudioInfos.get(position).getSingerName());
        localAudioListViewHolder.tv_duration.setText(mAudioInfos.get(position).getDurationText());
        localAudioListViewHolder.tv_index.setText(String.valueOf(position));
        localAudioListViewHolder.setEditMode(isEditMode);
        localAudioListViewHolder.cb_check.setChecked(isItemChecked(position));

        localAudioListViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEditMode) {

                    if (isItemChecked(position)) {

                        setItemChecked(position, false);
                    } else {
                        setItemChecked(position, true);
                    }
                    if (mSelectListener != null) {
                        mSelectListener.onItemSelected(position);
                    }
                    notifyItemChanged(position);
                } else {
                    if (mListener != null) {
                        mListener.onItemClick(position);
                    }
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mAudioInfos.size();
    }

    private class LocalAudioListViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_song_name, tv_singer_name, tv_duration, tv_index;
        public AudioInfo audioInfo;
        public int audioIndex;
        public View itemView;
        public CheckBox cb_check;

        public LocalAudioListViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            tv_song_name = itemView.findViewById(R.id.tv_song_name);
            tv_singer_name = itemView.findViewById(R.id.tv_singer_name);
            tv_duration = itemView.findViewById(R.id.tv_duration);
            tv_index = itemView.findViewById(R.id.tv_index);
            cb_check = itemView.findViewById(R.id.cb_check);

        }

        public void setEditMode(boolean editMode) {
            if (editMode) {
                cb_check.setVisibility(View.VISIBLE);
                tv_index.setVisibility(View.INVISIBLE);
            } else {
                cb_check.setVisibility(View.GONE);
                tv_index.setVisibility(View.VISIBLE);
            }
        }

    }


    public ArrayList<AudioInfo> getSelectedItem() {
        ArrayList<AudioInfo> selectList = new ArrayList<>();
        for (int i = 0; i < mAudioInfos.size(); i++) {
            if (isItemChecked(i)) {
                selectList.add(mAudioInfos.get(i));
            }
        }
        return selectList;
    }

    public void setEditMode(boolean isEditMode) {
        this.isEditMode = isEditMode;
    }

    public boolean getEditMode(){
        return isEditMode;
    }

    public void setSelectListener(ListOnSelectListener selectListener) {
        this.mSelectListener = selectListener;
    }

    public void setOnClickListener(LocalListItemOnClickListener listener) {
        this.mListener = listener;
    }

    public interface LocalListItemOnClickListener {
        void onItemClick(int index);
    }

    public interface ListOnSelectListener {
        void onItemSelected(int index);
    }
}
