package com.fireblack.musicplayer.dao;

import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.fireblack.musicplayer.entity.Album;
import com.fireblack.musicplayer.entity.Artist;
import com.fireblack.musicplayer.entity.Song;
import com.fireblack.musicplayer.utils.Common;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ChengHao on 2016/5/14.
 * 歌曲Dao
 */
public class SongDao {
    private static DBHelper dbHelper;


    public SongDao(Context context) {
        dbHelper = new DBHelper(context);
    }

    /**
     * 全部歌曲：0:id 1:文件名 2:歌手　3：文件路径 4:喜欢收藏
     */
    public static List<String[]> searchByAll(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        List<String[]> list = new ArrayList<String[]>();
        Cursor cursor = db.rawQuery("select A.isLike, A._id, A.displayName, B.name, " +
                "A.filePath from song as A inner join artist as B where A.artistId = B._id order by displayName desc", null);
        while(cursor.moveToNext()){
            String[] s = new String[5];
            s[0] = String.valueOf(cursor.getInt(cursor.getColumnIndex("_id")));
            s[1] = Common.clearSuffix(cursor.getString(cursor.getColumnIndex("displayName")));
            s[2] = cursor.getString(cursor.getColumnIndex("name"));
            s[3] = cursor.getString(cursor.getColumnIndex("filePath"));
            s[4] = String.valueOf(cursor.getInt(cursor.getColumnIndex("isLike")));
            list.add(s);
        }
        cursor.close();
        db.close();
        return list;
    }

    /**
     * 根据文件夹查询
     */
    public List<String[]> searchByDirectory(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<String[]> list = new ArrayList<String[]>();
        StringBuffer sb = new StringBuffer();
        Cursor cursor = db.rawQuery("select filePath, _id from song order by filePath desc", null);
        while(cursor.moveToNext()){
            String filePath = Common.clearFileName(cursor.getString(cursor.getColumnIndex("filePath"))).toLowerCase();
            if(!sb.toString().contains("#" + filePath + "#")){
                sb.append("#").append(filePath).append("#");
                String[] s = new String[3];
                s[0] = cursor.getString(cursor.getColumnIndex("filePath"));
                s[1] = filePath;
                s[2] = "";
                list.add(s);
            }
        }
        cursor.close();
        db.close();
        return list;
    }

    /**
     * 根据我最爱听查询
     * id 文件名 歌手 文件路径 是否喜爱
     */
    public List<String[]> searchByIsLike(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<String[]> list = new ArrayList<String[]>();
        Cursor cursor = db.rawQuery("select A._id, A.displayName, B.name, A.filePath, A.isLike " +
                "from song as A " +
                "inner join artist as B where " +
                "A.artistId=B._id and A.isLike=1 " +
                "order by displayName desc",null);
        while (cursor.moveToNext()){
            String[] s = new String[5];
            s[0] = String.valueOf(cursor.getInt(cursor.getColumnIndex("_id")));
            s[1] = Common.clearSuffix(cursor.getString(cursor.getColumnIndex("displayName")));
            s[2] = cursor.getString(cursor.getColumnIndex("name"));
            s[3] = cursor.getString(cursor.getColumnIndex("filePath"));
            s[4] = String.valueOf(cursor.getInt(cursor.getColumnIndex("isLike")));
            list.add(s);
        }
        cursor.close();
        db.close();
        return list;
    }

    /**
     * 根据歌手查询
     * id 文件名 歌手 文件路径 是否喜爱
     */
    public List<String[]> searchByArtist(String artistId){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<String[]> list = new ArrayList<String[]>();
        Cursor cursor = db.rawQuery("select A._id, A.displayName, B.name, A.filePath, A.isLike " +
                "from song as A " +
                "inner join artist as B where " +
                "A.artistId=B._id and A.artistId=? " +
                "order by displayName desc",new String[]{artistId});
        while (cursor.moveToNext()){
            String[] s = new String[5];
            s[0] = String.valueOf(cursor.getInt(cursor.getColumnIndex("_id")));
            s[1] = Common.clearSuffix(cursor.getString(cursor.getColumnIndex("displayName")));
            s[2] = cursor.getString(cursor.getColumnIndex("name"));
            s[3] = cursor.getString(cursor.getColumnIndex("filePath"));
            s[4] = String.valueOf(cursor.getInt(cursor.getColumnIndex("isLike")));
            list.add(s);
        }
        cursor.close();
        db.close();
        return list;
    }

    /**
     * 根据专辑查询
     * id 文件名 歌手 文件路径 是否喜爱
     */
    public List<String[]> searchByAlbum(String albumId){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<String[]> list = new ArrayList<String[]>();
        Cursor cursor = db.rawQuery("select A._id, A.displayName, B.name, A.filePath, A.isLike " +
                "from song as A " +
                "inner join artist as B where " +
                "A.artistId=B._id and A.albumId=? " +
                "order by displayName desc", new String[]{albumId});
        while (cursor.moveToNext()){
            String[] s = new String[5];
            s[0] = String.valueOf(cursor.getInt(cursor.getColumnIndex("_id")));
            s[1] = Common.clearSuffix(cursor.getString(cursor.getColumnIndex("displayName")));
            s[2] = cursor.getString(cursor.getColumnIndex("name"));
            s[3] = cursor.getString(cursor.getColumnIndex("filePath"));
            s[4] = String.valueOf(cursor.getInt(cursor.getColumnIndex("isLike")));
            list.add(s);
        }
        cursor.close();
        db.close();
        return list;
    }

    /**
     * 根据文件夹路径查询
     * id 文件名 歌手 文件路径 是否喜爱
     */
    public List<String[]> searchByDirectory(String filePath){
        filePath = filePath.toLowerCase();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<String[]> list = new ArrayList<String[]>();
        Cursor cursor = db.rawQuery("select A._id, A.displayName, B.name, A.filePath, A.isLike " +
                "from song as A " +
                "inner join artist as B where " +
                "A.artistId=B._id and A.filePath=? " +
                "order by displayName desc", new String[]{filePath});
        while (cursor.moveToNext()){
            String[] s = new String[5];
            s[0] = String.valueOf(cursor.getInt(cursor.getColumnIndex("_id")));
            s[1] = Common.clearSuffix(cursor.getString(cursor.getColumnIndex("displayName")));
            s[2] = cursor.getString(cursor.getColumnIndex("name"));
            s[3] = cursor.getString(cursor.getColumnIndex("filePath"));
            s[4] = String.valueOf(cursor.getInt(cursor.getColumnIndex("isLike")));
            list.add(s);
        }
        cursor.close();
        db.close();
        return list;
    }

    /**
     * 根据播放列表查询
     */
    public List<String[]> searchByDisplayerList(String playerListId){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<String[]> list = new ArrayList<String[]>();
        Cursor cursor = db.rawQuery("select A._id, A.displayName, B.name, A.filePath, A.isLike, A.playerList " +
                "from song as A " +
                "inner join artist as B where " +
                "A.artistId=B._id " +
                "order by displayName desc",null);
        while (cursor.moveToNext()){
            String playerList = cursor.getString(cursor.getColumnIndex("playerList"));
            if(playerList.contains(playerListId)){
                String[] s = new String[5];
                s[0] = String.valueOf(cursor.getInt(cursor.getColumnIndex("_id")));
                s[1] = Common.clearSuffix(cursor.getString(cursor.getColumnIndex("displayName")));
                s[2] = cursor.getString(cursor.getColumnIndex("name"));
                s[3] = cursor.getString(cursor.getColumnIndex("filePath"));
                s[4] = String.valueOf(cursor.getInt(cursor.getColumnIndex("isLike")));
                list.add(s);
            }
        }
        cursor.close();
        db.close();
        return list;
    }

    /**
     * 查询本库中的所有歌曲信息,以便在扫描歌曲时不添加已经在本库中存在的歌曲
     * 返回歌曲路径用"#[String]#"分隔
     */
    public String getFilePathAll(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        StringBuffer sb = new StringBuffer();
        Cursor cursor = db.rawQuery("select filePath from song order by _id desc", null);
        while (cursor.moveToNext()){
            sb.append("#").append(cursor.getString(cursor.getColumnIndex("filePath"))).append("#");
        }
        cursor.close();
        db.close();
        return sb.toString();
    }



    /**
     * 添加歌曲
     */
    public long add(Song song){
        SQLiteDatabase db= dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("displayName", song.getDisplayName());
        values.put("filePath", song.getFilePath());
        values.put("lyricPath", song.getLyricPath());
        values.put("mimeType", song.getMimeType());
        values.put("name", song.getName());
        values.put("albumId", song.getAlbum().getId());
        values.put("netUrl", song.getNetUrl());
        values.put("durationTime", song.getDurationTime());
        values.put("size", song.getSize());
        values.put("artistId", song.getArtist().getId());
        values.put("playerList", song.getPlayerList());
        values.put("isDownFinish", song.isDownFinish());
        values.put("isLike", song.isLike());
        values.put("isNet", song.isNet());
        long rowid = db.insert("song", "name", values);
        db.close();
        return rowid;
    }

    /**
     * @param latelyStr
     * 根据最近播放 字符串来查询
     */
    public List<String[]> searchByLately(String latelyStr){
        if(TextUtils.isEmpty(latelyStr)){
            return new ArrayList<String[]>();
        }
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<String[]> list = new ArrayList<String[]>();
        StringBuffer buffer = new StringBuffer();
        String[] ls = latelyStr.split(",");
        Cursor cursor = null;
        for (String ss : ls) {
            buffer.append("select A.isLike, A._id, A.displayName, B.name, A.filePath " +
                    "from song as A inner join artist as B on " +
                    "A.artistId=B._id " +
                    "where A._id=").append(ss).append(";");
            cursor = db.rawQuery(buffer.toString(),null);
            if(cursor.moveToNext()){
                String[] s= new String[5];
                s[0] = String.valueOf(cursor.getInt(cursor.getColumnIndex("_id")));
                s[1] = Common.clearSuffix(cursor.getString(cursor.getColumnIndex("displayName")));
                s[2] = cursor.getString(cursor.getColumnIndex("name"));
                s[3] = cursor.getString(cursor.getColumnIndex("filePath"));
                s[4] = String.valueOf(cursor.getInt(cursor.getColumnIndex("isLike")));
                list.add(s);
            }
            buffer.setLength(0);
        }
        cursor.close();
        db.close();
        return list;
    }

    /**
     * 获取记录总数
     */
    public int getCount(){
        int count = 0;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select count(*) from song", null);
        while (cursor.moveToNext()){
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }

    /**
     * 查询全部歌曲
     */
    public List<Song> searchAll(){
        return commonSearch("",null);
    }

    /**
     * 根据专辑查询
     */
    public List<Song> searchAlbum(String albumId){
        return commonSearch("where A.albumId=?",new String[]{albumId});
    }

    /**
     * 根据歌手查询
     */
    public List<Song> searchArtist(String artistId){
        return commonSearch("where A.artistId=?",new String[]{artistId});
    }

    /**
     * 查询下载完成的歌曲
     */
    public List<Song> searchDownload(){
        return commonSearch("where A.isDownFinish=1",null);
    }

    public List<Song> searchFolder(String filePath){
        filePath = filePath.toLowerCase();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Song> list = new ArrayList<Song>();
        Song song = null;
        Cursor cursor = db.rawQuery("select A._id, A.displayName, " +
                "B.name as Bname, " +
                "C.name as Cname, " +
                "A.name as Aname, " +
                "A.albumId, A.artistId, " +
                "C.picPath as Cpicpath, " +
                "B.picPath as BpicPath, " +
                "A.filePath, A.durationTime " +
                "from song as A " +
                "inner join artist as B on " +
                "A.artistId=B._id " +
                "inner join album as C on " +
                "A.albumId=C._id " +
                "order by displayName desc", null);
        while (cursor.moveToNext()){
            String filePaths = cursor.getString(cursor.getColumnIndex("filePath"));
            if(Common.clearFileName(filePaths).equals(filePath)){
                song = new Song();
                song.setId(cursor.getInt(cursor.getColumnIndex("_id")));
                song.setDisplayName(cursor.getString(cursor.getColumnIndex("displayName")));
                song.setArtist(new Artist(cursor.getInt(cursor.getColumnIndex("artistId")),
                        cursor.getString(cursor.getColumnIndex("Bname")),
                        cursor.getString(cursor.getColumnIndex("BpicPath"))));
                song.setAlbum(new Album(cursor.getInt(cursor.getColumnIndex("albumId")),
                        cursor.getString(cursor.getColumnIndex("Cname")),
                        cursor.getString(cursor.getColumnIndex("CpicPath"))));
                song.setName(cursor.getString(cursor.getColumnIndex("Aname")));
                song.setFilePath(filePaths);
                song.setDurationTime(cursor.getInt(cursor.getColumnIndex("durationTime")));
                list.add(song);
            }
        }
        cursor.close();
        db.close();
        return list;
    }

    /**
     * @param latelyStr 最近播放拼接成的字符串
     * 根据最近播放搜索
     */
    public List<Song> searLately(String latelyStr){
        if(TextUtils.isEmpty(latelyStr)){
            return new ArrayList<Song>();
        }
        StringBuffer buffer = new StringBuffer();
        String[] ls = latelyStr.split(",");
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Song> list = new ArrayList<Song>();
        Song song = null;
        Cursor cursor = null;
        for (String s : ls) {
            buffer.append("select A._id, A.displayName," +
                    "B.name as Bname," +
                    "C.name as Cname," +
                    "A.name as Aname," +
                    "A.albumId, A.artistId," +
                    "C.picPath as CpicPath," +
                    "B.picPath as BpicPath," +
                    "A.filePath, A.durationTime " +
                    "from song as A " +
                    "inner join artist as B on " +
                    "A.artistId=B._id " +
                    "inner join album as C on " +
                    "A.albumId=C._id " +
                    "where A._id=").append(s).append(";");
            cursor = db.rawQuery(buffer.toString(),null);
            while (cursor.moveToNext()){
                song = new Song();
                song.setId(cursor.getInt(cursor.getColumnIndex("_id")));
                song.setDisplayName(cursor.getString(cursor.getColumnIndex("displayName")));
                song.setArtist(new Artist(cursor.getInt(cursor.getColumnIndex("artistId")),
                        cursor.getString(cursor.getColumnIndex("Bname")),
                        cursor.getString(cursor.getColumnIndex("BpicPath"))));
                song.setAlbum(new Album(cursor.getInt(cursor.getColumnIndex("albumId")),
                        cursor.getString(cursor.getColumnIndex("Cname")),
                        cursor.getString(cursor.getColumnIndex("CpicPath"))));
                song.setName(cursor.getString(cursor.getColumnIndex("Aname")));
                song.setFilePath(cursor.getString(cursor.getColumnIndex("filePath")));
                song.setDurationTime(cursor.getInt(cursor.getColumnIndex("durationTime")));
                list.add(song);
            }
            buffer.setLength(0);
        }
        cursor.close();
        db.close();
        return list;
    }

    /**
     * @根据我最爱听歌曲查询
     */
    public List<Song> searchIsLike(){
        return commonSearch("where A.isLike=1",null);
    }

    /**
     * 根据播放列表查询歌曲
     */
    public List<Song> searchPlayerList(String playerListId){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Song> list = new ArrayList<Song>();
        Song song = null;
        Cursor cursor = db.rawQuery("select A._id, A.displayName, " +
                "B.name as Bname, " +
                "C.name as Cname, " +
                "A.name as Aname, " +
                "A.albumId, A.artistId, A.playerList " +
                "C.picPath as Cpicpath, " +
                "B.picPath as BpicPath, " +
                "A.filePath, A.durationTime " +
                "from song as A " +
                "inner join artist as B on " +
                "A.artistId=B._id " +
                "inner join album as C on " +
                "A.albumId=C._id " +
                "order by displayName desc", null);
        while (cursor.moveToNext()){
            String playerList = cursor.getString(cursor.getColumnIndex("playerList"));
            if(playerList.contains(playerListId)){
                song = new Song();
                song.setId(cursor.getInt(cursor.getColumnIndex("_id")));
                song.setDisplayName(cursor.getString(cursor.getColumnIndex("displayName")));
                song.setArtist(new Artist(cursor.getInt(cursor.getColumnIndex("artistId")),
                        cursor.getString(cursor.getColumnIndex("Bname")),
                        cursor.getString(cursor.getColumnIndex("BpicPath"))));
                song.setAlbum(new Album(cursor.getInt(cursor.getColumnIndex("albumId")),
                        cursor.getString(cursor.getColumnIndex("Cname")),
                        cursor.getString(cursor.getColumnIndex("CpicPath"))));
                song.setName(cursor.getString(cursor.getColumnIndex("Aname")));
                song.setFilePath(cursor.getString(cursor.getColumnIndex("filePath")));
                song.setDurationTime(cursor.getInt(cursor.getColumnIndex("durationTime")));
                list.add(song);
            }
        }
        cursor.close();
        db.close();
        return list;
    }

    /**
     * @param str
     * @param parameter 查询参数
     * 通用查询
     */
    public List<Song> commonSearch(String str, String[] parameter) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Song> list = new ArrayList<Song>();
        Song song = null;
        Cursor cursor = db.rawQuery("select A._id, A.displayName, " +
                "B.name as Bname, " +
                "C.name as Cname, " +
                "A.name as Aname, " +
                "A.albumId, A.artistId, " +
                "C.picPath as CpicPath, " +
                "B.picPath as BpicPath, " +
                "A.filePath, A.durationTime " +
                "from song as A " +
                "inner join artist as B on " +
                "A.artistId=B._id " +
                "inner join album as C on " +
                "A.albumId=C._id" + " " + str +
                " order by displayName desc", parameter);
        while(cursor.moveToNext()){
            song = new Song();
            song.setId(cursor.getInt(cursor.getColumnIndex("_id")));
            song.setDisplayName(cursor.getString(cursor.getColumnIndex("displayName")));
            song.setArtist(new Artist(cursor.getInt(cursor.getColumnIndex("artistId")),
                    cursor.getString(cursor.getColumnIndex("Bname")),
                    cursor.getString(cursor.getColumnIndex("BpicPath"))));
            song.setAlbum(new Album(cursor.getInt(cursor.getColumnIndex("albumId")),
                    cursor.getString(cursor.getColumnIndex("Cname")),
                    cursor.getString(cursor.getColumnIndex("CpicPath"))));
            song.setName(cursor.getString(cursor.getColumnIndex("Aname")));
            song.setFilePath(cursor.getString(cursor.getColumnIndex("filePath")));
            song.setDurationTime(cursor.getInt(cursor.getColumnIndex("durationTime")));
            list.add(song);
        }
        cursor.close();
        db.close();
        return list;
    }

    public Song searchById(int id){
        Song song = null;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select A.displayName, " +
                "B.name as Bname, " +
                "C.name as Cname, " +
                "A.name as Aname, " +
                "A.artistId, A.albumId, " +
                "C.picPath as CpicPath, " +
                "B.picPath as BpicPath, " +
                "A.filePath, A.durationTime, A.size " +
                "from song as A " +
                "inner join artist as B on " +
                "A.artistId=B._id " +
                "inner join album as C on " +
                "A.albumId=C._id " +
                "where A._id=?", new String[]{String.valueOf(id)});
        if(cursor.moveToNext()){
            song = new Song();
            song.setId(cursor.getInt(cursor.getColumnIndex("_id")));
            song.setDisplayName(cursor.getString(cursor.getColumnIndex("displayName")));
            song.setArtist(new Artist(cursor.getInt(cursor.getColumnIndex("artistId")),
                    cursor.getString(cursor.getColumnIndex("Bname")),
                    cursor.getString(cursor.getColumnIndex("BpicPath"))));
            song.setAlbum(new Album(cursor.getInt(cursor.getColumnIndex("albumId")),
                    cursor.getString(cursor.getColumnIndex("Cname")),
                    cursor.getString(cursor.getColumnIndex("CpicPath"))));
            song.setName(cursor.getString(cursor.getColumnIndex("Aname")));
            song.setFilePath(cursor.getString(cursor.getColumnIndex("filePath")));
            song.setDurationTime(cursor.getInt(cursor.getColumnIndex("durationTime")));
            song.setSize(cursor.getInt(cursor.getColumnIndex("size")));
        }
        cursor.close();
        db.close();
        return song;
    }

}
