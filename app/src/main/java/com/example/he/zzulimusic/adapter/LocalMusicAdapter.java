package com.example.he.zzulimusic.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.he.zzulimusic.R;
import com.example.he.zzulimusic.utils.MediaUtil;
import com.example.he.zzulimusic.bean.Mp3Info;

import java.util.ArrayList;

/**
 * Created by he on 2017/3/1.
 */

public class LocalMusicAdapter extends RecyclerView.Adapter<LocalMusicAdapter.ViewHolder> {
    private LayoutInflater layoutInflater;//用于布局加载
    private ArrayList<Mp3Info> mp3Infos;//存储音乐列表
    private Context context;

    private  OnItemClickListener onItemClickListener;

    public LocalMusicAdapter(Context context,ArrayList<Mp3Info> mp3Infos){
        this.context = context;
        this.mp3Infos = mp3Infos;
        this.layoutInflater = LayoutInflater.from(context);
    }

    /**
     * 创建一个回调接口
     */
    public interface OnItemClickListener{
        void onItemClick(int position);
        void onItemLongClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_music_layout,parent,false);
        ViewHolder viewHolder = new ViewHolder(view,onItemClickListener);
        return  viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Mp3Info mp3Info = mp3Infos.get(position);
        holder.textView1_title.setText(mp3Info.getTitle());
        holder.textView2_singer.setText(mp3Info.getArtist());
        holder.textView3_time.setText(MediaUtil.formatTime1(mp3Info.getDuration()));
        holder.imageView1_icon.setImageBitmap(MediaUtil.getArtwork(context,mp3Info.getId(),mp3Info.getAlbumId(),true,true));
        holder.mp3Infos = this.mp3Infos;
    }

    @Override
    public int getItemCount() {
        return mp3Infos.size();
    }


    public  static class  ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView textView1_title;
        public TextView textView2_singer;
        public TextView textView3_time;
        public ImageView imageView1_icon;
        private OnItemClickListener onItemClickListener;
        private ArrayList<Mp3Info> mp3Infos;//存储音乐列表

        public ViewHolder(View itemView,OnItemClickListener onItemClickListener) {
            super(itemView);
            textView1_title = (TextView) itemView.findViewById(R.id.textView1_title);
            textView2_singer = (TextView) itemView.findViewById(R.id.textView2_singer);
            textView3_time = (TextView) itemView.findViewById(R.id.textView3_time);
            imageView1_icon = (ImageView) itemView.findViewById(R.id.imageView1_icon);
            this.onItemClickListener = onItemClickListener;
            itemView.setOnClickListener(this);
        }

        /**
         * 实现OnClickListener接口重写的方法
         * @param v
         */
        @Override
        public void onClick(View v) {
            if (onItemClickListener != null){
                onItemClickListener.onItemClick(getPosition());
                onItemClickListener.onItemLongClick(getPosition());
            }
        }
    }

}
