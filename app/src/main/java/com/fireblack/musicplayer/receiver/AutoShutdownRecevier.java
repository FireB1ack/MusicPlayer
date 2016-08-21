package com.fireblack.musicplayer.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.fireblack.musicplayer.activity.BaseActivity;
import com.fireblack.musicplayer.custom.Setting;
import com.fireblack.musicplayer.service.MediaPlayerManager;

/**
 * Created by ChengHao on 2016/5/27.
 */
public class AutoShutdownRecevier extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //更新配置文件
        new Setting(context, true).setValue(Setting.KEY_AUTO_SLEEP,"");

        //关闭程序
        context.sendBroadcast(new Intent(BaseActivity.BROADCASTRECEVIER_ACTON));
        //停止音乐
        context.startService(new Intent(MediaPlayerManager.SERVICE_ACTION).putExtra("flag", MediaPlayerManager.SERVICE_MUSIC_STOP));
        //停止下载
//        context.startService(new Intent(DownLoadManager.SERVICE_ACTION).putExtra("flag", DownLoadManager.SERVICE_DOWNLOAD_STOP));
    }
}
