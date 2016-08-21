package com.fireblack.musicplayer.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.fireblack.musicplayer.R;
import com.fireblack.musicplayer.adapter.ImageAdapter;
import com.fireblack.musicplayer.custom.Setting;

public class SkinSettingActivity extends BaseActivity {
    private GridView gv_skin;
    private ImageAdapter adapter;
    private Setting mSetting;
    public int resultCode=-1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skin_setting);
        resultCode=2;
        setBackButton();
        setTopTitle(getResources().getString(R.string.skinsetting_title));

        mSetting=new Setting(this, true);

        adapter=new ImageAdapter(this,mSetting.getCurrentSkinId());
        gv_skin=(GridView)this.findViewById(R.id.gv_skin);
        gv_skin.setAdapter(adapter);
        gv_skin.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //更新GridView
                adapter.setCurrentId(position);
                //更新背景图片
                SkinSettingActivity.this.getWindow().setBackgroundDrawableResource(Setting.SKIN_RESOURCES[position]);
                //保存数据
                mSetting.setCurrentSkinResId(position);
            }
        });
    }

    public void setBackButton() {
        ((ImageButton) (this.findViewById(R.id.bar_setting_top))
                .findViewById(R.id.ibtn_scan_setting_back))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (v.getId() == R.id.ibtn_scan_setting_back) {
                            if (resultCode != -1) {
                                setResult(resultCode);
                            }
                            finish();
                        }
                    }
                });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode== KeyEvent.KEYCODE_BACK){
            if(resultCode!=-1){
                setResult(resultCode);
            }
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    public void setTopTitle(String title) {
        ((TextView) (this.findViewById(R.id.bar_setting_top))
                .findViewById(R.id.tv_scan_top_title)).setText(title);
    }
}
