package com.fireblack.musicplayer.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.fireblack.musicplayer.R;
import com.fireblack.musicplayer.adapter.ArtistItemAdapter;
import com.fireblack.musicplayer.adapter.SongItemAdapter;
import com.fireblack.musicplayer.adapter.SongWebAdapter;
import com.fireblack.musicplayer.custom.FlingGallery;
import com.fireblack.musicplayer.custom.MyDialog;
import com.fireblack.musicplayer.dao.AlbumDao;
import com.fireblack.musicplayer.dao.ArtistDao;
import com.fireblack.musicplayer.dao.PlayerListDao;
import com.fireblack.musicplayer.dao.SongDao;
import com.fireblack.musicplayer.entity.PlayerList;
import com.fireblack.musicplayer.entity.Song;
import com.fireblack.musicplayer.service.MediaPlayerManager;
import com.fireblack.musicplayer.utils.Common;
import com.fireblack.musicplayer.utils.XmlUtil;

import java.util.Arrays;
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

    //底部工具栏
    private ImageButton ibtn_player_albumart;//专辑封面
    private ImageButton ibtn_player_control;//播放/暂停
    private TextView tv_player_title;//播放歌曲 歌手-标题
    private ProgressBar pb_player_progress;//播放进度条
    private TextView tv_player_currentPosition;//当前播放的进度
    private TextView tv_player_duration;//歌曲播放时长

    private int pageNumber = 0;

    private SongDao songDao;
    private ArtistDao artistDao;
    private AlbumDao albumDao;
    private PlayerListDao playerListDao;

    private ViewGroup.LayoutParams params;
    private LayoutInflater inflater;
    private SharedPreferences preferences;
    private MediaPlayerManager mediaPlayerManager;
    private ListView lv_list_web;


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
        albumDao = new AlbumDao(this);
        playerListDao = new PlayerListDao(this);
        mediaPlayerManager = new MediaPlayerManager(this);
        params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

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
        ibtn_list_content_add_icon.setOnClickListener(imageButoon_listenner);
        lv_list_change_content.setOnItemClickListener(list_change_content_listener);

        //底部工具栏
        ibtn_player_albumart=(ImageButton)this.findViewById(R.id.ibtn_player_albumart);
        ibtn_player_control=(ImageButton)this.findViewById(R.id.ibtn_player_control);
        tv_player_title=(TextView)this.findViewById(R.id.tv_player_title);
        pb_player_progress=(ProgressBar)this.findViewById(R.id.pb_player_progress);
        tv_player_currentPosition=(TextView)this.findViewById(R.id.tv_player_currentPosition);
        tv_player_duration=(TextView)this.findViewById(R.id.tv_player_duration);

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

        //初始化网络音乐
        lv_list_web = (ListView) list_main_web.findViewById(R.id.lv_list_web);
        lv_list_web.setAdapter(new SongWebAdapter(HomeActivity.this, XmlUtil.parseWebSong(this)).setItemListener(itemListener));
        //初始化下载管理
        initDownLoad();

        mediaPlayerManager.setConnectionListener(serviceConnectionListener);
    }

    private MediaPlayerManager.ServiceConnectionListener serviceConnectionListener = new MediaPlayerManager.ServiceConnectionListener() {
        @Override
        public void onServiceConnected() {
            //重新拿数据
            mediaPlayerManager.initPlayerSongInfo();
//            updateSongItemList();
        }

        @Override
        public void onServiceDisconnected() {

        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        mediaPlayerManager.startAndBindService();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mediaPlayerManager.unbindService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 初始化下载
     */
    private void initDownLoad() {
        List<HashMap<String,Object>> data = Common.getListDownLoadData();
        SimpleAdapter download_adapter = new SimpleAdapter(this,data,R.layout.list_item,new String[]{"icon","title","icon2"},
                new int[]{R.id.iv_list_item_icon,R.id.tv_list_item_title,R.id.iv_list_item_icon2});

        ListView lv_list_download = (ListView) list_main_download.findViewById(R.id.lv_list_download);
        lv_list_download.setAdapter(download_adapter);
        lv_list_download.setOnItemClickListener(list_down_listener);
    }

    /**
     * 显示扫描歌曲提示，只有在第一次进入软件界面提示
     */
    private void checkScannerTip() {
        Boolean isScannerTip = preferences.getBoolean("isScannerTip", false);
        if(!isScannerTip){
            final AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
            builder.setTitle("扫描提示");
            builder.setMessage("是否要扫描本地歌曲入库");
            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    dialog.dismiss();
                    Intent intent = new Intent(HomeActivity.this,ScanMusicActivity.class);
                    startActivityForResult(intent,1);
                }
            });
            builder.setNegativeButton("取消", null);
            builder.create().show();
            preferences.edit().putBoolean("isScannerTip", false).commit();
        }
    }

    //重写返回键事件
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(pageNumber == 0){
                int state = mediaPlayerManager.getPlayerState();
                if(state == MediaPlayerManager.STATE_NULL || state == MediaPlayerManager.STATE_OVER || state == MediaPlayerManager.STATE_PAUSE){
//                    cancelAutoShutdown();
//                    mediaPlayerManager.stop();
//                    downLoadManager.stop();
                }
                finish();
                return true;
            }
            return backPage();
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 返回键事件
     */
    private boolean backPage() {
        if(pageNumber < 10){
            rl_list_content.setVisibility(View.GONE);
            rl_list_main_content.setVisibility(View.VISIBLE);
            pageNumber = 0;
            return true;
        }else {
            if(pageNumber == 22){
                JumpPages(1, 2, null);
            }else if(pageNumber == 33){
                JumpPages(1,3,null);
            }else if(pageNumber == 44){
                JumpPages(1,4,null);
            }else if(pageNumber == 55){
                JumpPages(1,5,null);
            }
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == 1){
            mediaPlayerManager.initScanner_SongInfo();
            updateListAdapterData();
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
            JumpPages(1,position+1,null);
        }
    };

    //下载管理列表点击事件
    private AdapterView.OnItemClickListener list_down_listener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            JumpPages(1,position+8,null);
        }
    };

    //网络音乐列表下载项点击事件
    private SongWebAdapter.ItemListener itemListener = new SongWebAdapter.ItemListener() {
        @Override
        public void onDoadLoad(Song song) {

        }
    };

    private AdapterView.OnItemClickListener list_change_content_listener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(pageNumber == 2){//歌手种类列表
                JumpPages(2,22,view.getTag());
            }else if(pageNumber == 3){//专辑种类列表
                JumpPages(2,33,view.getTag());
            }else if(pageNumber == 4){//文件夹种类列表
                JumpPages(2,44,view.getTag());
            }else if(pageNumber == 5){//播放列表列表
                JumpPages(2,55,view.getTag());
            }else if(pageNumber == 8){//正在下载列表

            }else if(pageNumber == 9){//下载完成列表

            }else if(pageNumber == 1){//全部歌曲列表
                playerMusicByItem(view, MediaPlayerManager.PLAYERFLAG_ALL, null);
            }else if(pageNumber == 6){//我最爱听列表
                playerMusicByItem(view,MediaPlayerManager.PLAYERFLAG_LIKE,null);
            }else if(pageNumber == 7){//最近播放列表
                playerMusicByItem(view,MediaPlayerManager.PLAYERFLAG_LATELY,null);
            }else if(pageNumber == 22){//歌手歌曲列表
                playerMusicByItem(view,MediaPlayerManager.PLAYERFLAG_ARTIST,condition);
            }else if(pageNumber == 33){//专辑
                playerMusicByItem(view,MediaPlayerManager.PLAYERFLAG_ARTIST,condition);
            }else if(pageNumber == 44){
                playerMusicByItem(view,MediaPlayerManager.PLAYERFLAG_FOLDER,condition);
            }else if(pageNumber == 55){
                playerMusicByItem(view,MediaPlayerManager.PLAYERFLAG_PLAYERLIST,condition);
            }
        }
    };

    private void playerMusicByItem(View view,int flag,String condition) {
//        if(mediaPlayerManager.getPlayerFlag() == MediaPlayerManager.PLAYERFLAG_WEB){
//            //网络列表#####################################################
//
//        }
        int songId = Integer.valueOf(((SongItemAdapter.ViewHolder) view.getTag()).tv_song_list_item_bottom.getTag().toString());
        if(songId == mediaPlayerManager.getSongId()){
            PlayerOrPause(view);
        }else {
            ibtn_player_control.setBackgroundResource(R.drawable.player_btn_mini_pause);
            mediaPlayerManager.player(songId, flag, condition);
            int[] playerInfo=new int[]{songId,mediaPlayerManager.getPlayerState()};
            ((SongItemAdapter)lv_list_change_content.getAdapter()).setPlayerInfo(playerInfo);
        }
    }

    /**
     * @param view
     * 播放或暂停歌曲
     */
    private void PlayerOrPause(View view) {
        if(mediaPlayerManager.getPlayerState() == MediaPlayerManager.STATE_NULL){
            Toast.makeText(this,"请先添加歌曲",Toast.LENGTH_LONG).show();
            return;
        }
        if(view == null){
            //当前列表播放结束
            if(mediaPlayerManager.getPlayerState() == MediaPlayerManager.STATE_OVER){
                Toast.makeText(this,"当前播放列表已经播放结束",Toast.LENGTH_SHORT).show();
                return;
            }
        }
        mediaPlayerManager.pauseOrPlayer();
        final int state = mediaPlayerManager.getPlayerState();
        int itemId = 0;
        if(state == MediaPlayerManager.STATE_PLAYER || state == MediaPlayerManager.STATE_PREPARE){
            ibtn_player_control.setBackgroundResource(R.drawable.music_list_item_pause);
            itemId = R.drawable.music_list_item_player;
        }else if(state == MediaPlayerManager.STATE_PAUSE){
            ibtn_player_control.setBackgroundResource(R.drawable.player_btn_mini_player);
            itemId = R.drawable.music_list_item_pause;
        }
        if(mediaPlayerManager.getPlayerFlag()==MediaPlayerManager.PLAYERFLAG_WEB){
//            if(v==null){
//                ((SongItemWebAdapter)lv_list_web.getAdapter()).setPlayerState(mediaPlayerManager.getPlayerState());
//            }else{
//                ((SongItemWebAdapter.ViewHolder)v.getTag()).tv_web_list_item_number.setBackgroundResource(itemRsId);
//            }
        }else {
            if(pageNumber == 1 || pageNumber == 6 || pageNumber == 7 || pageNumber == 22 || pageNumber == 33 || pageNumber == 44 || pageNumber == 55){
                if(view ==null){
                    ((SongItemAdapter)lv_list_change_content.getAdapter()).setPlayerState(mediaPlayerManager.getPlayerState());
                }else {
                    ((SongItemAdapter.ViewHolder)view.getTag()).tv_song_list_item_number.setBackgroundResource(itemId);
                }
            }
//            if(pageNumber==9){
//                if(v==null){
//                    ((DownLoadListAdapter)lv_list_change_content.getAdapter()).setPlayerState(mediaPlayerManager.getPlayerState());
//                }else{
//                    ((DownLoadListAdapter.ViewHolder)v.getTag()).tv_download_list_item_number.setBackgroundResource(itemRsId);
//                }
//            }
        }
    }

    private SongItemAdapter.ItemListener songItemListener = new SongItemAdapter.ItemListener() {
        @Override
        public void onLikeClick(int id, View view, int position) {
            //排除我最爱听歌曲列表
            if(pageNumber == 6){
                songDao.updateByLike(id, 0);
                //更新
                ((SongItemAdapter)lv_list_change_content.getAdapter()).deleteItem(position);
                btn_list_random_music2.setText("(共" + lv_list_change_content.getCount() + "首)随机播放");
                btn_list_random_music2.setTag(lv_list_change_content.getCount());
                deleteForResetPlayerList(id,MediaPlayerManager.PLAYERFLAG_LIKE,"");
                return;
            }
            if(view.getTag().equals("1")){
                view.setTag("0");
                view.setBackgroundResource(R.drawable.dislike);
                songDao.updateByLike(id,0);
            }else {
                view.setTag("1");
                view.setBackgroundResource(R.drawable.like);
                songDao.updateByLike(id,1);
            }
        }

        @Override
        public void onMenuClick(int id, String text, String path, int position) {
            showListSongLoogDialog(id,text,path,position);
        }
    };

    private AdapterView.OnItemLongClickListener longClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            if(pageNumber == 5){//播放列表长按事件
                showPlayListLongDialog(view);
                return true;
            }
            return false;
        }
    };

    /**
     * 长按播放列表 弹出对话框
     */
    private void showPlayListLongDialog(View view) {
        final TextView textView = ((ArtistItemAdapter.ViewHolder)view.getTag()).tv_list_item_title;
        final String text = textView.getText().toString();//列表名称
        final int plid = Integer.parseInt(textView.getTag().toString());//id

    }

    /**
     * 创建歌曲列表菜单对话框
     */
    private void showListSongLoogDialog(final int id2,String text,final String path,final int parrentposition) {
        String delete_title = "移除歌曲";
        if(pageNumber == 9){
            delete_title = "清楚任务";
        }
        String[] menuString = new String[]{"添加到列表","设为铃声",delete_title,"查看详情"};
        ListView menuList = new ListView(HomeActivity.this);
        menuList.setCacheColorHint(Color.TRANSPARENT);
        menuList.setDividerHeight(1);
        menuList.setAdapter(new ArrayAdapter<String>(HomeActivity.this, R.layout.dialog_menu_item, R.id.text1, menuString));
        menuList.setLayoutParams(new ViewGroup.LayoutParams(Common.getScreen(HomeActivity.this)[0]/2, ViewGroup.LayoutParams.WRAP_CONTENT));

        final MyDialog myDialog = new MyDialog.Builder(HomeActivity.this).setTitle(text).setView(menuList).create();
        myDialog.show();

        menuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                myDialog.cancel();
                myDialog.dismiss();
                if(position == 0){//添加到列表
                    addPlayerListDialog(id2);
                }
            }
        });
    }

    /**
     * 添加到列表对话框
     */
    private void addPlayerListDialog(final int id2) {
    }

    /**
     * 删除歌曲，重置播放列表
     */
    private void deleteForResetPlayerList(int id, int flag, String parameter) {
        final int state = mediaPlayerManager.getPlayerState();
        if(state == MediaPlayerManager.STATE_NULL || state == MediaPlayerManager.STATE_OVER){
            return;
        }
        if(mediaPlayerManager.getPlayerFlag() == MediaPlayerManager.PLAYERFLAG_WEB){
            return;
        }
        String s_parameter = mediaPlayerManager.getParameter();//获取当前查询条件
        if(s_parameter == null){
            s_parameter="";
            if(flag == MediaPlayerManager.PLAYERFLAG_ALL || (flag == mediaPlayerManager.getPlayerFlag() && parameter.equals(s_parameter))){
                //删除'播放列表'，就播放全部歌曲
                if(id == -1){
                    mediaPlayerManager.delete(-1);
                    return;
                }else {
                    //若果是当前播放歌曲，就要切换下一首
                    if (id == mediaPlayerManager.getSongId()){
                        mediaPlayerManager.delete(id);
                    }
                }
                mediaPlayerManager.resetPlayerList();
            }
        }
    }

    private String condition = null;//当前歌曲列表查询条件
    /**
     * 执行跳转页面，进入各个各个子级列表
     * 默认页：0
     * 1.全部歌曲 2.歌手 3.专辑 4.文件夹 5.播放列表 6.我最爱听 7.最近播放 8.正在下载 9.下载完成
     * 22.歌手二级 33.专辑二级 44.文件夹二级 55.播放列表二级
     */
    private void JumpPages(int index,int position,Object obj) {
        int[] playerInfo = new int[]{mediaPlayerManager.getSongId(),mediaPlayerManager.getPlayerState()};
        if(index == 1){
            rl_list_main_content.setVisibility(View.GONE);
            rl_list_content.setVisibility(View.VISIBLE);
            ibtn_list_content_icon.setBackgroundResource(R.drawable.player_btn_list);
            btn_list_random_music2.setVisibility(View.GONE);
            ibtn_list_content_add_icon.setVisibility(View.GONE);
            if(position == 1){//全部歌曲
                tv_list_content_title.setText("全部歌曲");
                List<String[]> data = songDao.searchByAll();
                lv_list_change_content.setAdapter(new SongItemAdapter(HomeActivity.this, data,playerInfo).setItemListener(songItemListener));
                btn_list_random_music2.setVisibility(View.VISIBLE);
                btn_list_random_music2.setText("(共" + data.size() + "首)随机播放");
                btn_list_random_music2.setTag(data.size());
            }else if(position == 2){//歌手
                tv_list_content_title.setText("歌手");
                List<String[]> data = artistDao.SearchByAll();
                lv_list_change_content.setAdapter(new ArtistItemAdapter(HomeActivity.this, data, R.drawable.default_list_singer));
            }else if(position == 3){//专辑
                tv_list_content_title.setText("专辑");
                List<String[]> data = albumDao.searchByAll();
                lv_list_change_content.setAdapter(new ArtistItemAdapter(HomeActivity.this,data,R.drawable.default_list_album));
            }else if(position == 4){//文件夹
                tv_list_content_title.setText("文件夹");
                List<String[]> data = songDao.searchByDirectory();
                lv_list_change_content.setAdapter((new ArtistItemAdapter(HomeActivity.this,data,R.drawable.local_file)));
            }else if(position == 5){//播放列表
                tv_list_content_title.setText("播放列表");
                ibtn_list_content_add_icon.setVisibility(View.VISIBLE);
                List<String[]> data = playerListDao.searchAll();
                lv_list_change_content.setAdapter(new ArtistItemAdapter(HomeActivity.this,data,R.drawable.local_custom));
            }else if(position == 6){//我最爱听
                tv_list_content_title.setText("我最爱听");
                List<String[]> data = songDao.searchByIsLike();
                lv_list_change_content.setAdapter(new SongItemAdapter(HomeActivity.this,data,playerInfo).setItemListener(songItemListener));
                btn_list_random_music2.setVisibility(View.VISIBLE);
                btn_list_random_music2.setText("(共" + data.size() + "首)随机播放");
                btn_list_random_music2.setTag(data.size());
            }else if(position == 7){//最近播放
                tv_list_content_title.setText("最近播放");
                List<String[]> data = songDao.searchByLately(mediaPlayerManager.getLatelyStr());
                lv_list_change_content.setAdapter(new SongItemAdapter(HomeActivity.this,data,playerInfo).setItemListener(songItemListener));
                btn_list_random_music2.setVisibility(View.VISIBLE);
                btn_list_random_music2.setText("(共" + data.size() + "首)随机播放");
                btn_list_random_music2.setTag(data.size());
            }else if(position == 8){//正在下载
                tv_list_content_title.setText("正在下载");

            }else if(position == 9){//下载完成

            }
            pageNumber = position;
        }else if(index == 2){//二级界面
            btn_list_random_music2.setVisibility(View.VISIBLE);
            TextView tv_list_item_title = ((ArtistItemAdapter.ViewHolder) obj).tv_list_item_title;
            condition = tv_list_item_title.getTag().toString().trim();
            tv_list_content_title.setText(tv_list_item_title.getText().toString());
            List<String[]> data = null;
            if(position == 22){//歌手
                data = songDao.searchByArtist(condition);
            }else if(position == 33){//专辑
                data = songDao.searchByAlbum(condition);
            }else if(position == 44){//文件夹
                data = songDao.searchByDirectory(condition);
            }else if(position == 55){//播放列表
                data = songDao.searchByDisplayerList("#" + condition + "#");
            }
            lv_list_change_content.setAdapter(new SongItemAdapter(HomeActivity.this,data,playerInfo).setItemListener(songItemListener));
            btn_list_random_music2.setText("(共" + data.size() + "首)随机播放");
            btn_list_random_music2.setTag(data.size());
            pageNumber = position;
        }
    }

    /**
     * 扫描后 更新本地列表的数据展示
     * 该方法是在二级界面通过菜单键扫描后返回时更新
     */
    private void updateListAdapterData(){
        int[] playerInfo = new int[]{mediaPlayerManager.getSongId(),mediaPlayerManager.getPlayerState()};
        if(pageNumber == 1){
            List<String[]> data = songDao.searchByAll();
            lv_list_change_content.setAdapter(new SongItemAdapter(HomeActivity.this,data,playerInfo));
            btn_list_random_music2.setText("(共" + data.size() + "首)随机播放");
            btn_list_random_music2.setTag(data.size());
        }else if(pageNumber == 2){
            List<String[]> data = artistDao.SearchByAll();
            lv_list_change_content.setAdapter(new ArtistItemAdapter(HomeActivity.this, data, R.drawable.default_list_singer));
        }else if(pageNumber == 3){
            List<String[]> data = albumDao.searchByAll();
            lv_list_change_content.setAdapter(new ArtistItemAdapter(HomeActivity.this,data,R.drawable.default_list_album));
        }else if(pageNumber == 4){
            List<String[]> data = songDao.searchByDirectory();
            lv_list_change_content.setAdapter((new ArtistItemAdapter(HomeActivity.this, data, R.drawable.local_file)));
        }else if(pageNumber == 22 || pageNumber == 33 || pageNumber == 44 || pageNumber == 55){
            List<String[]> data = null;
            if(pageNumber == 22){//歌手
                data = songDao.searchByArtist(condition);
            }else if(pageNumber == 33){//专辑
                data = songDao.searchByAlbum(condition);
            }else if(pageNumber == 44){//文件夹
                data = songDao.searchByDirectory(condition);
            }else if(pageNumber == 55){//播放列表
                data = songDao.searchByDisplayerList("#" + condition + "#");
            }
            lv_list_change_content.setAdapter(new SongItemAdapter(HomeActivity.this,data,playerInfo));
            btn_list_random_music2.setText("(共" + data.size() + "首)随机播放");
            btn_list_random_music2.setTag(data.size());
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
            }else if(v.getId() == R.id.ibtn_list_content_add_icon){
                if(pageNumber == 5){
                    //添加播放列表，弹出菜单
                    doPlayList(0,0,null);
                }
            }else if(v.getId() == R.id.ibtn_player_control){
                PlayerOrPause(null);
            }else if(v.getId() == R.id.ibtn_player_albumart){
                //进入playerMainActivity
//                startActivity(new Intent(HomeActivity.this,));
            }
        }
    };

    /**
     * 添加或更新播放列表
     */
    private void doPlayList(final int flag, final int id, String text) {
        String titleMsg = null;
        final EditText et_newPlayList = new EditText(HomeActivity.this);
        et_newPlayList.setLayoutParams(params);
        et_newPlayList.setTextSize(15);
        et_newPlayList.setBackgroundColor(Color.WHITE);
        et_newPlayList.setGravity(Gravity.CENTER);
        if(flag == 0){
            titleMsg = "创建";
            et_newPlayList.setHint("请输入播放列表的名称");
        }else if(flag == 1){
            titleMsg = " 更新";
            et_newPlayList.setText(text);
            et_newPlayList.selectAll();
        }
        final String titleMsg2 = titleMsg;
        new MyDialog.Builder(HomeActivity.this).setTitle(titleMsg+"播放列表")
                .setView(et_newPlayList,5,10,5,10).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String content =  et_newPlayList.getText().toString().trim();
                if(!TextUtils.isEmpty(content)){
                    if(playerListDao.isExists(content)){
                        Toast.makeText(HomeActivity.this,"此名称已经存在",Toast.LENGTH_SHORT).show();
                    }else {
                        PlayerList playerList = new PlayerList();
                        playerList.setName(content);

                        int rowId = -1;
                        if(flag == 0){//创建
                            rowId = (int) playerListDao.add(playerList);
                        }else if(flag == 1){//更新列表
                            playerList.setId(id);
                            rowId = playerListDao.update(playerList);
                        }
                        if(rowId>0){//判断是否成功
                            Toast.makeText(HomeActivity.this,titleMsg2 + "成功",Toast.LENGTH_SHORT).show();
                            lv_list_change_content.setAdapter(new ArtistItemAdapter(HomeActivity.this, playerListDao.searchAll(), R.drawable.local_custom));
                            dialog.cancel();
                            dialog.dismiss();
                        }else {
                            Toast.makeText(HomeActivity.this,titleMsg2 + "失败",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        }).setNegativeButton("取消",null).create().show();
    }

}


