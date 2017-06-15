package com.example.he.zzulimusic.application;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

import com.example.he.zzulimusic.bean.Mp3Info;

import java.io.File;
import java.util.List;

/**
 * Created by he on 2017/4/19.
 */

public class MainApplication extends Application {
    public static Context context;
    private static String rootPath = "/zzulimusic";
    public  static String lrcPath = "/lrc";
    public static List<Mp3Info> mp3Infos;
    public static int position = 0;
    public static  boolean isPlaying = false;
    public static int playMode;

    @Override
    public void onCreate() {
        super.onCreate();
        context  = this;
        initPath();
    }

    private void initPath() {
        String ROOT = "";
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            ROOT = Environment.getExternalStorageDirectory().getPath();
        }
        rootPath = ROOT + rootPath;
        lrcPath = rootPath + lrcPath;
        File lrcFile = new File(lrcPath);
        if(lrcFile.exists()){
            lrcFile.mkdirs();
        }

    }

}
