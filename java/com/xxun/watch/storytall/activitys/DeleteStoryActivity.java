package com.xxun.watch.storytall.activitys;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.view.GestureDetectorCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.xxun.watch.storytall.Const;
import com.xxun.watch.storytall.R;

public class DeleteStoryActivity extends Activity {

    private ImageView iv_confirm;
    private ImageView iv_cancle;
    private String storyPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_story);
        iv_confirm = (ImageView)findViewById(R.id.iv_confirm);
        iv_cancle = (ImageView)findViewById(R.id.iv_cancle);
        storyPosition = getIntent().getStringExtra("story_position");
        iv_confirm.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent _intent = new Intent();
                _intent.putExtra("story_position_back",storyPosition);
                setResult(1,_intent);
                finish();
            }
        });

        iv_cancle.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                finish();
            }

        });
    }

}