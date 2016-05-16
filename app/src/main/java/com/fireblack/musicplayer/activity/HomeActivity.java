package com.fireblack.musicplayer.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.fireblack.musicplayer.R;
import com.fireblack.musicplayer.adapter.ArtistItemAdapter;
import com.fireblack.musicplayer.adapter.SongItemAdapter;
import com.fireblack.musicplayer.custom.FlingGallery;
import com.fireblack.musicplayer.dao.ArtistDao;
import com.fireblack.musicplayer.dao.SongDao;
import com.fireblack.musicplayer.utils.Common;

import java.util.HashMap;
import java.util.List;

public class HomeActivity extends BaseActivity {
    //导航栏选项卡布局数组
    private ViewGroup[] vg_list_tab_item = new ViewGroup[3];
    private FlingGallery fgv_list_main;
    //当前屏幕的下标
    private int screenIndex = 0;
    //导航栏的内容
    private String[] list_item_items;
    //导航栏的icon
    private int[] list_item_icons = new int[] { R.drawable.list_music_icon,
            R.drawable.list_web_icon, R.drawable.list_download_icon };
    //本地音乐
    private ViewGroup list_main_music;
    //网络音乐
    private ViewGroup list_main_web;
    //下载管理
    private ViewGroup list_main_download;
    //主屏幕内容布局
    private ViewGroup rl_list_main_content;
    //切换内容布局
    private ViewGroup rl_list_content;
    //本地音乐和下载管理的二三级布局
    private ImageButton ibtn_list_content_icon;//左边图标
    private ImageButton ibtn_list_content_add_icon;//右边图标
    private TextView tv_list_content_title;//标题
    private ListView lv_list_change_content;//ListView
    private Button btn_list_random_music2;//随机播放按钮

    private SongDao songDao;
    private ArtistDao artistDao;

    private SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //检查是否进入SplashActivity界面
        preferences = getSharedPreferences("config",MODE_PRIVATE);
        Boolean isStart = preferences.getBoolean("isStart", true);
        if(isStart) {
            startActivity(new Intent(this, SplashActivity.class));
            this.finish();
        }else {
            //扫描歌曲
            checkScannerTip();
        }

        songDao = new SongDao(this);
        artistDao = new ArtistDao(this);

        //导航栏选项卡数组 实例化
        vg_list_tab_item[0] = (ViewGroup) this.findViewById(R.id.list_tab_item_music);
        vg_list_tab_item[1] = (ViewGroup) this.findViewById(R.id.list_tab_item_web);
        vg_list_tab_item[2] = (ViewGroup) this.findViewById(R.id.list_tab_item_download);

        //主屏幕内容布局选项 实例化
        list_main_music = (ViewGroup) this.findViewById(R.id.list_main_music);
        list_main_web = (ViewGroup) this.findViewById(R.id.list_main_web);
        list_main_download = (ViewGroup) this.findViewById(R.id.list_main_download);

        //主屏幕内容布局和切换内容布局 实例化
        rl_list_main_content = (ViewGroup)this.findViewById(R.id.rl_list_main_content);
        rl_list_content = (ViewGroup)this.findViewById(R.id.rl_list_content);

        //本地音乐的二三级布局
        ibtn_list_content_icon = (ImageButton) findViewById(R.id.ibtn_list_content_icon);
        ibtn_list_content_add_icon = (ImageButton) findViewById(R.id.ibtn_list_content_add_icon);
        tv_list_content_title = (TextView) findViewById(R.id.tv_list_content_title);
        lv_list_change_content = (ListView) findViewById(R.id.lv_list_change_content);
        btn_list_random_music2 = (Button) findViewById(R.id.btn_list_random_music2);
        ibtn_list_content_icon.setOnClickListener(imageButoon_listenner);

        //切换主屏幕内容容器
        fgv_list_main = (FlingGallery) rl_list_main_content
                .findViewById(R.id.fgv_list_main);
        fgv_list_main.setDefaultScreen(screenIndex);
        fgv_list_main.setOnScrollToScreenListener(scrollToScreenListener);

        //从资源文件中获取导航栏选项卡标题
        list_item_items = getResources().getStringArray(R.array.list_tab_items);
        //初始化导航栏
        initTabItem();
        //初始化本地音乐内容区域
        initListMusicItem();

    }

    /**
     * 显示扫描歌曲提示，只有在第一次进入软件界面提示
     */
    private void checkScannerTip() {
        Boolean isScannerTip = preferences.getBoolean("isScannerTip", true);
        if(!isScannerTip){
            final AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
            builder.setTitle("扫描提示");
            builder.setMessage("是否要扫描本地歌曲入库");
            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    dialog.dismiss();
                    startActivity(new Intent(HomeActivity.this,ScanMusicActivity.class));
                }
            });
            builder.setNegativeButton("取消", null);
            builder.create().show();
            preferences.edit().putBoolean("isScannerTip", false).commit();
        }
    }

    /**
     * 初始化本地音乐内容区域
     */
    private void initListMusicItem() {
        List<HashMap<String,Object>> data =  Common.getListMusicData();
        SimpleAdapter music_adapter = new SimpleAdapter(this,data,R.layout.list_item,new String[]{"icon","title","icon2"},
                new int[]{R.id.iv_list_item_icon,R.id.tv_list_item_title,R.id.iv_list_item_icon2});
        ListView lv_list_music = (ListView) list_main_music.findViewById(R.id.lv_list_music);
        lv_list_music.setAdapter(music_adapter);
        lv_list_music.setOnItemClickListener(list_music_listener);
    }

    //本地音乐列表点击事件
    private AdapterView.OnItemClickListener list_music_listener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            JumpPages(1,position+1);
        }
    };

    /**
     * 执行跳转页面，进入各个各个子级列表
     * 默认页：0
     * 1.全部歌曲 2.歌手 3.专辑 4.文件夹 5.播放列表 6.我最爱听 7.最近播放 8.正在下载 9.下载完成
     * 22.歌手二级 33.专辑二级 44.文件夹二级 55.播放列表二级
     */
    private void JumpPages(int index,int position) {
        if(index == 1){
            rl_list_main_content.setVisibility(View.GONE);
            rl_list_content.setVisibility(View.VISIBLE);
            ibtn_list_content_icon.setBackgroundResource(R.drawable.player_btn_list);
            btn_list_random_music2.setVisibility(View.GONE);
            ibtn_list_content_add_icon.setVisibility(View.GONE);
            if(position == 1){//全部歌曲
                tv_list_content_title.setText("全部歌曲");
                List<String[]> data = songDao.searchByAll();
                lv_list_change_content.setAdapter(new SongItemAdapter(HomeActivity.this, data));
                Log.v("tag", "12345");
                btn_list_random_music2.setVisibility(View.VISIBLE);
                btn_list_random_music2.setText("(共" + data.size() + "首)随机播放");
                btn_list_random_music2.setTag(data.size());
            }else if(position == 2){//歌手
                tv_list_content_title.setText("歌手");
                List<String[]> data = artistDao.SearchByAll();
                lv_list_change_content.setAdapter(new ArtistItemAdapter(HomeActivity.this,data,R.drawable.default_list_singer));
            }else if(position == 3){//专辑
                tv_list_content_title.setText("专辑");
            }

        }
    }


    /**
     * 初始化导航栏
     */
    private void initTabItem() {
        for(int i = 0; i < vg_list_tab_item.length; i++){
            vg_list_tab_item[i].setOnClickListener(tabClickListener);
            if(screenIndex == i){
                vg_list_tab_item[0].setBackgroundResource(R.drawable.list_top_press);
            }
        }
        ((ImageView) vg_list_tab_item[0].findViewById(R.id.iv_list_tab_item_icon)).setImageResource(list_item_icons[0]);
        ((ImageView) vg_list_tab_item[1].findViewById(R.id.iv_list_tab_item_icon)).setImageResource(list_item_icons[1]);
        ((ImageView) vg_list_tab_item[2].findViewById(R.id.iv_list_tab_item_icon)).setImageResource(list_item_icons[2]);
        ((TextView) vg_list_tab_item[0].findViewById(R.id.tv_list_item_text)).setText(list_item_items[0]);
        ((TextView) vg_list_tab_item[1].findViewById(R.id.tv_list_item_text)).setText(list_item_items[1]);
        ((TextView) vg_list_tab_item[2].findViewById(R.id.tv_list_item_text)).setText(list_item_items[2]);
    }
    //导航栏选项卡切换事件
    private View.OnClickListener tabClickListener = new View.OnClickListener() {

        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.list_tab_item_music:
                    if (screenIndex == 0) {
                        return;
                    }
                    vg_list_tab_item[screenIndex].setBackgroundResource(0);
                    screenIndex = 0;
                    break;
                case R.id.list_tab_item_web:
                    if (screenIndex == 1) {
                        return;
                    }
                    vg_list_tab_item[screenIndex].setBackgroundResource(0);
                    screenIndex = 1;
                    break;
                case R.id.list_tab_item_download:
                    if (screenIndex == 2) {
                        return;
                    }
                    vg_list_tab_item[screenIndex].setBackgroundResource(0);
                    screenIndex = 2;
                    break;
                default:
                    break;
            }
            vg_list_tab_item[screenIndex]
                    .setBackgroundResource(R.drawable.list_top_press);
            //切换屏幕
        fgv_list_main.setToScreen(screenIndex, true);
        }
    };

    //主屏幕左右滑动事件
    private FlingGallery.OnScrollToScreenListener scrollToScreenListener = new FlingGallery.OnScrollToScreenListener() {

        public void operation(int currentScreen, int screenCount) {
            vg_list_tab_item[screenIndex].setBackgroundResource(0);
            screenIndex = currentScreen;
            vg_list_tab_item[screenIndex]
                    .setBackgroundResource(R.drawable.list_top_press);
        }
    };

    //imageButton 点击监听器
    private View.OnClickListener imageButoon_listenner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.ibtn_list_content_icon){
                rl_list_content.setVisibility(View.GONE);
                rl_list_main_content.setVisibility(View.VISIBLE);
            }
        }
    };

}


