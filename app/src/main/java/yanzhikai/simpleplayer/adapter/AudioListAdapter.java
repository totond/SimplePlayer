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

/**
 * author : yany
 * e-mail : yanzhikai_yjk@qq.com
 * time   : 2018/01/30
 * desc   :
 */

public class AudioListAdapter extends RecyclerView.Adapter {
    public static final String TAG = "yjkAdapter";
    private Context mContext;
    private List<AudioInfo> mAudioInfos;
    private BaseOnItemClickListener mListener;
    private int mCurrentIndex = -1;

    private boolean isEditMode = false;
    private SparseBooleanArray mSelectedPositions = new SparseBooleanArray();

    public AudioListAdapter(Context context, List<AudioInfo> audioInfos) {
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
        if (getItemCount() > 0) {
            setItemChecked(getItemCount() - 1, mSelectedPositions.get(getItemCount() - 1));
            for (int i = 0; i < mSelectedPositions.size(); i++) {
                setItemChecked(i, true);
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: ");
        View view = LayoutInflater.from(mContext).inflate(R.layout.audio_list_item, null, false);
        return new AudioListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final AudioListViewHolder audioListViewHolder = (AudioListViewHolder) holder;
        audioListViewHolder.tv_song_name.setText(mAudioInfos.get(position).getSongName());
        audioListViewHolder.tv_singer_name.setText(mAudioInfos.get(position).getSingerName());
        audioListViewHolder.tv_duration.setText(mAudioInfos.get(position).getDurationText());
        audioListViewHolder.tv_index.setText(String.valueOf(position));
        audioListViewHolder.setEditMode(isEditMode);
        audioListViewHolder.cb_check.setChecked(isItemChecked(position));

        audioListViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEditMode) {

                    if (isItemChecked(position)) {
                        setItemChecked(position, false);
                    } else {
                        setItemChecked(position, true);
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

    private class AudioListViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_song_name, tv_singer_name, tv_duration, tv_index;
        public AudioInfo audioInfo;
        public int audioIndex;
        public View itemView;
        public CheckBox cb_check;

        public AudioListViewHolder(View itemView) {
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


    //获取选中Item信息
    public ArrayList<AudioInfo> getSelectedItem() {
        ArrayList<AudioInfo> selectList = new ArrayList<>();
        for (int i = 0; i < mAudioInfos.size(); i++) {
            if (isItemChecked(i)) {
                selectList.add(mAudioInfos.get(i));
            }
        }
        return selectList;
    }

    public List<AudioInfo> getAudioInfos() {
        return mAudioInfos;
    }

    public void setEditMode(boolean isEditMode) {
        this.isEditMode = isEditMode;
    }

    public boolean getEditMode(){
        return isEditMode;
    }


    public void setAudioInfos(List<AudioInfo> audioInfos) {
        this.mAudioInfos = audioInfos;
    }

    public void setOnClickListener(BaseOnItemClickListener listener) {
        this.mListener = listener;
    }
}
