package com.fireblack.musicplayer.custom;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by ChengHao on 2016/5/17.
 * 用于设置SharedPreferences和皮肤
 */
public class Setting {
    public static final String PREFERENCE_NAME = "com.fireblack.musicplayer.setting";
    public static final String KEY_ISSTARTUP = "isStartUp";//是否是刚启动
    public static final String KEY_PLAYER_ID = "player_id";//歌曲ID
    public static final String KEY_PLAYER_CURRENTDURATION = "player_currentduration";//已经播放时长
    public static final String KEY_PLAYER_MODE = "player_mode";//播放模式
    public static final String KEY_PLAYER_FLAG = "player_flag";//播放列表
    public static final String KEY_PLAYER_PARAMETER = "player_parameter";//播放列表查询参数
    public static final String KEY_PLAYER_LATELY = "player_lately";//最近播放
    public static final String KEY_ISSCANNERTIP = "isScannerTip";//是否显示要扫描提示

    private SharedPreferences preferences;

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
}
