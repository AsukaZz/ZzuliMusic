package com.example.he.zzulimusic.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.he.zzulimusic.R;
import com.example.he.zzulimusic.adapter.MyFragmentAdapter;
import com.example.he.zzulimusic.application.MainApplication;
import com.example.he.zzulimusic.bean.Mp3Info;
import com.example.he.zzulimusic.fragment.LocalMusicFragment;
import com.example.he.zzulimusic.fragment.MyFragment;
import com.example.he.zzulimusic.fragment.NetMusicFragment;
import com.example.he.zzulimusic.service.PlayService;
import com.example.he.zzulimusic.utils.PermissionsUtil;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MyFragment.OnFragmentInteractionListener {
    private List<String> list_title;
    private List<Fragment> list_fragment;


    private LocalMusicFragment localMusicFragment;
    private NetMusicFragment netMusicFragment;

    private Intent intent;




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        TabLayout tl = (TabLayout) findViewById(R.id.tabLayout);

        list_title = new ArrayList<>();
        list_title.add("本地音乐");
        list_title.add("网络音乐");

        localMusicFragment = LocalMusicFragment.newInstance();
        netMusicFragment = NetMusicFragment.newInstance();

        //将fragmeng装进列表中
        list_fragment = new ArrayList<>();
        list_fragment.add(localMusicFragment);
        list_fragment.add(netMusicFragment);

        ViewPager vp = (ViewPager) findViewById(R.id.viewPager);
        MyFragmentAdapter mfa = new MyFragmentAdapter(getSupportFragmentManager(), list_fragment, list_title);
        vp.setAdapter(mfa);
        tl.setupWithViewPager(vp);
        //先将服务器启动起来，然后进行绑定和解除绑定，服务不会被结束
        //否则，解除绑定时，服务会自动被回收
        intent = new Intent(this,PlayService.class);
        startService(intent);
    }

    @Override
    protected void onDestroy() {
        stopService(intent);
        super.onDestroy();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }


    /**
     * 播放音乐
     *
     * @param position
     */
    public boolean play(List<Mp3Info> mp3Infos,int position) {
        MainApplication.mp3Infos = mp3Infos;
        Log.i("测试", "本地音乐的歌曲id: "+mp3Infos.get(position).getId());

        Intent intent = new Intent("play");
        intent.putExtra("updateList",true);
        intent.putExtra("position",position);
        sendBroadcast(intent);

        return true;

    }

}
