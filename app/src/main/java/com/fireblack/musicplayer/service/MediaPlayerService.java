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
import com.fireblack.musicplayer.utils.Common;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

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
    final Semaphore mSemaphore = new Semaphore(1);
    private SongDao songDao;
    private boolean isFirst;//是否是启动后第一次播放
    private String parameter;//查询参数
    private String latelyStr;//最近播放歌曲拼接而成的字符串

    private String isStartUp;
    private boolean isPrepare = false;
    private boolean isDeleteSop = false;

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
        PendingIntent nextPendingIntent = PendingIntent.getService(this, 1, new Intent(MediaPlayerManager.SERVICE_ACTION)
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
        resetPlayerList();//重置播放歌曲列表
    }

    /**
     * 重置播放歌曲列表
     */
    public  void resetPlayerList() {
        switch (playerFlag){
            case MediaPlayerManager.PLAYERFLAG_ALL:
                list = songDao.searchAll();
                break;
            case MediaPlayerManager.PLAYERFLAG_ALBUM:
                list = songDao.searchAlbum(parameter);
                break;
            case MediaPlayerManager.PLAYERFLAG_ARTIST:
                list = songDao.searchArtist(parameter);
                break;
            case MediaPlayerManager.PLAYERFLAG_DOWNLOAD:
                list = songDao.searchDownload();
                break;
            case MediaPlayerManager.PLAYERFLAG_FOLDER:
                list = songDao.searchFolder(parameter);
                break;
            case MediaPlayerManager.PLAYERFLAG_LATELY:
                list = songDao.searLately(getLatelyStr());
                break;
            case MediaPlayerManager.PLAYERFLAG_LIKE:
                list = songDao.searchIsLike();
                break;
            case MediaPlayerManager.PLAYERFLAG_PLAYERLIST:
                list = songDao.searchPlayerList(parameter);
                break;
            case MediaPlayerManager.PLAYERFLAG_WEB:
                //解析XML文件
                break;
            default:
                break;

        }
    }


    /**
     * 播放
     */
    private void player(){
        if(playerFlag != MediaPlayerManager.PLAYERFLAG_WEB){
            //准备状态
            playerstate = MediaPlayerManager.STATE_PREPARE;
        }else {//网络音乐-缓冲
            playerstate = MediaPlayerManager.STATE_BUFFER;
        }
        if(mPlayer.isPlaying()){
            mPlayer.stop();
        }
        if(song != null){
            String name = song.getName();
            //判断标题是否存在
            if(TextUtils.isEmpty(name)){
                name = Common.clearSuffix(song.getDisplayName());
            }
            showPrepare();
        }
    }

    /**
     * 播放或暂停
     */
    public void pauseOrPlayer(){
        if(mPlayer.isPlaying()){
            mPlayer.pause();
            currentDuration = mPlayer.getCurrentPosition();
            playerstate = MediaPlayerManager.STATE_PAUSE;
        }else {
            //判断是否是启动后第一次播放
            if(isFirst){
                if(song != null){
                    player(song.getId(),playerFlag,parameter);
                }else {
                    currentDuration = 0;
                }
            }else {
                if(isPrepare){
                    player();
                }else {
                    mPlayer.start();
                }
            }
            playerstate = MediaPlayerManager.STATE_PLAYER;
        }
    }

    /**
     *根据指定条件播放
     */
    public void player(int id,int playerFlag,String parameter){
        if(this.playerFlag != playerFlag){
            this.playerFlag = playerFlag;
            this.parameter = parameter;
            resetPlayerList();
        }
        this.playerFlag = playerFlag;
        this.parameter = parameter;
        if(playerFlag != MediaPlayerManager.PLAYERFLAG_WEB){
            if(song != null){
                if(song.getId() != id){
                    isFirst = false;
                }
            }
            song = songDao.searchById(id);
            playerstate = MediaPlayerManager.STATE_PLAYER;
        }else {
            for (Song s : list) {
                if(s.getId() == id){
                    song = s;
                    isFirst = false;
                    break;
                }
            }
        }
        if(playerMode == MediaPlayerManager.MODE_RANDOM){
            randomIds.clear();
            randomIds.add(song.getId());
        }
        player();
    }

    /**
     * 显示播放信息
     */
    private void showPrepare() {
        Intent intent = new Intent(MediaPlayerManager.BROADCASTRECEVIER_ACTON);
        intent.putExtra("flag",MediaPlayerManager.FLAG_PREPARE);
        intent.putExtra("title",getTitle());
        intent.putExtra("currentPosition",isFirst?currentDuration:0);
//        intent.putExtra("duration",get)
        sendBroadcast(intent);
    }

    /**
     * 返回当前播放歌曲标题
     */
    public String getTitle() {
        if(song == null){
            return "未知歌曲";
        }
        //判断标题是否存在
        if(TextUtils.isEmpty(song.getName())){
            return Common.clearSuffix(song.getDisplayName());
        }
        return song.getArtist().getName() + "-" + song.getName();
    }
    public int getPlayerDuration(){
        if(song == null){
            return 0;
        }
        int durationTime = song.getDurationTime();
        if(durationTime == -1){
//            song.setDurationTime();
        }
        return durationTime;
    }

    /**
     * 获取当前播放歌曲的Id
     */
    public int getSongId(){
        if(song == null){
            return -1;
        }
        return song.getId();
    }

    /**
     * 获取当前播放状态
     */
    public int getPlayerState(){
        return playerstate;
    }

    /**
     * 获取当前播放Flag
     */
    public int getPlayerFlag(){
        return playerFlag;
    }

    /**
     * @获取最近播放字符串
     */
    public String getLatelyStr() {
        if(TextUtils.isEmpty(latelyStr)){
            return null;
        }
        return latelyStr.substring(0,latelyStr.length() - 1);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }
}
