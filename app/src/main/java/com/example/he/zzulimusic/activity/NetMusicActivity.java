package com.example.he.zzulimusic.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.he.zzulimusic.Constant;
import com.example.he.zzulimusic.R;
import com.example.he.zzulimusic.adapter.NetMusicAdapter;
import com.example.he.zzulimusic.application.MainApplication;
import com.example.he.zzulimusic.bean.BillboardBean;
import com.example.he.zzulimusic.bean.Mp3Info;
import com.example.he.zzulimusic.utils.BaiduMusicUtils;
import com.example.he.zzulimusic.utils.MediaUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class NetMusicActivity extends AppCompatActivity {
    private Toolbar toolbar = null;
    private ImageView tbImage = null;//ToolBar显示的图片
    private RecyclerView recyclerView = null;
    private List<Mp3Info> mp3Infos = null;
    private BillboardBean billboardBean = null;//用于存储当前榜单信息
    private NetMusicAdapter adapter = null;
    private String strTitle;//存储ToolBar显示文本
    private int type = 0;
    private int size = 0;//每次读取的歌曲数量
    private int offset = 0;//读取歌曲的偏移量，用于分页加载
    private int billboardMusicCount = 0;
    private boolean loading = true;//是否正在加载
    int firstVisibleItem, visibleItemCount, totalItemCount;
    private LinearLayoutManager mLinearLayoutManager = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_music);


        //接收传递的参数
        type = getIntent().getIntExtra("type", 1);
        size = getIntent().getIntExtra("size", 10);
        offset = getIntent().getIntExtra("offset", 0);
        strTitle = getIntent().getStringExtra("name");

        //1,启动异步任务，加载榜单和音乐数据
        new LoadNetMusic().execute(type, size, offset);
        //2.ToolBar设置
        setToolbar();
        //3.初始化设置控件和事件处理
        initView();

    }

    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(strTitle);//设置ToolBar文本
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//显示左侧的箭头
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });//点击箭头的事件处理
    }

    private void initView() {
        tbImage = (ImageView) findViewById(R.id.app_bar_image);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mLinearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        //recyclerView.addItemDecoration();

        adapter = new NetMusicAdapter(this, null);
        adapter.setOnItemClickListener(new NetMusicAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(int position) {
                play((ArrayList<Mp3Info>) mp3Infos, position);
            }

            @Override
            public void onItemLongClick(final int position) {
                final String[] items = {"添加到播放列表", "下载"};
                AlertDialog.Builder listDialog =
                        new AlertDialog.Builder(NetMusicActivity.this);
                listDialog.setTitle("选择一项操作");
                listDialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Toast.makeText(NetMusicActivity.this, "待开发", Toast.LENGTH_SHORT).show();
                                break;
                            case 1:
                                download(position);
                                break;
                            default:
                                break;
                        }

                    }
                });
                listDialog.show();
            }


        });
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                visibleItemCount = recyclerView.getChildCount();
                totalItemCount = mLinearLayoutManager.getItemCount();
                firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();
                if (totalItemCount >= billboardMusicCount)
                    return;
                if (!loading && (totalItemCount - visibleItemCount) <= firstVisibleItem) {
                    offset += size;
                    new LoadNetMusic().execute(type, size, offset);
                    loading = true;
                }
            }
        });


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    /**
     * 播放音乐
     *
     * @param position
     */
    public boolean play(ArrayList<Mp3Info> mp3Infos, int position) {
        MainApplication.mp3Infos = mp3Infos;
        Log.i("测试", "网络音乐的歌曲id: "+mp3Infos.get(position).getId());
        Intent intent = new Intent("play");
        intent.putExtra("updateList", true);
        intent.putExtra("position", position);
        sendBroadcast(intent);
        return true;
    }


    private void download(final int position) {
        new AsyncTask<String, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(String... params) {
                File file = BaiduMusicUtils.downloadMp3(params[0], params[1]);
                //下载歌词和图片
                BaiduMusicUtils.downloadLrc(mp3Infos.get(position).getLrcLink(),mp3Infos.get(position).getTitle());
                BaiduMusicUtils.downloadPic(mp3Infos.get(position).getBigPicUri(),mp3Infos.get(position).getTitle());
                mediaScan(file);
                return true;
            }
        }.execute(mp3Infos.get(position).getUrl(), mp3Infos.get(position).getTitle());
        Toast.makeText(getApplicationContext(), "下载音乐完成", Toast.LENGTH_SHORT).show();
    }


    public void mediaScan(File file) {

        MediaScannerConnection.scanFile(this,
                new String[]{file.getAbsolutePath()}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                        Mp3Info mp3Info = MediaUtil.scanNewMp3Infos(NetMusicActivity.this,
                                uri);
//                        MusicDbUtils musicDbUtils = new MusicDbUtils(NetMusicActivity.this);
//                         musicDbUtils.musicInsert(mp3Info);
//                        musicDbUtils.addMusicToList("本地音乐",mp3Info.getId());
//                        musicDbUtils.addMusicToList("我的下载",mp3Info.getId());
                        Intent intent = new Intent(Constant.RECEIVER_LOCAL_REFRESH);
                        LocalBroadcastManager.getInstance(NetMusicActivity.this).sendBroadcast(intent);
                    }
                });

    }

    class LoadNetMusic extends AsyncTask<Integer, Integer, Boolean> {
        List<Mp3Info> musics = null;

        @Override
        protected Boolean doInBackground(Integer... params) {
            loading = true;

            musics = BaiduMusicUtils.getNetMusic(params[0], params[1], params[2]);
            if (params[2] == 0) {
                mp3Infos = musics;
                billboardBean = BaiduMusicUtils.getBillboardBean();
            } else {
                mp3Infos.addAll(musics);
            }
            if (musics == null)
                return false;
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean) {
                if (offset == 0) {
                    Glide.with(getApplication()).load(billboardBean.getPic_s444()).into(tbImage);
                    billboardMusicCount = Integer.parseInt(billboardBean.getBillboard_songnum());
                    adapter.setMp3Infos((ArrayList<Mp3Info>) musics);
                }
                adapter.notifyDataSetChanged();
                loading = false;
            }
        }
    }


}
