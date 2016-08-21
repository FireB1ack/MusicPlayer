package com.fireblack.musicplayer.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fireblack.musicplayer.entity.ThreadInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ChengHao on 2016/5/27.
 */
public class ThreadInfoDao {
    private DBHelper dbHpler;

    public ThreadInfoDao(Context context){
        dbHpler=new DBHelper(context);
    }

    /**
     * 查询某个下载任务的全部子线程
     * */
    public List<ThreadInfo> searchByDownLoadInfoId(int downLoadInfoId){
        List<ThreadInfo> list=new ArrayList<ThreadInfo>();
        ThreadInfo info=null;
        SQLiteDatabase db=dbHpler.getReadableDatabase();
        Cursor cr=db.rawQuery("SELECT * FROM threadInfo WHERE downLoadInfoId="+downLoadInfoId, null);
        while(cr.moveToNext()){
            info=new ThreadInfo();
            info.setId(cr.getInt(cr.getColumnIndex("_id")));
            info.setStartPosition(cr.getInt(cr.getColumnIndex("startPosition")));
            info.setCompleteSize(cr.getInt(cr.getColumnIndex("completeSize")));
            info.setDownLoadInfoId(cr.getInt(cr.getColumnIndex("downLoadInfoId")));
            info.setEndPosition(cr.getInt(cr.getColumnIndex("endPosition")));
            list.add(info);
        }
        cr.close();
        db.close();
        return list;
    }

    /**
     * 添加
     * */
    public int add(ThreadInfo threadInfo){
        SQLiteDatabase db=dbHpler.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put("startPosition", threadInfo.getStartPosition());
        values.put("endPosition", threadInfo.getEndPosition());
        values.put("downLoadInfoId", threadInfo.getDownLoadInfoId());
        values.put("completeSize", threadInfo.getCompleteSize());
        int rs=(int)db.insert("threadInfo", "completeSize", values);
        db.close();
        return rs;
    }

    /**
     * 更新
     * */
    public void update(List<ThreadInfo> threadInfos){
        SQLiteDatabase db=dbHpler.getWritableDatabase();
        db.beginTransaction();
        try {
            for (int i = 0,len=threadInfos.size(); i < len; i++) {
                db.execSQL("UPDATE threadInfo SET completeSize=? WHERE _id=?",new Object[]{threadInfos.get(i).getCompleteSize(),threadInfos.get(i).getId()});
            }
            db.setTransactionSuccessful();//设置事务处理成功，不设置会自动回滚不提交
        } finally {
            db.endTransaction();
        }
        db.close();
    }

    /**
     * 删除
     * */
    public int delete(int id){
        SQLiteDatabase db=dbHpler.getWritableDatabase();
        int rs=db.delete("threadInfo", "_id=?", new String[]{String.valueOf(id)});
        db.close();
        return rs;
    }

    /**
     * 根据下载任务Id删除
     * */
    public int deleteByDownLoadInfoId(int id){
        SQLiteDatabase db=dbHpler.getWritableDatabase();
        int rs=db.delete("threadInfo", "downLoadInfoId=?", new String[]{String.valueOf(id)});
        db.close();
        return rs;
    }
}
