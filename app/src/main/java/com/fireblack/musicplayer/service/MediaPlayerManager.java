package com.fireblack.musicplayer.service;

/**
 * Created by ChengHao on 2016/5/17.
 */
public class MediaPlayerManager {

    public static final String SERVICE_ACTION = "com.fireblack.musicplayer.service.mediaplayer";

    //MediaPlayerService onStart flag
    public static final int SERVICE_RESET_PLAYLIST = 0;//更新播放列表
    public static final int SERVICE_MUSIC_PAUSE = 1;//暂停
    public static final int SERVICE_MUSIC_PLAYERORPAUSE = 2;//播放/暂停
    public static final int SERVICE_MUSIC_PREV = 3;//上一首
    public static final int SERVICE_MUSIC_NEXT = 4;//下一首
    public static final int SERVICE_MUSIC_STOP = 5;//停止播放

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
}
