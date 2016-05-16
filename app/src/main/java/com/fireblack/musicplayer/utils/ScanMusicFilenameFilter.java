package com.fireblack.musicplayer.utils;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Created by ChengHao on 2016/5/16.
 * 文件名过滤器
 */
public class ScanMusicFilenameFilter implements FilenameFilter {
    private String suffix = ".MP3.WMA.AAC.M4A";
    @Override
    public boolean accept(File dir, String filename) {
        if(suffix.contains("."+Common.getSuffix(filename))){
            return true;
        }
        return false;
    }
}
