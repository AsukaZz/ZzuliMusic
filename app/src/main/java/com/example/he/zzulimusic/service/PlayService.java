package com.example.he.zzulimusic.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v7.app.NotificationCompat;
import android.telephony.TelephonyManager;

import com.bumptech.glide.Glide;
import com.example.he.zzulimusic.Constant;
import com.example.he.zzulimusic.R;
import com.example.he.zzulimusic.activity.MainActivity;
import com.example.he.zzulimusic.application.MainApplication;
import com.example.he.zzulimusic.bean.Mp3Info;
import com.example.he.zzulimusic.utils.MediaUtil;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

/**
 * Created by he on 2017/3/15.
 */

public class PlayService extends Service {
    private MediaPlayer mediaPlayer;
    private int currentPosition;//当前正在播放的歌曲的位置
    List<Mp3Info> mp3Infos;
    public static final int ORDER_PLAY = 1;//顺序播放
    public static final int RANDOM_PLAY = 2;//随机播放
    public static final int SINGLE_PLAY = 3;//单曲循环
    private int play_mode = ORDER_PLAY;//播放模式，默认顺序播放
    private Random random = new Random();//创建随机对象
    private Notification mNotification;
    private NotificationManager mNotificationManager;

    private Timer mTimer;
    private TimerTask mTimerTask;
    private boolean isTimerStart = false;

    private Context mContext;
    private SystemReceiver mReceiver;//处理电话广播

    public void setMp3Infos(List<Mp3Info> mp3Infos) {
        this.mp3Infos = mp3Infos;
    }

    public int getPlay_mode() {
        return play_mode;
    }

    public void setPlay_mode(int play_mode) {
        this.play_mode = play_mode;
    }

    public PlayService() {

    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    public int getPlayPosition() {
        return mediaPlayer.getCurrentPosition() / 1000;
    }

    //内部类PlayBinder实现Binder
    public class PlayBinder extends Binder {
        public PlayService getPlayService() {
            return PlayService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new PlayBinder();//通过PlayBinder拿到PlayService,给Activity调用
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mp3Infos = MediaUtil.getMp3Infos(this);//获取Mp3列表
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                switch (play_mode) {
                    case ORDER_PLAY://顺序播放
                        next();
                        break;
                    case RANDOM_PLAY://随机播放
                        play(mContext, random.nextInt(mp3Infos.size()));
                        break;
                    case SINGLE_PLAY://单曲循环
                        play(mContext, currentPosition);
                        break;
                    default:
                        break;
                }
            }
        });
        mReceiver = new SystemReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
        intentFilter.addAction("android.intent.action.PHONE_STATE");
        intentFilter.addAction("play");
        intentFilter.addAction("pause");
        intentFilter.addAction("next");
        intentFilter.addAction("prev");
        intentFilter.addAction("close");
        registerReceiver(mReceiver, intentFilter);
        //----定时器记录播放进度---//
        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(Constant.RECEIVER_PLAY_POSITION);
                intent.putExtra("position", mediaPlayer.getCurrentPosition() / 1000);
                mContext.sendBroadcast(intent);
            }
        };
    }

    //播放
    public void play(Context context, int position) {
        if (mp3Infos == null)
            return;
        mContext = context;
        if (position >= 0 && position < mp3Infos.size()) {
            Mp3Info mp3Info = mp3Infos.get(position);
            try {
                mediaPlayer.reset();//复位
                mediaPlayer.setDataSource(this, Uri.parse(mp3Info.getUrl()));//资源解析，Mp3地址
                mediaPlayer.prepare();//准备
                mediaPlayer.start();//开始播放
                setupNotification(mp3Info);
                currentPosition = position;//保存当前位置到currentPosition
                MainApplication.position = position;
                MainApplication.isPlaying = mediaPlayer.isPlaying();
                Intent intent = new Intent(Constant.RECEIVER_MUSIC_CHANGE);
                intent.putExtra("position", currentPosition);
                intent.putExtra("isPlaying", true);
                mContext.sendBroadcast(intent);

                if (!isTimerStart) {
                    mTimer.schedule(mTimerTask, 0, 500);//如果没有启动，则启动
                    isTimerStart = true;
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    //暂停
    public void pause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            MainApplication.isPlaying = mediaPlayer.isPlaying();
            setupNotification(mp3Infos.get(currentPosition));
            Intent intent = new Intent(Constant.RECEIVER_MUSIC_CHANGE);
            intent.putExtra("position", currentPosition);
            intent.putExtra("isPlaying", false);
            mContext.sendBroadcast(intent);
        }
    }

    //下一首
    public void next() {
        if (currentPosition >= mp3Infos.size() - 1) {
            currentPosition = 0;//回到第一首
        } else {
            currentPosition++; //下一首
        }
        play(mContext, currentPosition);
    }

    //上一首
    public void previous() {
        if (currentPosition - 1 < 0) {
            currentPosition = mp3Infos.size() - 1;//回到最后一首
        } else {
            currentPosition--;
        }
        play(mContext, currentPosition);
    }

    public void start() {//继续播放
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            setupNotification(mp3Infos.get(currentPosition));
            Intent intent = new Intent(Constant.RECEIVER_MUSIC_CHANGE);
            intent.putExtra("position", currentPosition);
            intent.putExtra("isPlaying", true);
            mContext.sendBroadcast(intent);
        }
    }


    @Override
    public void onDestroy() {
        mediaPlayer.stop();
        //解除注册接收者
        unregisterReceiver(mReceiver);
        if (isTimerStart) {
            mTimer.cancel();
            isTimerStart = false;
        }
        super.onDestroy();
    }

    public Mp3Info getMusic() {
        return mp3Infos.get(currentPosition);
    }

    private void setupNotification(final Mp3Info mp3Info) {
        if (mp3Info.getPicUri() == null) {//本地歌曲
            setupNotification(mp3Info, MediaUtil.getArtwork(this, mp3Info.getId(), mp3Info.getAlbumId(), true, true));
        } else {
            new AsyncTask<String, Void, Bitmap>() {

                @Override
                protected Bitmap doInBackground(String... params) {
                    Bitmap bitmap = null;
                    try {
                        bitmap = Glide.with(PlayService.this).load(params[0]).asBitmap().into(100, 100).get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    return bitmap;
                }

                @Override
                protected void onPostExecute(Bitmap bitmap) {
                    setupNotification(mp3Info, bitmap);
                }
            }.execute(mp3Info.getPicUri());
        }
    }

    private void setupNotification(final Mp3Info mp3Info, Bitmap bitmap) {
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle(mp3Info.getTitle());
        builder.setContentText(mp3Info.getArtist());
        builder.setSmallIcon(R.mipmap.music);
        builder.setLargeIcon(bitmap);
        builder.setDefaults(NotificationCompat.FLAG_FOREGROUND_SERVICE);
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, 0);
        builder.setContentIntent(pendingIntent);
        //新建意图，并设置action标记为"play"，用于接收广播时过滤意图信息
        Intent intentPlay = new Intent("play");
        PendingIntent pendingIntentPlay = PendingIntent.getBroadcast(this, 0, intentPlay, 0);

        Intent intentPause = new Intent("pause");
        PendingIntent pendingIntentPause = PendingIntent.getBroadcast(this, 0, intentPause, 0);

        Intent intentNext = new Intent("next");
        PendingIntent pendingIntentNext = PendingIntent.getBroadcast(this, 0, intentNext, 0);

        Intent intentPrev = new Intent("prev");
        PendingIntent pendingIntentPrev = PendingIntent.getBroadcast(this, 0, intentPrev, 0);

        Intent intentClose = new Intent("close");
        PendingIntent pendingIntentClose = PendingIntent.getBroadcast(this, 0, intentClose, 0);

        //第一个参数是图标资源Id 第二个是图标显示的名称 第三个图标点击要启动的PendingIntnet
        builder.addAction(R.mipmap.ic_skip_previous_white_24dp, "", pendingIntentPrev);
        if (isPlaying()) {
            builder.addAction(R.mipmap.uamp_ic_pause_white_24dp, "", pendingIntentPause);
        } else {
            builder.addAction(R.mipmap.uamp_ic_play_arrow_white_24dp, "", pendingIntentPlay);
        }
        builder.addAction(R.mipmap.ic_skip_next_white_24dp, "", pendingIntentNext);
        builder.addAction(R.mipmap.ic_close_black_24dp, "", pendingIntentClose);
        NotificationCompat.MediaStyle style = new NotificationCompat.MediaStyle();
        style.setMediaSession(new MediaSessionCompat(this, "MeidaSession", new ComponentName(this, Intent.ACTION_MEDIA_BUTTON), null).getSessionToken());
        //CancelButton在5.0以下的机器有效
        style.setCancelButtonIntent(pendingIntent);
        style.setShowCancelButton(true);
        //设置要显示在通知右方的图标 最多三个
        style.setShowActionsInCompactView(2, 3);
        builder.setStyle(style);
        builder.setShowWhen(false);
        mNotification = builder.build();
        startForeground(1, mNotification);
    }


    /**
     * 打电话时暂停播放
     */
    public class SystemReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("play".equals(intent.getAction())) {
                boolean isUpdateList = intent.getBooleanExtra("updateList", false);
                int position = intent.getIntExtra("position", 0);
                if (isUpdateList) {
                    mp3Infos = MainApplication.mp3Infos;
                    play(getApplicationContext(), position);
                } else {
                    start();
                }
            } else if ("pause".equals(intent.getAction())) {
                pause();
            } else if ("prev".equals(intent.getAction())) {
                previous();
            } else if ("next".equals(intent.getAction())) {
                next();
            } else if ("close".equals(intent.getAction())) {
                stopForeground(true);
                pause();
            }

            // 如果是打电话
            else if (Intent.ACTION_NEW_OUTGOING_CALL.equals(intent.getAction())) {
                pause();
            } else {
                // 如果是来电
                TelephonyManager tm = (TelephonyManager) context
                        .getSystemService(Service.TELEPHONY_SERVICE);
                switch (tm.getCallState()) {
                    // 响铃
                    case TelephonyManager.CALL_STATE_RINGING:
                        pause();
                        break;
                    // 摘机
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        pause();
                        break;
                    // 空闲
                    case TelephonyManager.CALL_STATE_IDLE:
                        start();
                        break;
                }
            }
        }
    }

}
