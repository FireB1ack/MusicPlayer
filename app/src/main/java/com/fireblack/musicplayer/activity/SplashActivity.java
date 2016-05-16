package com.fireblack.musicplayer.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.fireblack.musicplayer.R;

/**
 * Created by ChengHao on 2016/5/15.
 */
public class SplashActivity extends Activity {

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            preferences.edit().putBoolean("isStart",false).commit();
            startActivity(new Intent(SplashActivity.this, HomeActivity.class));
            finish();
        }
    };
    private RelativeLayout rl_root;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置无标题
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);

        rl_root = (RelativeLayout) findViewById(R.id.rl_root);
        preferences = getSharedPreferences("config",MODE_PRIVATE);

        //执行跳转,延时2秒
        handler.sendEmptyMessageDelayed(0, 2000);
        //Splash渐变效果
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.3f,1f);
        alphaAnimation.setDuration(2000);
        rl_root.startAnimation(alphaAnimation);

//        ImageView ig = new ImageView(this);
//        ViewGroup.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        ig.setLayoutParams(params);
//        ig.setImageResource(R.drawable.splash_activity);
//        setContentView(ig);
    }
}
