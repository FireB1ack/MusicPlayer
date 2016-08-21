package com.fireblack.musicplayer.utils;

import com.fireblack.musicplayer.entity.DownLoadInfo;

import java.util.Comparator;

/**
 * Created by ChengHao on 2016/6/12.
 */
public class ComparatorDownLoadInfo implements Comparator<DownLoadInfo> {
    @Override
    public int compare(DownLoadInfo lhs, DownLoadInfo rhs) {
        return lhs.getId()-rhs.getId();
    }
}
