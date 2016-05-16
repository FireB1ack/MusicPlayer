package com.fireblack.musicplayer.utils;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.fireblack.musicplayer.entity.ScanData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ChengHao on 2016/5/16.
 */
public class MusicManager {
    private Context context;
    public MusicManager(Context context) {
        this.context = context;
    }

    /**
     * 查询媒体库所有目录
     */
    public List<ScanData> searchByDirectory(){
        List<ScanData> list = new ArrayList<ScanData>();
        StringBuffer buffer = new StringBuffer();
        String[] prjs = {MediaStore.Audio.Media.DISPLAY_NAME,MediaStore.Audio.Media.DATA};
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                                                prjs,null,null,MediaStore.Audio.Media.DISPLAY_NAME);
        String data = null;
        String displayName = null;
        while (cursor.moveToNext()){
            displayName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
            data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            data = data.replace(displayName,"").toLowerCase();//全部转换为小写并去除文件名
            if(!buffer.toString().contains(data)){
                list.add(new ScanData(data,true));
                buffer.append(data);
            }
        }
        cursor.close();
        return list;
    }
}
