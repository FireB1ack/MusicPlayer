package com.fireblack.musicplayer.service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.fireblack.musicplayer.custom.Setting;
import com.fireblack.musicplayer.dao.SongDao;
import com.fireblack.musicplayer.entity.Song;
import com.fireblack.musicplayer.utils.Common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * Created by ChengHao on 2016/5/17.
 */
public class MediaPlayerService extends Service {

    private final IBinder mBinder = new MyBind();
    private MediaPlayer mPlayer;
    private List<Song> list;//播放歌曲列表
    private Song song;
    private int playerFlag;//播放列表
    private int playerState;//播放状态
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
        return mBinder;
    }

    public class MyBind extends Binder{
        public MediaPlayerService getService(){
            return MediaPlayerService.this;
        }
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
        Log.e("12345",playerFlag + "setting");
        if(TextUtils.isEmpty(s_player_flag)){
            playerFlag = MediaPlayerManager.PLAYERFLAG_ALL;
            Log.e("12345",playerFlag + "All");
        } else {
            playerFlag = Integer.valueOf(s_player_flag);
        }
        resetPlayerList();//重置播放歌曲列表
        playerState = MediaPlayerManager.STATE_PAUSE;
        if(TextUtils.isEmpty(s_playerId) || s_playerId.equals("-1")){
            if(list.size() != 0){
                song = list.get(0);
                Log.e("12345",playerFlag + "playerFlag");
                Log.e("12345",song.getId() + "获得的ID");
                Log.e("12345",song.getDisplayName() + "获得的名字");
            }else {
                playerState = MediaPlayerManager.STATE_NULL;
            }
        }
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
     * 初始化歌曲信息-在播放界面进入时
     */
    public void initPlayerSongInfo(){
        Intent intent = new Intent(MediaPlayerManager.BROADCASTRECEVIER_ACTON);
        intent.putExtra("flag",MediaPlayerManager.FLAG_INIT);
        intent.putExtra("title",getTitle());
        intent.putExtra("currentPosition",currentDuration);
        intent.putExtra("duration",getPlayerDuration());
        intent.putExtra("albumPic", getAlbumPic());
        intent.putExtra("playerMode",playerMode);
        intent.putExtra("playerState",playerState);
        sendBroadcast(intent);
    }

    /**
     * 播放
     */
    private void player(){
//        isRun = false;
        if(playerFlag != MediaPlayerManager.PLAYERFLAG_WEB){
            //准备状态
            playerState = MediaPlayerManager.STATE_PREPARE;
        }else {//网络音乐-缓冲
            playerState = MediaPlayerManager.STATE_BUFFER;
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
            Log.e("12345", "pause");
            mPlayer.pause();
            currentDuration = mPlayer.getCurrentPosition();
            playerState = MediaPlayerManager.STATE_PAUSE;
        }else {
            //判断是否是启动后第一次播放
            if(isFirst){
                Log.e("12345", "isFirst");
                if(song != null){
                    Log.e("12345", String.valueOf(song.getId()));
                    player(song.getId(), playerFlag, parameter);
                }else {
                    Log.e("12345", "currention");
                    currentDuration = 0;
                }
            }else {
                if(isPrepare){
                    Log.e("12345", "prepare");
                    player();
                }else {
                    Log.e("12345", "start");
                    mPlayer.start();
                }
            }
            playerState = MediaPlayerManager.STATE_PLAYER;
        }
    }

    /**
     *根据指定条件播放
     */
    public void player(int id,int playerFlag,String parameter){
        if(this.playerFlag != playerFlag){
            Log.e("1234567",String.valueOf(this.playerFlag));
            Log.e("12345",String.valueOf(playerFlag));
            this.playerFlag = playerFlag;
            this.parameter = parameter;
            resetPlayerList();
        }
        this.playerFlag = playerFlag;
        this.parameter = parameter;
        if(playerFlag != MediaPlayerManager.PLAYERFLAG_WEB){
            Log.e("12345","not web");
            if(song != null){
                if(song.getId() != id){
                    isFirst = false;
                }
            }
            song = songDao.searchById(id);
            playerState = MediaPlayerManager.STATE_PLAYER;
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
     * 显示播放信息,发送广播
     */
    private void showPrepare() {
        Intent intent = new Intent(MediaPlayerManager.BROADCASTRECEVIER_ACTON);
        intent.putExtra("flag",MediaPlayerManager.FLAG_PREPARE);
        intent.putExtra("title",getTitle());
        intent.putExtra("currentPosition",isFirst?currentDuration:0);
        intent.putExtra("duration",getPlayerDuration());
        intent.putExtra("albumPic", song.getAlbum().getPicPath());
        sendBroadcast(intent);
    }


    /**
     * 获取当前播放歌曲的时长
     */
    public int getPlayerDuration(){
        if(song == null){
            return 0;
        }
        int durationTime = song.getDurationTime();
        if(durationTime == -1){//先判断扫描文件是否获取了歌曲时长
            song.setDurationTime(getSongDurationTIme(song.getId(), song.getDurationTime()));
        }
        return durationTime;
    }

    private  int getSongDurationTIme(int id,int durationTime){
        int time = durationTime;
        MediaPlayer player = MediaPlayer.create(this, Uri.parse(song.getFilePath()));
        try {
            player.prepare();
            time = player.getDuration();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            player.release();
            player = null;
        }
        if(time != -1){
            songDao.updateByDuration(id, time);
        }
        return time;
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
        return playerState;
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

    public int getPlayerMode(){
        return playerMode;
    }

    /**
     * @获取当前播放歌曲的进度
     */
    public  int getPlayerProgress(){
        return currentDuration;
    }

    /**
     * 获取专辑图片
     */
    public String getAlbumPic(){
        if(song == null){
            return null;
        }
        return song.getAlbum().getPicPath();
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
