package com.fireblack.musicplayer.activity;

import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.fireblack.musicplayer.R;
import com.fireblack.musicplayer.adapter.ScanListAdapter;
import com.fireblack.musicplayer.entity.ScanData;
import com.fireblack.musicplayer.utils.MusicManager;

import java.util.List;

public class ScanMusicActivity extends BaseActivity {

    private MusicManager musicManager;
    private List<ScanData> datas;
    private ListView lv_scan_music_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_music);

        ImageButton ibtn_scan_setting_back = (ImageButton)(this.findViewById(R.id.bar_setting_top)).findViewById(R.id.ibtn_scan_setting_back);
        ibtn_scan_setting_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.ibtn_scan_setting_back) {
                    finish();
                }
            }
        });
        TextView tv_scan_top_title = (TextView) (this.findViewById(R.id.bar_setting_top)).findViewById(R.id.tv_scan_top_title);
        tv_scan_top_title.setText(R.string.scan_top_title);

        lv_scan_music_list = (ListView) findViewById(R.id.lv_scan_music_list);

        musicManager = new MusicManager(this);
        datas = musicManager.searchByDirectory();
        lv_scan_music_list.setAdapter(new ScanListAdapter(this,datas));
        lv_scan_music_list.setOnItemClickListener(mItemClickListener);

        //扫描歌曲，添加目录按钮
        Button btn_scan_ok = (Button) findViewById(R.id.btn_scan_ok);
        Button btn_scan_add = (Button) findViewById(R.id.btn_scan_add);
        btn_scan_ok.setOnClickListener(mListener);
        btn_scan_add.setOnClickListener(mListener);
    }

    private View.OnClickListener mListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_scan_ok:
                    //开始扫描歌曲

                    break;
                case R.id.btn_scan_add:
                    //开始添加目录
                    break;
                default:
                    break;
            }
        }
    };

    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            CheckBox cb_scan_item = (CheckBox) view.findViewById(R.id.cb_scan_item);
            cb_scan_item.setChecked(!cb_scan_item.isChecked());
        }
    };
}
