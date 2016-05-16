package com.fireblack.musicplayer.utils;

import com.fireblack.musicplayer.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ChengHao on 2016/5/13.
 */
public class Common {

    /**
     * 获取音乐列表类别
     */
    public static List<HashMap<String,Object>> getListMusicData(){
        List<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();

        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("icon", String.valueOf(R.drawable.local_allsongs));
        map.put("title", "全部歌曲");
        map.put("icon2", String.valueOf(R.drawable.playlist_sign));
        data.add(map);

        HashMap<String, Object> map2 = new HashMap<String, Object>();
        map2.put("icon", String.valueOf(R.drawable.local_singer));
        map2.put("title", "歌手");
        map2.put("icon2", String.valueOf(R.drawable.playlist_sign));
        data.add(map2);

        HashMap<String, Object> map3 = new HashMap<String, Object>();
        map3.put("icon", String.valueOf(R.drawable.local_album));
        map3.put("title", "专辑");
        map3.put("icon2", String.valueOf(R.drawable.playlist_sign));
        data.add(map3);

        HashMap<String, Object> map4 = new HashMap<String, Object>();
        map4.put("icon", String.valueOf(R.drawable.local_file));
        map4.put("title", "文件夹");
        map4.put("icon2", String.valueOf(R.drawable.playlist_sign));
        data.add(map4);

        HashMap<String, Object> map5 = new HashMap<String, Object>();
        map5.put("icon", String.valueOf(R.drawable.local_custom));
        map5.put("title", "播放列表");
        map5.put("icon2", String.valueOf(R.drawable.playlist_sign));
        data.add(map5);

        HashMap<String, Object> map6 = new HashMap<String, Object>();
        map6.put("icon", String.valueOf(R.drawable.local_custom_like));
        map6.put("title", "我最爱听");
        map6.put("icon2", String.valueOf(R.drawable.playlist_sign));
        data.add(map6);

        HashMap<String, Object> map7 = new HashMap<String, Object>();
        map7.put("icon", String.valueOf(R.drawable.lately_player));
        map7.put("title", "最近播放");
        map7.put("icon2", String.valueOf(R.drawable.playlist_sign));
        data.add(map7);
        return data;
    }

    /**
     *根据文件名获取不带后缀名的文件名
     */
    public static String gainSuffix(String string){
        int j = string.lastIndexOf(".");
        if(j != -1){
            return string.substring(0,j);
        }
        return string;
    }
}
