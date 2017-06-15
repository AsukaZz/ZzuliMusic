package com.example.he.zzulimusic.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.he.zzulimusic.Constant;
import com.example.he.zzulimusic.R;
import com.example.he.zzulimusic.activity.MusicPlayActivity;
import com.example.he.zzulimusic.application.MainApplication;
import com.example.he.zzulimusic.bean.Mp3Info;
import com.example.he.zzulimusic.utils.MediaUtil;

public class MusicPlayerFragment extends Fragment {

    private TextView tvSongName;
    private TextView tvSinger;
    private ImageView ivSongPic;
    private ImageView ivPlay;
    private ImageView ivNext;
    private ProgressBar pb_time;//进度条
    private boolean isPlaying = false;

    private BroadcastReceiver mReceiver;

    public MusicPlayerFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.music_play_control, container, false);

        tvSongName = (TextView) view.findViewById(R.id.textView_songName);
        tvSinger = (TextView) view.findViewById(R.id.textView3_singer);
        ivNext = (ImageView) view.findViewById(R.id.imageView3_next);
        ivPlay = (ImageView) view.findViewById(R.id.imageView3_play_pause);
        ivSongPic = (ImageView) view.findViewById(R.id.imageView3);
        pb_time = (ProgressBar) view.findViewById(R.id.pb_time);
        ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isPlaying) {
                    isPlaying = true;
                    Intent intent = new Intent("play");
                    getActivity().sendBroadcast(intent);
                } else {
                    Intent intent = new Intent("pause");
                    getActivity().sendBroadcast(intent);
                }
            }
        });

        ivNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("next");
                getActivity().sendBroadcast(intent);
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),MusicPlayActivity.class);
                startActivity(intent);
            }
        });
        mReceiver = new MusicChangeReceiver();
        IntentFilter intentFilter = new IntentFilter(Constant.RECEIVER_MUSIC_CHANGE);
        intentFilter.addAction(Constant.RECEIVER_PLAY_POSITION);
        getActivity().registerReceiver(mReceiver, intentFilter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (MainApplication.isPlaying) {
            onPlayStateChanged(MainApplication.position, true);
        }
    }

    private class MusicChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Constant.RECEIVER_MUSIC_CHANGE.equals(intent.getAction())) {
                int position = intent.getIntExtra("position", 0);
                boolean isplay = intent.getBooleanExtra("isPlaying", false);
                isPlaying = isplay;
                onPlayStateChanged(position, isplay);
            } else if (Constant.RECEIVER_PLAY_POSITION.equals(intent.getAction())) {
                int position = intent.getIntExtra("position", 0);
                onPlayStateChanged(position);
            }
        }

    }

    private void onPlayStateChanged(int position) {
        pb_time.setProgress(position);
    }

    private void onPlayStateChanged(int position, boolean isplay) {
        Mp3Info music = MainApplication.mp3Infos.get(position);
        if (music == null) {
            tvSongName.setText(Constant.DEFAULT_MUSIC_TITLE);
            tvSinger.setText(Constant.DEFAULT_MUSIC_ARTIST);
            ivSongPic.setImageResource(R.mipmap.music);
            ivPlay.setImageResource(R.mipmap.uamp_ic_play_arrow_white_48dp);
            return;
        }
        tvSongName.setText(music.getTitle());
        tvSinger.setText(music.getArtist());
        if (music.getPicUri() == null) {//如果是本地音乐
            ivSongPic.setImageBitmap(MediaUtil.getArtwork(getActivity(), music.getId(), music.getAlbumId(), true, true));
        } else {
            Glide.with(this).load(music.getPicUri()).into(ivSongPic);
        }
        pb_time.setMax((int) music.getDuration());//设置歌曲的时长
        pb_time.setProgress(0);//设置当前进度为0
        if (isplay) {
            ivPlay.setImageResource(R.mipmap.uamp_ic_pause_white_48dp);
        } else {
            ivPlay.setImageResource(R.mipmap.uamp_ic_play_arrow_white_48dp);
        }

    }
}
