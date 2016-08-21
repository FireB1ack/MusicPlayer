package com.fireblack.musicplayer.dao;

import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

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
     * 从播放列表中删除某一首歌曲
     */
    public int deleteByPlayerList(int id,int playerListId){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rs = 0;
        Cursor cursor = db.rawQuery("select playerList from song where _id=?", new String[]{String.valueOf(id)});
        String temp = null;
        if(cursor.moveToNext()){
            temp = cursor.getString(0);
        }
        cursor.close();
        if(temp != null){
            ContentValues values = new ContentValues();
            values.put("playerList",temp.replace("#"+playerListId+"#",""));
            rs = db.update("song",values,"_id=?",new String[]{String.valueOf(id)});
        }
        db.close();
        return rs;
    }

    /**
     * 删除
     */
    public int delete(Integer... ids){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        StringBuilder sb = new StringBuilder();
        String[] str = new String[ids.length];
        for (int i=0;i<ids.length;i++){
            sb.append("?,");
            str[i] = String.valueOf(ids[i]);
            Cursor cursor = db.query("song", new String[]{"artistId", "albumId"}, "_id=?", new String[]{str[i]}, null, null, null);
            if(cursor.moveToNext()){
                final int artistId = cursor.getInt(0);
                final int albumId = cursor.getInt(1);
                //删除歌手
                Cursor artist_cr = db.rawQuery("select count(*) from song where artistId=?", new String[]{String.valueOf(artistId)});
                if(artist_cr.getCount() == 1){
                    db.delete("artist","_id=?",new String[]{String.valueOf(artistId)});
                }
                //删除专辑
                Cursor album_cr = db.rawQuery("select count(*) from song where albumId=?", new String[]{String.valueOf(albumId)});
                if(album_cr.getCount() == 1){
                    db.delete("album","_id=?",new String[]{String.valueOf(albumId)});
                }
            }
            cursor.close();
        }
        sb.deleteCharAt(sb.length() - 1);
        int rs = db.delete("song","_id in("+sb.toString()+")",str);
        db.close();
        return rs;
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
            Log.e("12345", cursor.getInt(cursor.getColumnIndex("_id")) + "送ID");
            song.setDisplayName(cursor.getString(cursor.getColumnIndex("displayName")));
            Log.e("12345", cursor.getString(cursor.getColumnIndex("displayName"))+"songname");
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
            song.setId(id);
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

    /**
     *更新播放时长
     */
    public void updateByDuration(int id,int duration){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("update song set durationTime=" + duration + " where _id=" + id);
        db.close();
    }

    /**
     * 更新播放大小
     */
    public void updateBySize(int id,int size){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("update song set size=" + size + " where _id=" + id);
        db.close();
    }

    /**
     * 更新是否是最爱
     */
    public void updateByLike(int id,int like){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("update song set isLike=" + like + " where _id=" + id);
        db.close();
    }

    public void updateByPlayerList(int id,int list){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select playerList from song where _id=?", new String[]{String.valueOf(id)});
        String playerList = null;
        if(cursor.moveToNext()){
            playerList = cursor.getString(0);
        }
        cursor.close();
        if(!playerList.contains("#" + playerList + "#")){
            db.execSQL("update song set playerList=? where _id=?", new Object[]{playerList + "#" +list + "#", id});
        }
        db.close();
    }

    /**
     * 判断下载任务是否存在
     * */
    public boolean isExist(String url) {
        int rs = 0;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cr = db.rawQuery("SELECT COUNT(*) FROM song WHERE netUrl=?", new String[] { url });
        while (cr.moveToNext()) {
            rs = cr.getInt(0);
        }
        cr.close();
        db.close();
        return rs > 0;
    }

    /**
     * 查询完成下载的歌曲
     * */
    public List<Song> searchByDownLoad() {
        List<Song> list = new ArrayList<Song>();
        Song song = null;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cr = db.rawQuery("SELECT A._id, A.filePath, " +
                "A.name AS Aname,B.name AS Bname FROM song AS A " +
                "INNER JOIN artist AS B " +
                "ON A.artistId=B._id " +
                "WHERE A.isDownFinish=1", null);
        while (cr.moveToNext()) {
            song = new Song();
            song.setId(cr.getInt(cr.getColumnIndex("_id")));
            song.setName(cr.getString(cr.getColumnIndex("Aname")));
            song.setArtist(new Artist(0, cr.getString(cr
                    .getColumnIndex("Bname")), null));
            song.setFilePath(cr.getString(cr
                    .getColumnIndex("filePath")));
            list.add(song);
        }
        cr.close();
        db.close();
        return list;
    }

}
