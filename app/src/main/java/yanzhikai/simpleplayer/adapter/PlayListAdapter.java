package yanzhikai.simpleplayer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import yanzhikai.simpleplayer.R;
import yanzhikai.simpleplayer.model.AudioInfo;

/**
 * author : yany
 * e-mail : yanzhikai_yjk@qq.com
 * time   : 2018/01/12
 * desc   :
 */

public class PlayListAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private ArrayList<AudioInfo> mAudioInfos;
    private PlayListItemOnClickListener mListener;

    public PlayListAdapter(Context context, ArrayList<AudioInfo> audioInfos){
        mContext = context;
        mAudioInfos = audioInfos;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.play_list_item, null, false);
        return new PlayListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        PlayListViewHolder playListViewHolder = (PlayListViewHolder) holder;
        playListViewHolder.tv_song_name.setText(mAudioInfos.get(position).getSongName());
        playListViewHolder.tv_singer_name.setText(mAudioInfos.get(position).getSingerName());
        playListViewHolder.tv_duration.setText(mAudioInfos.get(position).getDurationText());
        playListViewHolder.audioInfo = mAudioInfos.get(position);
    }

    @Override
    public int getItemCount() {
        return mAudioInfos.size();
    }

    private class PlayListViewHolder extends RecyclerView.ViewHolder{
        public TextView tv_song_name,tv_singer_name,tv_duration;
        public AudioInfo audioInfo;

        public PlayListViewHolder(View itemView) {
            super(itemView);
            tv_song_name = itemView.findViewById(R.id.tv_song_name);
            tv_singer_name = itemView.findViewById(R.id.tv_singer_name);
            tv_duration = itemView.findViewById(R.id.tv_duration);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null){
                        mListener.onItemClick(audioInfo);
                        Log.d("yjk", "onItemClick: " + audioInfo.getSongName());
                    }
                }
            });
        }

    }

    public void setListener(PlayListItemOnClickListener listener) {
        this.mListener = listener;
    }

    public interface PlayListItemOnClickListener{
        void onItemClick(AudioInfo info);
    }
}
