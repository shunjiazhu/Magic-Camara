package com.example.william.mycamerat;

import com.example.william.mycamerat.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class PokerShowActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Intent intent = getIntent();
        String message = intent.getStringExtra("pokerName");
        setContentView(R.layout.activity_poker_show);
        final ImageView imageView = (ImageView)findViewById(R.id.imageView);
        imageView.setImageResource(getResources().
                getIdentifier(message, "drawable", getPackageName()));
        }
}
