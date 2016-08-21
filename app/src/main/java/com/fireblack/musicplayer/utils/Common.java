package com.fireblack.musicplayer.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.fireblack.musicplayer.R;

import java.io.File;
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

    public static List<HashMap<String,Object>> getListDownLoadData(){
        List<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("icon",String.valueOf(R.drawable.download_icon_down));
        map.put("title","正在下载");
        map.put("icon2",String.valueOf(R.drawable.playlist_sign));
        data.add(map);

        HashMap<String, Object> map2 = new HashMap<String, Object>();
        map2.put("icon",String.valueOf(R.drawable.download_icon_finish));
        map2.put("title","下载完成");
        map2.put("icon2",String.valueOf(R.drawable.playlist_sign));
        data.add(map2);
        return data;
    }

    /**
     * 修改文件名
     * */
    public static String renameFileName(String str){
        int i=str.lastIndexOf('.');
        if(i!=-1){
            File file=new File(str);
            file.renameTo(new File(str.substring(0,i)));
            return str.substring(0,i);
        }
        return str;
    }

    /**
     *根据文件名获取不带后缀名的文件名
     */
    public static String clearSuffix(String str){
        int j = str.lastIndexOf(".");
        if(j != -1){
            return str.substring(0, j);
        }
        return str;
    }

    /**
     * 根据文件路径获取不带后缀名的文件名字
     */
    public static String clearDirectory(String str){
        int i = str.lastIndexOf(File.separator);//File.separator: windows下为\ unix下为/
        if(i != -1){
            return clearSuffix(str.substring(i + 1, str.length()));
        }
        return str;
    }

    /**
     * 根据文件filePath获取文件所在目录
     */
    public static String clearFileName(String str){
        int i = str.lastIndexOf(File.separator);
        if(i != -1){
            return str.substring(0, i + 1);
        }
        return str;
    }

    /**
     * 获取后缀名
     */
    public static String getSuffix(String str){
        int i = str.indexOf(".");
        if(i != -1){
            return str.substring(i+1).toUpperCase();
        }
        return str;
    }

    /**
     * Byte--MB
     */
    public static String getByteToMB(int size){
        float mb =size/1024f/1024f;
        return String.format("%.2f",mb);
    }

    /**
     * 格式化文件大小 Byte->KB
     * */
    public static String formatByteToKB(int size){
        float kb=size/1024f;
        return String.format("%.2f",kb);
    }

    /**
     * Type--KB
     */
    public static String getByteToKB(int size){
        float kb = size/1024f;
        return String.format("%.2f",kb);
    }

    public static String formatSecondTime(int duration){
        if(duration == 0){
            return "00:00";
        }
        duration = duration/1000;
        int m = duration/60%60;
        int s = duration%60;
        return (m>9?m:"0"+m)+":"+(s>9?s:"0"+s);
    }

    /**
     * 计算百分比
     * */
    public static String getPercent(int n,float total){
        float rs=(n/total)*100;
        //判断是否是正整数
        if(String.valueOf(rs).indexOf(".0")!=-1){
            return String.valueOf((int)rs);
        }else{
            return  String.format("%.1f",rs);
        }
    }

    /**
     *  获取屏幕的大小
     *  0:宽度  1：高度
     */
    public static int[] getScreen(Context context){
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        int[] s = new int[]{(int)(outMetrics.density * outMetrics.widthPixels),
                (int)(outMetrics.density * outMetrics.heightPixels)};
        return s;
    }

    /**
     * 判断网络是否可用
     * */
    public static boolean getNetIsAvailable(Context context){
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connManager.getActiveNetworkInfo();
        if(networkInfo==null){
            return false;
        }
        return networkInfo.isAvailable();
    }


    /**
     * 检查SD卡是否已装载
     * */
    public static boolean isExistSdCard(){
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 判断目录是否存在，不在则创建
     * */
    public static void isExistDirectory(String directoryName) {
        File file = new File(directoryName);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    /**
     * 获得SD目录路径
     * */
    public static String getSdCardPath(){
        return Environment.getExternalStorageDirectory().getPath();
    }

    /**
     * 删除文件并且删除媒体库中数据
     */
    public static boolean deleteFile(Context context,String filePath){
        new File(filePath).delete();
        int id = -1;
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Audio.Media._ID},
                MediaStore.Audio.Media.DATA + "=?", new String[]{filePath}, null);
        if(cursor.moveToNext()){
            id = cursor.getInt(0);
        }
        cursor.close();
        if(id != -1){
            return resolver.delete(ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,id),null,null)>0;
        }
        return false;
    }

    public static boolean isExistFile(String filePath){
        return new File(filePath).exists();
    }
}
