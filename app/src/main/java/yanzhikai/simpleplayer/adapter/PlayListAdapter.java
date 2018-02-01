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

import yanzhikai.simpleplayer.R;
import yanzhikai.simpleplayer.model.AudioInfo;
import yanzhikai.simpleplayer.model.PlayList;

/**
 * author : yany
 * e-mail : yanzhikai_yjk@qq.com
 * time   : 2018/01/12
 * desc   :
 */

public class PlayListAdapter extends RecyclerView.Adapter {
    public static final String TAG = "yjkAdapter";
    private Context mContext;
    private ArrayList<AudioInfo> mAudioInfos;
    private BaseOnItemClickListener mListener;
    private int mCurrentIndex = -1;

    private boolean isEditMode = false;
    private SparseBooleanArray mSelectedPositions = new SparseBooleanArray();
    private ListOnSelectListener mSelectListener;

    public PlayListAdapter(Context context, ArrayList<AudioInfo> audioInfos) {
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
        return new PlayListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final PlayListViewHolder playListViewHolder = (PlayListViewHolder) holder;
        playListViewHolder.tv_song_name.setText(mAudioInfos.get(position).getSongName());
        playListViewHolder.tv_singer_name.setText(mAudioInfos.get(position).getSingerName());
        playListViewHolder.tv_duration.setText(mAudioInfos.get(position).getDurationText());
        playListViewHolder.tv_index.setText(String.valueOf(position));
        playListViewHolder.setEditMode(isEditMode);
        playListViewHolder.cb_check.setChecked(isItemChecked(position));

        playListViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
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

        if (position == PlayList.getInstance().getCurrentIndex()) {
            playListViewHolder.itemView.setBackgroundResource(R.drawable.background_play_list_playing_item);
        } else {
            playListViewHolder.itemView.setBackgroundResource(R.drawable.background_play_list_item);
        }
    }

    @Override
    public int getItemCount() {
        return mAudioInfos.size();
    }

    private class PlayListViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_song_name, tv_singer_name, tv_duration, tv_index;
        public AudioInfo audioInfo;
        public int audioIndex;
        public View itemView;
        public CheckBox cb_check;

        public PlayListViewHolder(View itemView) {
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


    //刷新当前播放Item
    public void refreshItem() {
        Log.e(TAG, "refreshItem: " + mCurrentIndex);
        if (mCurrentIndex != -1) {
            notifyItemChanged(mCurrentIndex);
        }

        mCurrentIndex = PlayList.getInstance().getCurrentIndex();
        Log.e(TAG, "refreshItem a: " + mCurrentIndex);
        if (mCurrentIndex != -1) {
            notifyItemChanged(mCurrentIndex);
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

    public void setEditMode(boolean isEditMode) {
        this.isEditMode = isEditMode;
    }

    public boolean getEditMode(){
        return isEditMode;
    }

    public void setSelectListener(ListOnSelectListener selectListener) {
        this.mSelectListener = selectListener;
    }

    public void setOnClickListener(BaseOnItemClickListener listener) {
        this.mListener = listener;
    }



    public interface ListOnSelectListener {
        void onItemSelected(int index);
    }
}
