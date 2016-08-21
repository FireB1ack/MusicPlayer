package com.fireblack.musicplayer.custom;

import android.content.Context;
import android.content.SharedPreferences;

import com.fireblack.musicplayer.R;

/**
 * Created by ChengHao on 2016/5/17.
 * 用于设置SharedPreferences和皮肤
 */
public class Setting {

    /**
     * 系统设置的保存的文件名
     * */
    public static final String PREFERENCE_NAME = "com.fireblack.musicplayer.setting";
    /**
     * SD卡下载歌曲目录
     * */
    public static final String DOWNLOAD_MUSIC_DIRECTORY="/MusicPlayer/download_music/";
    /**
     * SD卡下载歌词目录
     * */
    public static final String DOWNLOAD_LYRIC_DIRECTORY="/MusicPlayer/download_lyric/";
    /**
     * SD卡下载专辑图片目录
     * */
    public static final String DOWNLOAD_ALBUM_DIRECTORY="/MusicPlayer/download_album/";
    /**
     * SD卡下载歌手图片目录
     * */
    public static final String DOWNLOAD_ARTIST_DIRECTORY="/MusicPlayer/download_artist/";

    public static final String KEY_SKINID = "skin_id";
    public static final String KEY_ISSTARTUP = "isStartUp";//是否是刚启动
    public static final String KEY_PLAYER_ID = "player_id";//歌曲ID
    public static final String KEY_PLAYER_CURRENTDURATION = "player_currentduration";//已经播放时长
    public static final String KEY_PLAYER_MODE = "player_mode";//播放模式
    public static final String KEY_PLAYER_FLAG = "player_flag";//播放列表
    public static final String KEY_PLAYER_PARAMETER = "player_parameter";//播放列表查询参数
    public static final String KEY_PLAYER_LATELY = "player_lately";//最近播放
    public static final String KEY_ISSCANNERTIP = "isScannerTip";//是否显示要扫描提示
    public static final String KEY_AUTO_SLEEP="sleep";//定时关闭时间
    public static final String KEY_BRIGHTNESS="brightness";//屏幕模式 1:正常模式 0:夜间模式
    public static final float KEY_DARKNESS=0.1f;//夜间模式值level
    private SharedPreferences preferences;
    /**
     * 皮肤资源的ID数组
     * */
    public static final int[] SKIN_RESOURCES = { R.drawable.main_bg01,
            R.drawable.main_bg02, R.drawable.main_bg03, R.drawable.main_bg04,
            R.drawable.main_bg05, R.drawable.main_bg06 };


    public Setting(Context context, boolean isWrite) {
        preferences = context.getSharedPreferences(PREFERENCE_NAME,
                isWrite?Context.MODE_WORLD_READABLE:Context.MODE_WORLD_WRITEABLE);
    }

    /**
     * 获取数据
     */
    public String getValue(String key){
        return preferences.getString(key,null);
    }

    /**
     * 设置数据
     */
    public void setValue(String key,String value){
        SharedPreferences.Editor  editor = preferences.edit();
        editor.putString(key,value);
        editor.commit();
    }

    /**
     * 保存播放信息[0:歌曲Id 1:已经播放时长 2:播放模式3:播放列表Flag 4:播放列表查询参数 5:最近播放的]
     * */
    public void setPlayerInfo(String[] playerInfos){
        SharedPreferences.Editor it = preferences.edit();
        it.putString(KEY_PLAYER_ID, playerInfos[0]);
        it.putString(KEY_PLAYER_CURRENTDURATION, playerInfos[1]);
        it.putString(KEY_PLAYER_MODE, playerInfos[2]);
        it.putString(KEY_PLAYER_FLAG, playerInfos[3]);
        it.putString(KEY_PLAYER_PARAMETER, playerInfos[4]);
        it.putString(KEY_PLAYER_LATELY, playerInfos[5]);
        it.commit();
    }

    /**
     * 获取皮肤资源ID
     * */
    public int getCurrentSkinResId() {
        int skinIndex = preferences.getInt(KEY_SKINID, 0);
        if (skinIndex >= SKIN_RESOURCES.length) {
            skinIndex = 0;
        }
        return SKIN_RESOURCES[skinIndex];
    }

    /**
     * 获取皮肤Id
     * */
    public int getCurrentSkinId(){
        int skinIndex = preferences.getInt(KEY_SKINID, 0);
        if (skinIndex >= SKIN_RESOURCES.length) {
            skinIndex = 0;
        }
        return skinIndex;
    }

    /**
     * 设置皮肤资源ID
     * */
    public void setCurrentSkinResId(int skinIndex) {
        SharedPreferences.Editor it = preferences.edit();
        it.putInt(KEY_SKINID, skinIndex);
        it.commit();
    }
}
