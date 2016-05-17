package com.fireblack.musicplayer.service;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.fireblack.musicplayer.custom.Setting;
import com.fireblack.musicplayer.dao.SongDao;
import com.fireblack.musicplayer.entity.Song;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by ChengHao on 2016/5/17.
 */
public class MediaPlayerService extends Service {

    private MediaPlayer mPlayer;
    private List<Song> list;//播放歌曲列表
    private Song song;
    private int playerFlag;//播放列表
    private int playerstate;//播放状态
    private int playerMode;//播放模式
    private int currentDuration = 0;//已经播放时长
    private List<Integer> randomIds;
    private ExecutorService mExecutorService; //线程池
    private SongDao songDao;
    private boolean isFirst;//是否是启动后第一次播放
    private String parameter;//查询参数
    private String latelyStr;//最近播放歌曲拼接而成的字符串

    private String isStartUp;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mPlayer = new MediaPlayer();
        list = new ArrayList<Song>();
        randomIds = new ArrayList<Integer>();
        mExecutorService = Executors.newCachedThreadPool();
        songDao = new SongDao(this);
        isFirst = true;

        //下一首
        PendingIntent nextPendingIntent = PendingIntent.getService(this,1,new Intent(MediaPlayerManager.SERVICE_ACTION)
                                                                    .putExtra("flag", MediaPlayerManager.SERVICE_MUSIC_NEXT),
                                                                    PendingIntent.FLAG_UPDATE_CURRENT);
        //播放或暂停动作
        PendingIntent playerPendingIntent=PendingIntent.getService(this, 2, new Intent(MediaPlayerManager.SERVICE_ACTION)
                                                                    .putExtra("flag", MediaPlayerManager.SERVICE_MUSIC_PLAYERORPAUSE)
                                                                    , PendingIntent.FLAG_UPDATE_CURRENT);
        //上一首动作
        PendingIntent prevPendingIntent=PendingIntent.getService(this, 3, new Intent(MediaPlayerManager.SERVICE_ACTION)
                                                                    .putExtra("flag", MediaPlayerManager.SERVICE_MUSIC_PREV)
                                                                    , PendingIntent.FLAG_UPDATE_CURRENT);

        init();
    }

    /**
     * 初始化信息
     */
    private void init() {
        //从SharedPrefers中获取保存的信息
        Setting setting = new Setting(this,false);
        isStartUp = setting.getValue(Setting.KEY_ISSTARTUP);
        String s_player_flag = setting.getValue(Setting.KEY_PLAYER_FLAG);
        parameter = setting.getValue(Setting.KEY_PLAYER_PARAMETER);
        String s_playerId = setting.getValue(Setting.KEY_PLAYER_ID);
        String s_currentDuration = setting.getValue(Setting.KEY_PLAYER_CURRENTDURATION);
        String s_playerMode = setting.getValue(Setting.KEY_PLAYER_MODE);
        latelyStr = setting.getValue(Setting.KEY_PLAYER_LATELY);
        if(TextUtils.isEmpty(s_player_flag)){
            playerFlag = MediaPlayerManager.PLAYERFLAG_ALL;
        } else {
            playerFlag = Integer.valueOf(s_player_flag);
        }
        resetPlayerList();
    }

    /**
     * 重置播放歌曲列表
     */
    public  void resetPlayerList() {
        switch (playerFlag){
            case MediaPlayerManager.PLAYERFLAG_ALL:

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }
}
