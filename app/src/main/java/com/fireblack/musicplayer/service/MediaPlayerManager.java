package com.fireblack.musicplayer.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by ChengHao on 2016/5/17.
 */
public class MediaPlayerManager {
    private MediaPlayerService mMediaPlayerService;
    private ContextWrapper mContextWrapper;
    private ServiceConnectionListener serviceConnectionListener;

    public static final String SERVICE_ACTION = "com.fireblack.musicplayer.service.mediaplayer";
    public static String BROADCASTRECEVIER_ACTON = "com.fireblack.musicplayer.brocast";

    public static final int FLAG_CHANGED=0;//更新前台
    public static final int FLAG_PREPARE=1;//准备状态
    public static final int FLAG_INIT=2;//初始化数据
    public static final int FLAG_LIST=3;//自动播放时，更新前台列表状态
    public static final int FLAG_BUFFERING=4;//网络音乐-缓冲数据


    //MediaPlayerService onStart flag
    public static final int SERVICE_RESET_PLAYLIST = 0;//更新播放列表
    public static final int SERVICE_MUSIC_PAUSE = 1;//暂停
    public static final int SERVICE_MUSIC_PLAYERORPAUSE = 2;//播放/暂停
    public static final int SERVICE_MUSIC_PREV = 3;//上一首
    public static final int SERVICE_MUSIC_NEXT = 4;//下一首
    public static final int SERVICE_MUSIC_STOP = 5;//停止播放


    //播放模式
    public static final int MODE_CIRCLELIST=0;//顺序播放
    public static final int MODE_RANDOM=1;//随机播放
    public static final int MODE_CIRCLEONE=2;//单曲循环
    public static final int MODE_SEQUENCE=3;//列表循环

    //播放列表
    public static final int PLAYERFLAG_WEB=0;//网络
    public static final int PLAYERFLAG_ALL=1;//全部
    public static final int PLAYERFLAG_ARTIST=2;//歌手
    public static final int PLAYERFLAG_ALBUM=3;//专辑
    public static final int PLAYERFLAG_FOLDER=4;//文件夹
    public static final int PLAYERFLAG_PLAYERLIST=5;//播放列表
    public static final int PLAYERFLAG_LIKE=6;//我最爱听
    public static final int PLAYERFLAG_LATELY=7;//最近播放
    public static final int PLAYERFLAG_DOWNLOAD=9;//下载完成

    //播放状态
    public static final int STATE_NULL=0;//空闲
    public static final int STATE_BUFFER=1;//缓冲
    public static final int STATE_PAUSE=2;//暂停
    public static final int STATE_PLAYER=3;//播放
    public static final int STATE_PREPARE=4;//准备
    public static final int STATE_OVER=5;//播放结束
    public static final int STATE_STOP=6;//停止

    public MediaPlayerManager(ContextWrapper mContextWrapper) {
        this.mContextWrapper = mContextWrapper;
    }

    public interface ServiceConnectionListener{
        public void onServiceConnected();
        public void onServiceDisconnected();
    }

    public void setConnectionListener(ServiceConnectionListener listener){
        serviceConnectionListener = listener;
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMediaPlayerService = ((MediaPlayerService.MyBind)service).getService();
            if(serviceConnectionListener != null){
                serviceConnectionListener.onServiceConnected();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mMediaPlayerService = null;
            if(serviceConnectionListener != null){
                serviceConnectionListener.onServiceDisconnected();
            }
        }
    };

    /**
     * 开始服务并绑定服务
     */
    public void startAndBindService(){
        mContextWrapper.startService(new Intent(SERVICE_ACTION));
        mContextWrapper.bindService(new Intent(SERVICE_ACTION), serviceConnection, Context.BIND_AUTO_CREATE);
    }

    public void unbindService(){
        mContextWrapper.unbindService(serviceConnection);
    }

    /**
     * 初始化歌曲信息-在播放界面进入时
     */
    public void initPlayerSongInfo(){
        if(mMediaPlayerService != null){
            mMediaPlayerService.initPlayerSongInfo();
        }
    }

    /**
     * 初始化歌曲信息-扫描之后
     * */
    public void initScanner_SongInfo(){
        if(mMediaPlayerService!=null){
            mMediaPlayerService.initScanner_SongInfo();
        }
    }

    /**
     * 获取最近播放 ————字符串
     */
    public String getLatelyStr(){
        if(mMediaPlayerService != null){
            return mMediaPlayerService.getLatelyStr();
        }
        return null;
    }

    public void pauseOrPlayer(){
        if(mMediaPlayerService != null){
            Log.e("12345","OK");
            mMediaPlayerService.pauseOrPlayer();
        }else{
            Log.e("12345","no song");
        }
    }

    /**
     * 根据指定条件播放
     * */
    public void player(int id,int playerFlag,String parameter){
        if(mMediaPlayerService!=null){
            mMediaPlayerService.player(id,playerFlag,parameter);
        }
    }

    /**
     * 获取当前播放歌曲的Id
     */
    public int getSongId(){
        if(mMediaPlayerService != null){
            return mMediaPlayerService.getSongId();
        }
        return -1;
    }

    /**
     * 获取当前播放状态
     */
    public int getPlayerState(){
        if(mMediaPlayerService != null){
            return mMediaPlayerService.getPlayerState();
        }
        return -1;
    }


    /**
     * 获取当前播放Flag
     */
    public int getPlayerFlag(){
        if(mMediaPlayerService != null){
            return mMediaPlayerService.getPlayerFlag();
        }
        return -1;
    }
}
