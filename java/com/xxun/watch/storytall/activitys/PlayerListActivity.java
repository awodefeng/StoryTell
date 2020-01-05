package com.xxun.watch.storytall.activitys;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.view.GestureDetectorCompat;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.xxun.watch.storytall.Const;
import com.xxun.watch.storytall.R;
import com.xxun.watch.storytall.StoryTellApp;
import com.xxun.watch.storytall.adapters.PlayListAdapter;
import com.xxun.watch.storytall.storyUtil.DividerItemDecoration;

import java.io.File;

public class PlayerListActivity extends Activity {
    private GestureDetectorCompat mDetector;
    private TextView mTextDesc;
    private StoryTellApp mApp;
    private RecyclerView mPlayListView;
    private LinearLayoutManager mLinearLayoutManager;
    private PlayListAdapter mPlayListAdapter;
    private BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_list);

        mApp = (StoryTellApp) getApplication();
        mApp.initStoryList();

        mTextDesc = (TextView) findViewById(R.id.textDesc);
        mDetector = new GestureDetectorCompat(this, new MyGestureListener());
        mPlayListView = (RecyclerView) findViewById(R.id.rv_playList);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mPlayListView.setLayoutManager(mLinearLayoutManager);
        mPlayListView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST) {
        });

        mPlayListAdapter = new PlayListAdapter(this,mApp.getStoryPlayerList());
        mPlayListView.setAdapter(mPlayListAdapter);
        mPlayListAdapter.setOnItemClickLitener(new Const.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(view.getId() == R.id.iv_story_icon) {
                    Log.e("click"," this icon zone1");
                    Intent _intent = new Intent(PlayerListActivity.this, DeleteStoryActivity.class);
                    _intent.putExtra("story_position",String.valueOf(position));
                    startActivityForResult(_intent,1);

                }else if(view.getId() == R.id.tv_story_name){
                    Log.e("click"," this rl zone2");
                    Intent _intent = new Intent("action.update.story.position");
                    _intent.putExtra("story_position",String.valueOf(position));
                    _intent.putExtra("story_type","play");
                    sendBroadcast(_intent);
                    finish();
                    overridePendingTransition(R.anim.activity_slide_in_right, R.anim.activity_slide_out_right);
                }
            }
        });

        if(mApp.getStoryPlayerList().size() > 0){
            mTextDesc.setVisibility(View.GONE);
            mPlayListView.setVisibility(View.VISIBLE);
        }else{
            mTextDesc.setVisibility(View.VISIBLE);
            mPlayListView.setVisibility(View.GONE);
        }
        initRevicer();
    }
    
     private void initRevicer(){
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.e("XunStoryTell",intent.getAction());
                if(intent.getAction().equals(Const.ACTION_BROAST_STORY_CHANGE_LIST_DATA)) {
                    mPlayListAdapter.notifyDataSetChanged();
                    if(mApp.getStoryPlayerList().size() > 0){
						            mTextDesc.setVisibility(View.GONE);
						            mPlayListView.setVisibility(View.VISIBLE);
						        }else{
						            mTextDesc.setVisibility(View.VISIBLE);
						            mPlayListView.setVisibility(View.GONE);
						        }
                 }
            }
            
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Const.ACTION_BROAST_STORY_CHANGE_LIST_DATA);
        registerReceiver(receiver,filter);
      }    

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.activity_slide_in_right, R.anim.activity_slide_out_right);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode == 1){
            super.onActivityResult(requestCode,resultCode,data);
            int position  = Integer.valueOf(data.getStringExtra("story_position_back"));

            String filePath = mApp.getMusicDir() + "/" + mApp.getStoryPlayerList().get(position);
            if (deleteFile(filePath)) {
                mApp.getStoryPlayerList().remove(position);
                mPlayListAdapter.notifyDataSetChanged();
                Intent _intent = new Intent("action.update.story.position");
                _intent.putExtra("story_position",String.valueOf(position));
                _intent.putExtra("story_type","delete");
                sendBroadcast(_intent);
                if(mApp.getStoryPlayerList().size() == 0){
                    mTextDesc.setVisibility(View.VISIBLE);
                    mPlayListView.setVisibility(View.GONE);
                }
            }
        }
    }

    public boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.isFile() && file.exists()) {
            return file.delete();
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final String DEBUG_TAG = "Gestures";
        private int verticalMinDistance = 20;

        private int minVelocity         = 0;
        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {
            Log.d(DEBUG_TAG, "onFling: " + event1.toString()+event2.toString());
            if(event1.getX() - event2.getX() > verticalMinDistance && Math.abs(velocityX) > minVelocity) {

            }else if(event2.getX() - event1.getX() > verticalMinDistance && Math.abs(velocityX) > minVelocity) {
                finish();
                overridePendingTransition(R.anim.activity_slide_in_right, R.anim.activity_slide_out_right);
            }
            return true;
        }
    }
}
