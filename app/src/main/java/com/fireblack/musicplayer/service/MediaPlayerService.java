package com.fireblack.musicplayer.service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.fireblack.musicplayer.R;
import com.fireblack.musicplayer.custom.Setting;
import com.fireblack.musicplayer.dao.SongDao;
import com.fireblack.musicplayer.entity.Song;
import com.fireblack.musicplayer.utils.Common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * Created by ChengHao on 2016/5/17.
 */
public class MediaPlayerService extends Service {

    private final IBinder mBinder = new MyBind();

    // 播放动作
    private static final int ACTION_NEXT = 1;// 下一首播放
    private static final int ACTION_PREVIOUS = 2;// 上一首播放
    private static final int ACTION_AUTO = 0;// 自动执行下一首
    private MediaPlayer mPlayer;
    private List<Song> list;//播放歌曲列表
    private Song song;
    private int playerFlag;//播放列表
    private int playerState;//播放状态
    private int playerMode;//播放模式
    private int currentDuration = 0;//已经播放时长
    private List<Integer> randomIds;
    private ExecutorService mExecutorService; //线程池
    private boolean isRun = true;
    final Semaphore mSemaphore = new Semaphore(1);
    private SongDao songDao;
    private boolean isFirst;//是否是启动后第一次播放
    private boolean isDeleteStop=false;
    private String parameter;//查询参数
    private String latelyStr;//最近播放歌曲拼接而成的字符串

    private String isStartUp;
    private boolean isPrepare = true;
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

        // 申请wake lock保证了CPU维持唤醒状态
        mPlayer.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
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
        playerState = MediaPlayerManager.STATE_PAUSE;
        if(TextUtils.isEmpty(s_playerId) || s_playerId.equals("-1")){
            if(list.size() != 0){
                song = list.get(0);
            }else {
                playerState = MediaPlayerManager.STATE_NULL;
            }
        }else {
            // 网络歌曲列表，遍历查找
            if (playerFlag == MediaPlayerManager.PLAYERFLAG_WEB) {
                for (Song s : list) {
                    if (s.getId() == Integer.valueOf(s_playerId)) {
                        song = s;
                        break;
                    }
                }
                // 网络歌曲找不到了，就播放全部歌曲
                if (song == null) {
                    playerFlag = MediaPlayerManager.PLAYERFLAG_ALL;
                    resetPlayerList();
                    if (list.size() != 0) {
                        song = list.get(0);
                    } else {
                        playerState = MediaPlayerManager.STATE_NULL;
                    }
                }
            } else {
                song = songDao.searchById(Integer.valueOf(s_playerId));
            }
        }
        if (!TextUtils.isEmpty(s_currentDuration)) {
            currentDuration = Integer.valueOf(s_currentDuration);
        }
        if (TextUtils.isEmpty(s_playerMode)) {
            playerMode = MediaPlayerManager.MODE_CIRCLELIST;
        } else {
            playerMode = Integer.valueOf(s_playerMode);
            if (playerMode == MediaPlayerManager.MODE_CIRCLEONE) {
                mPlayer.setLooping(true);
            }
            if(playerMode==MediaPlayerManager.MODE_RANDOM){
                if(song!=null)
                    randomIds.add(song.getId());
            }
        }
    }


    /**
     * 重置播放歌曲列表
     */
    public  void resetPlayerList() {
        switch (playerFlag){
            case MediaPlayerManager.PLAYERFLAG_ALL:
                Log.e("12345","传入的playerFlag"+playerFlag);
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
     * 初始化歌曲信息-扫描之后
     * */
    public void initScanner_SongInfo() {
        if (playerState != MediaPlayerManager.STATE_NULL) {
            return;
        }
        resetPlayerList();
        playerState = MediaPlayerManager.STATE_PAUSE;
        if (list.size() != 0) {
            song = list.get(0);
        } else {
            playerState = MediaPlayerManager.STATE_NULL;
        }
        Intent it = new Intent(MediaPlayerManager.BROADCASTRECEVIER_ACTON);
        it.putExtra("flag", MediaPlayerManager.FLAG_INIT);
        it.putExtra("currentPosition", currentDuration);
        it.putExtra("duration", getPlayerDuration());
        it.putExtra("title", getTitle());
        it.putExtra("albumPic", getAlbumPic());
        sendBroadcast(it);
    }

    /**
     * 播放
     */
    private void player(){
        isRun = false;
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
        mExecutorService.execute(new MediaPlayerRunnable());
    }

    /**
     * 准备
     */
    private void prepare(String path){
        try {
            mPlayer.setDataSource(path);
            mPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
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
     * 播放
     * */
    private void doPlayer(int action,boolean isPlayer) {
        switch (playerMode) {
            case MediaPlayerManager.MODE_CIRCLELIST:// 顺序播放
                if (list.size() == 1) {
                    // 播放完毕后，就没有了
                    if (action != ACTION_AUTO) {
                        if(isPlayer){
                            player();
                        }else{
                            showPrepare();
                        }
                    } else {
                        playerOver();
                    }
                } else {
                    int index = -1;
                    for (int i = 0, len = list.size(); i < len; i++) {
                        // 在列表中查找播放歌曲的位置
                        if (list.get(i).getId() == song.getId()) {
                            index = i;
                            break;
                        }
                    }
                    if (index != -1) {
                        if (action == ACTION_AUTO || action == ACTION_NEXT) {
                            // 有下一首
                            if (index < (list.size() - 1)) {
                                song = list.get(index + 1);
                                if(isPlayer){
                                    player();
                                }else{
                                    showPrepare();
                                }
                            } else {
                                if (action == ACTION_AUTO) {
                                    playerOver();
                                } else {
                                    song = list.get(0);
                                    if(isPlayer){
                                        player();
                                    }else{
                                        showPrepare();
                                    }
                                }
                            }
                        } else {// 上一首
                            if (index > 0) {
                                song = list.get(index - 1);
                                if(isPlayer){
                                    player();
                                }else{
                                    showPrepare();
                                }
                            } else {
                                if (action == ACTION_AUTO) {
                                    playerOver();
                                } else {
                                    song = list.get(list.size() - 1);
                                    if(isPlayer){
                                        player();
                                    }else{
                                        showPrepare();
                                    }
                                }
                            }
                        }
                    }
                }
                break;
            case MediaPlayerManager.MODE_RANDOM:// 随机播放
                if (list.size() == 1) {
                    if(isPlayer){
                        player();
                    }else{
                        showPrepare();
                    }
                } else if (list.size() == 2) {
                    if (song.getId() == list.get(0).getId()) {// 当前播放歌曲是首位
                        song = list.get(1);
                    } else {
                        song = list.get(0);
                    }
                    if(isPlayer){
                        player();
                    }else{
                        showPrepare();
                    }
                } else {
                    if (action == ACTION_AUTO || action == ACTION_NEXT) {
                        // 下一首随机时防止重复该歌曲
                        int random_index = -1;
                        while (random_index == -1) {
                            random_index = new Random().nextInt(list.size());
                            if (song.getId() != list.get(random_index).getId()) {
                                break;
                            }
                            random_index = -1;
                        }
                        song = list.get(random_index);
                        randomIds.add(song.getId());
                    } else {
                        if (randomIds.size() > 1) {
                            int len=randomIds.size();
                            int j=0;
                            Song s=null;
                            //查找上一首歌曲，找不到继续上一首...仍是没有找到，那就随机播放一首
                            for (int i=len-1;i>0;i--) {
                                s=searchPrevRandomSong(randomIds.get(i - 1));
                                if(s!=null){
                                    j=i-1;
                                    break;
                                }
                            }
                            if(s==null){
                                randomIds.clear();
                                song=null;
                                //随机播放一首
                                if(list.size()>0){
                                    int random_index = new Random().nextInt(list.size());
                                    song=list.get(random_index);
                                    randomIds.add(song.getId());
                                }
                            }else{
                                //移除上一首所有后面的数据
                                for (int i = len-1; i >j; i--) {
                                    randomIds.remove(i);
                                }
                                song=s;
                            }
                        }
                    }
                    if(isPlayer){
                        player();
                    }else{
                        showPrepare();
                    }
                }
                break;
            case MediaPlayerManager.MODE_SEQUENCE:// 列表循环
                if (list.size() == 1) {
                    if(isPlayer){
                        player();
                    }else{
                        showPrepare();
                    }
                } else {
                    int index = -1;
                    for (int i = 0, len = list.size(); i < len; i++) {
                        // 在列表中查找播放歌曲的位置
                        if (list.get(i).getId() == song.getId()) {
                            index = i;
                            break;
                        }
                    }
                    if (index != -1) {
                        if (action == ACTION_AUTO || action == ACTION_NEXT) {
                            // 有下一首
                            if (index < (list.size() - 1)) {
                                song = list.get(index + 1);
                            } else {
                                song = list.get(0);
                            }
                            if(isPlayer){
                                player();
                            }else{
                                showPrepare();
                            }
                        } else {// 上一首
                            if (index > 0) {
                                song = list.get(index - 1);
                            } else {
                                song = list.get(list.size() - 1);
                            }
                            if(isPlayer){
                                player();
                            }else{
                                showPrepare();
                            }
                        }
                    }
                }
                break;
        }
    }


    /**
     * 列表播放完毕时
     * */
    private void playerOver() {
        playerState = MediaPlayerManager.STATE_OVER;
        song = null;
        currentDuration = 0;
        Intent it = new Intent(MediaPlayerManager.BROADCASTRECEVIER_ACTON);
        it.putExtra("flag", MediaPlayerManager.FLAG_INIT);
        it.putExtra("currentPosition", 0);
        it.putExtra("duration", 0);
        it.putExtra("title", getTitle());
        it.putExtra("albumPic", getAlbumPic());
        sendBroadcast(it);

    }

    /**
     * 播放下一首
     * */
    public void nextPlayer() {
        doPlayer(ACTION_NEXT, true);
    }

    /**
     * 播放上一首
     * */
    public void previousPlayer() {
        doPlayer(ACTION_PREVIOUS,true);
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
     * 查找上一首随机播放歌曲是否存在
     * */
    private Song searchPrevRandomSong(int prevrandomid){
        for (int i = 0,len=list.size(); i < len; i++) {
            Song s=list.get(i);
            if(s.getId()==prevrandomid){
                return s;
            }
        }
        return null;
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
    public void onStart(Intent intent, int startId) {
        super.onStart(intent,startId);
        if(intent.getAction()!=null&&intent.getAction().equals(MediaPlayerManager.SERVICE_ACTION)){
            int flag=intent.getIntExtra("flag", -1);
            if(flag==MediaPlayerManager.SERVICE_RESET_PLAYLIST){
                resetPlayerList();
            }else if(flag==MediaPlayerManager.SERVICE_MUSIC_PAUSE){
                if(playerState==MediaPlayerManager.STATE_PLAYER){
                    pauseOrPlayer();
                    sendBroadcast(new Intent(MediaPlayerManager.BROADCASTRECEVIER_ACTON).putExtra("flag", MediaPlayerManager.FLAG_LIST));
                }
            }else if(flag==MediaPlayerManager.SERVICE_MUSIC_STOP){
                //停止音乐
                stop();
            }else if(flag==MediaPlayerManager.SERVICE_MUSIC_PLAYERORPAUSE){

                if(isStartUp==null||isStartUp.equals("true")){
                    isStartUp="false";
                    new Setting(this, true).setValue(Setting.KEY_ISSTARTUP, "false");
                }
                if(playerState==MediaPlayerManager.STATE_NULL){
                    return;
                }
                //顺序列表播放结束
                if(playerState==MediaPlayerManager.STATE_OVER){
                    return;
                }
                pauseOrPlayer();

            }else if(flag==MediaPlayerManager.SERVICE_MUSIC_NEXT){

                if(isStartUp==null||isStartUp.equals("true")){
                    isStartUp="false";
                    new Setting(this, true).setValue(Setting.KEY_ISSTARTUP, "false");
                }
                nextPlayer();
            }else if(flag==MediaPlayerManager.SERVICE_MUSIC_PREV){

                if(isStartUp==null||isStartUp.equals("true")){
                    isStartUp="false";
                    new Setting(this, true).setValue(Setting.KEY_ISSTARTUP, "false");
                }
                previousPlayer();
            }
        }
    }

    /**
     * 停止服务时
     * */
    public void stop() {
        // 保存数据[0:歌曲Id 1:已经播放时长 2:播放模式3:播放列表Flag4:播放列表查询参数 5:最近播放的]
        Setting setting = new Setting(this, true);
        String[] playerInfos = new String[6];
        playerInfos[0] = String.valueOf(getSongId());
        if (playerFlag == MediaPlayerManager.PLAYERFLAG_WEB) {
            currentDuration = 0;
        }
        playerInfos[1] = String.valueOf(currentDuration);
        playerInfos[2] = String.valueOf(playerMode);
        playerInfos[3] = String.valueOf(playerFlag);
        playerInfos[4] = parameter;
        playerInfos[5] = latelyStr;
        setting.setPlayerInfo(playerInfos);
        setting.setValue(Setting.KEY_ISSTARTUP, "true");

        playerState = MediaPlayerManager.STATE_STOP;
        isRun = false;

        if(mPlayer!=null){
            if (mPlayer.isPlaying()) {
                mPlayer.stop();
            }
            mPlayer.release();
        }
        mPlayer = null;
        mExecutorService.shutdown();

        //停止服务
        stopSelf();
    }

    private class MediaPlayerRunnable implements Runnable {
        @Override
        public void run() {
            try {
                mSemaphore.acquire();
                if(song==null){
                    mSemaphore.release();
                    return;
                }
                // 最近播放列表（保存本地歌曲，不保存网络的）
                if (playerFlag != MediaPlayerManager.PLAYERFLAG_WEB) {
//                    addLately();
                }
                mPlayer.reset();
                if (playerFlag == MediaPlayerManager.PLAYERFLAG_WEB) {
                    prepare(song.getNetUrl());
                } else {
                    prepare(song.getFilePath());
                }
                // 是否是启动后，第一次播放
                if (isFirst) {
                    mPlayer.seekTo(currentDuration);
                }
                isFirst = false;
                mPlayer.start();
                isRun = true;
                isDeleteStop=false;
                isPrepare=false;
                playerState = MediaPlayerManager.STATE_PLAYER;
                while (isRun) {
                    if (playerState == MediaPlayerManager.STATE_PLAYER) {
                        currentDuration = mPlayer.getCurrentPosition();
                        sendBroadcast(new Intent(
                                MediaPlayerManager.BROADCASTRECEVIER_ACTON)
                                .putExtra("flag",
                                        MediaPlayerManager.FLAG_CHANGED)
                                .putExtra("currentPosition", currentDuration)
                                .putExtra("duration", mPlayer.getDuration()));
                        Thread.sleep(1000);
                    }
                }
                if (mPlayer != null
                        && playerState != MediaPlayerManager.STATE_OVER
                        && playerState != MediaPlayerManager.STATE_STOP) {
                    sendBroadcast(new Intent(
                            MediaPlayerManager.BROADCASTRECEVIER_ACTON)
                            .putExtra("flag", MediaPlayerManager.FLAG_CHANGED)
                            .putExtra("currentPosition",
                                    mPlayer.getCurrentPosition())
                            .putExtra("duration", mPlayer.getDuration()));
                }
                if(isPrepare){
                    sendBroadcast(new Intent(
                            MediaPlayerManager.BROADCASTRECEVIER_ACTON)
                            .putExtra("flag",
                                    MediaPlayerManager.FLAG_CHANGED)
                            .putExtra("currentPosition", 0)
                            .putExtra("duration", getPlayerDuration()));
                }

                currentDuration=0;
                if(isDeleteStop){
                    playerOver();
                }
                mSemaphore.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
