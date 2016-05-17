package com.fireblack.musicplayer;

import android.app.Application;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.fireblack.musicplayer.dao.DBHelper;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }
    public void test(){
        DBHelper dbHelper = new DBHelper(getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
//        db.execSQL("insert into song (artistId,name,displayName,isLike,filePath)values(?,?,?,?,?)", new Object[]{1,"白月光","MP3",0,"/mnt/sdcard"});
//        db.execSQL("insert into song (artistId,name,displayName,isLike,filePath)values(?,?,?,?,?)", new Object[]{2,"白月光23","MP3",1,"/mnt/sdcard"});
//        db.execSQL("insert into artist (name)values(?)", new Object[]{"张信哲"});
//        db.execSQL("insert into artist (name)values(?)", new Object[]{"浩哥"});
//        Cursor cursor = db.rawQuery("select A.isLike, A._id, A.displayName, B.name, " +
//                "A.filePath from song as A inner join artist as B where A.artistId = B._id order by displayName desc", null);
        Cursor cursor = db.rawQuery("select A._id, A.displayName, B.name as Bname, " +
                "C.name as Cname, A.name as Aname, " +
                "A.albumId, A.artistId, C.picPath as CpicPath, " +
                "B.picPath as BpicPath, A.filePath, " +
                "A.durationTime from song as A inner join artist as B on " +
                "A.artistId=B._id inner join album as C on A.albumId=C._id order by displayName desc", null);
        while(cursor.moveToNext()){
            String[] s = new String[5];
            s[0] = String.valueOf(cursor.getInt(cursor.getColumnIndex("_id")));
            s[1] = cursor.getString(cursor.getColumnIndex("displayName"));
            s[2] = cursor.getString(cursor.getColumnIndex("Aname"));
            s[3] = cursor.getString(cursor.getColumnIndex("filePath"));
            s[4] = String.valueOf(cursor.getInt(cursor.getColumnIndex("durationTime")));

            for(int i=0;i<5;i++){
                Log.d("12345678", s[i]);
            }
            }

        cursor.close();
        db.close();
    }
}