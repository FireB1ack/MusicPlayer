package com.fireblack.musicplayer.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fireblack.musicplayer.entity.PlayerList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ChengHao on 2016/5/18.
 */
public class PlayerListDao {
    private DBHelper dbHelper;
    public PlayerListDao(Context context) {
        dbHelper = new DBHelper(context);
    }

    /**
     * 全部查询
     */
    public List<String[]> searchAll(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<String[]> list = new ArrayList<String[]>();
        Cursor cursor = db.rawQuery("select * from playerList order by date desc ", null);
        while (cursor.moveToNext()){
            String[] s = new String[3];
            s[0] = String.valueOf(cursor.getInt(cursor.getColumnIndex("_id")));
            s[1] = cursor.getString(cursor.getColumnIndex("name"));
            s[2] = "";
            list.add(s);
        }
        cursor.close();
        db.close();
        return list;
    }

    /**
     * 判断列表是否已经存在
     */
    public boolean isExists(String name){
        int count = 0;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select count(*) from playerList where name = ?", new String[]{name});
        if(cursor.moveToNext()){
            count = cursor.getInt(0);
        }
        db.close();
        cursor.close();
        return count>0;
    }

    /**
     * 获取记录总数
     */
    public int getCount(){
        int count = 0;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select count(*) from playerList", null);
        if(cursor.moveToNext()){
            count = cursor.getInt(0);
        }
        db.close();
        cursor.close();
        return count;
    }

    /**
     *添加列表
     */
    public long add(PlayerList playerList){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name",playerList.getName());
        values.put("date", playerList.getDate());
        long rowid = db.insert("playerList","name",values);
        db.close();
        return rowid;
    }

    /**
     *删除列表
     */
    public int delete(int id){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        //先更新Song表中的相关数据
        db.execSQL("update song set playerList=replace(playerList,?,'')", new String[]{"#" + id + "#"});
        int rowid = db.delete("playerList", "_id=?", new String[]{String.valueOf(id)});
        db.close();
        return rowid;
    }

    public int update(PlayerList playerList){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name",playerList.getName());
        values.put("date", playerList.getDate());
        int rowid = db.update("playerList",values,"_id=?",new String[]{String.valueOf(playerList.getId())});
        db.close();
        return rowid;
    }
}
