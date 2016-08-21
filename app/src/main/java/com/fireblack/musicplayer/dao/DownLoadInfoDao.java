package com.fireblack.musicplayer.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fireblack.musicplayer.entity.DownLoadInfo;
import com.fireblack.musicplayer.service.DownLoadManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ChengHao on 2016/5/24.
 */
public class DownLoadInfoDao {
    private DBHelper dbHpler;

    public DownLoadInfoDao(Context context){
        dbHpler=new DBHelper(context);
    }

    /**
     * 查询所有下载任务
     * */
    public List<DownLoadInfo> searchAll(){
        List<DownLoadInfo> list=new ArrayList<DownLoadInfo>();
        DownLoadInfo downLoadInfo=null;
        SQLiteDatabase db=dbHpler.getReadableDatabase();
        Cursor cr=db.rawQuery("select * from downLoadInfo ORDER BY _id", null);
        while(cr.moveToNext()){
            downLoadInfo=new DownLoadInfo();
            downLoadInfo.setId(cr.getInt(cr.getColumnIndex("_id")));
            downLoadInfo.setThreadInfos(null);
            downLoadInfo.setFileSize(cr.getInt(cr.getColumnIndex("fileSize")));
            downLoadInfo.setUrl(cr.getString(cr.getColumnIndex("url")));
            downLoadInfo.setAlbum(cr.getString(cr.getColumnIndex("album")));
            downLoadInfo.setArtist(cr.getString(cr.getColumnIndex("artist")));
            downLoadInfo.setDisplayName(cr.getString(cr.getColumnIndex("displayName")));
            downLoadInfo.setDurationTime(cr.getInt(cr.getColumnIndex("durationTime")));
            downLoadInfo.setCompleteSize(cr.getInt(cr.getColumnIndex("completeSize")));
            downLoadInfo.setFilePath(cr.getString(cr.getColumnIndex("filePath")));
            downLoadInfo.setMimeType(cr.getString(cr.getColumnIndex("mimeType")));
            downLoadInfo.setName(cr.getString(cr.getColumnIndex("name")));
            downLoadInfo.setState(DownLoadManager.STATE_PAUSE);
            downLoadInfo.setThreadCount(0);
            list.add(downLoadInfo);
        }
        cr.close();
        db.close();
        return list;
    }

    /**
     * 添加
     * */
    public int add(DownLoadInfo downLoadInfo){
        SQLiteDatabase db=dbHpler.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put("fileSize", downLoadInfo.getFileSize());
        values.put("url", downLoadInfo.getUrl());
        values.put("album", downLoadInfo.getAlbum());
        values.put("artist", downLoadInfo.getArtist());
        values.put("displayerName", downLoadInfo.getDisplayName());
        values.put("durationTime", downLoadInfo.getDurationTime());
        values.put("filePath", downLoadInfo.getFilePath());
        values.put("completeSize", 0);
        values.put("mimeType", downLoadInfo.getMimeType());
        values.put("name", downLoadInfo.getName());

        int rs=(int)db.insert("downLoadInfo", "url", values);
        db.close();
        return rs;
    }

    /**
     * 更新下载进度
     * */
    public  void update(int id,int completeSize){
        SQLiteDatabase db=dbHpler.getWritableDatabase();
        db.execSQL("update downLoadInfo set completeSize=? where _id=?",new Object[]{completeSize,id});
        db.close();
    }

    /**
     * 删除
     * */
    public int delete(int id){
        SQLiteDatabase db=dbHpler.getWritableDatabase();
        int rs=db.delete("downLoadInfo", "_id=?", new String[]{String.valueOf(id)});
        db.close();
        return rs;
    }

    /**
     * 判断下载任务是否存在
     * */
    public boolean isExist(String url){
        int rs=-1;
        SQLiteDatabase db=dbHpler.getReadableDatabase();
        Cursor cr=db.rawQuery("select count(*) from downLoadInfo where url=?", new String[]{url});
        while(cr.moveToNext()){
            rs=cr.getInt(0);
        }
        cr.close();
        db.close();
        return rs>0;
    }
}
