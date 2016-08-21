package com.fireblack.musicplayer.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.fireblack.musicplayer.R;

public class AboutActivity extends BaseActivity {
    public int resultCode=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setBackButton();
        setTopTitle(getResources().getString(R.string.about_title));
    }
    public void setTopTitle(String title) {
        ((TextView) (this.findViewById(R.id.bar_setting_top))
                .findViewById(R.id.tv_scan_top_title)).setText(title);
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
}
