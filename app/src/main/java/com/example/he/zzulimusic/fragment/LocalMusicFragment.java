package com.example.he.zzulimusic.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.he.zzulimusic.Constant;
import com.example.he.zzulimusic.R;
import com.example.he.zzulimusic.activity.MainActivity;
import com.example.he.zzulimusic.adapter.LocalMusicAdapter;
import com.example.he.zzulimusic.bean.Mp3Info;
import com.example.he.zzulimusic.utils.DividerItemDecoration;
import com.example.he.zzulimusic.utils.MediaUtil;

import java.util.ArrayList;


public class LocalMusicFragment extends Fragment {
    //private ListView listView_my_music;
    private RecyclerView recyclerView;
    private ArrayList<Mp3Info> mp3Infos;
    //private MyMusicListAdapter myMusicListAdapter;
    private LocalMusicAdapter localMusicAdapter;
    private MainActivity mainActivity;
    private View view;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }
    public LocalMusicFragment(){

    }

    public static LocalMusicFragment newInstance(){
        LocalMusicFragment fragment = new LocalMusicFragment();
        return  fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_local_music,null);
        //listView_my_music = (ListView) view.findViewById(R.id.listView);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(mainActivity));
        recyclerView.addItemDecoration(new DividerItemDecoration(mainActivity, LinearLayout.VERTICAL));
        loadData();
        return view;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(getActivity());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.RECEIVER_LOCAL_REFRESH);
        BroadcastReceiver mItemViewListClickReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent){
               loadData();
            }
        };
        broadcastManager.registerReceiver(mItemViewListClickReceiver, intentFilter);
    }


    private  void loadData(){
        mp3Infos = (ArrayList<Mp3Info>) MediaUtil.getMp3Infos(mainActivity);
        Log.i("测试","mp3info-=="+mp3Infos.size());
        //myMusicListAdapter = new MyMusicListAdapter(mainActivity,mp3Infos);
        //listView_my_music.setAdapter(myMusicListAdapter);
        localMusicAdapter = new LocalMusicAdapter(mainActivity,mp3Infos);
        localMusicAdapter.setOnItemClickListener(new LocalMusicAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(int position) {
                mainActivity.play(mp3Infos,position);
            }

            @Override
            public void onItemLongClick(int position) {

            }
        });
        recyclerView.setAdapter(localMusicAdapter);
    }



}
