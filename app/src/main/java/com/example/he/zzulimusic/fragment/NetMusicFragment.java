package com.example.he.zzulimusic.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.he.zzulimusic.R;
import com.example.he.zzulimusic.activity.MainActivity;
import com.example.he.zzulimusic.activity.NetMusicActivity;


public class NetMusicFragment extends Fragment {
    private MainActivity mainActivity;
    private ListView listView;
    private String[] musicType = {"新歌榜","热歌榜","经典老歌榜","欧美金曲榜","情歌对唱榜","网络歌曲榜"};
    private int[] typeValue = {1,2,22,21,23,25};//和musicType对应

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }

    public NetMusicFragment(){

    }

    public static NetMusicFragment newInstance(){
        NetMusicFragment fragment = new NetMusicFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_net_music,null);
        listView = (ListView) view.findViewById(R.id.listView);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(mainActivity,android.R.layout.simple_list_item_1,musicType);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(mainActivity, NetMusicActivity.class);
                intent.putExtra("type",typeValue[position]);
                intent.putExtra("size",10);
                intent.putExtra("offset",0);
                intent.putExtra("name",musicType[position]);//用来在ToolBar显示的文本
                startActivity(intent);
            }
        });
        return view;
    }
}
