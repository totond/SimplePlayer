package yanzhikai.simpleplayer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import yanzhikai.simpleplayer.R;
import yanzhikai.simpleplayer.model.AudioInfo;
import yanzhikai.simpleplayer.model.AudioListInfo;

/**
 * author : yany
 * e-mail : yanzhikai_yjk@qq.com
 * time   : 2018/01/29
 * desc   :
 */

public class ShowListAdapter extends RecyclerView.Adapter{
    public static final String TAG = "yjkAdapter";
    private Context mContext;
    private List<AudioListInfo> mAudioListInfos;
    private BaseOnItemClickListener mListener;

    private boolean isEditMode = false;
    private SparseBooleanArray mSelectedPositions = new SparseBooleanArray();

    public ShowListAdapter(Context context, List<AudioListInfo> audioInfos) {
        mContext = context;
        mAudioListInfos = audioInfos;
    }

    private void setItemChecked(int position, boolean isChecked) {
//        Log.d(TAG, "setItemChecked: " + position + " state: " + isChecked);
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
        //让数组启动初始化
        setItemChecked(getItemCount() - 1, mSelectedPositions.get(getItemCount() - 1));

        for (int i = 0; i < mSelectedPositions.size(); i++){
            setItemChecked(i,true);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: ");
        View view = LayoutInflater.from(mContext).inflate(R.layout.show_audio_list_item, null, false);
        return new ShowAudioListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ShowAudioListViewHolder showAudioListViewHolder = (ShowAudioListViewHolder) holder;
        showAudioListViewHolder.tv_list_name.setText(mAudioListInfos.get(position).getListName());
        showAudioListViewHolder.tv_list_size.setText(mAudioListInfos.get(position).getInfoList().size() + "首");
        showAudioListViewHolder.setEditMode(isEditMode);

        showAudioListViewHolder.cb_check.setChecked(isItemChecked(position));

        showAudioListViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
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
        return mAudioListInfos.size();
    }

    private class ShowAudioListViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_list_name, tv_list_size;
        public AudioInfo audioInfo;
        public View itemView;
        public CheckBox cb_check;
        public ImageView iv_enter;

        public ShowAudioListViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            tv_list_name = itemView.findViewById(R.id.tv_list_name);
            tv_list_size = itemView.findViewById(R.id.tv_list_size);
            cb_check = itemView.findViewById(R.id.cb_check);
            iv_enter = itemView.findViewById(R.id.iv_enter);
        }

        public void setEditMode(boolean editMode) {
            if (editMode) {
                cb_check.setVisibility(View.VISIBLE);
                iv_enter.setVisibility(View.INVISIBLE);
            } else {
                cb_check.setVisibility(View.GONE);
                iv_enter.setVisibility(View.VISIBLE);
            }
        }

    }


    public ArrayList<AudioListInfo> getSelectedItem() {
        ArrayList<AudioListInfo> selectList = new ArrayList<>();
        for (int i = 0; i < mAudioListInfos.size(); i++) {
            if (isItemChecked(i)) {
                selectList.add(mAudioListInfos.get(i));
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

    public void setAudioListInfos(List<AudioListInfo> audioListInfos) {
        this.mAudioListInfos = audioListInfos;
    }


    public void setOnClickListener(BaseOnItemClickListener listener) {
        this.mListener = listener;
    }

    public interface ListOnSelectListener {
        void onItemSelected(int index);
    }
}

