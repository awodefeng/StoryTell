package com.xxun.watch.storytall.activitys;

import android.app.Activity;
import android.support.v4.view.GestureDetectorCompat;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.xxun.watch.storytall.R;
import com.xxun.watch.storytall.StoryTellApp;
import com.xxun.watch.storytall.storyUtil.DividerItemDecoration;

public class DownloadingActivity extends Activity {
    private GestureDetectorCompat mDetector;
    private RecyclerView mDownLoadView;
    private LinearLayoutManager mLinearLayoutManager;
    private StoryTellApp mApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloading);

        mApp = (StoryTellApp)getApplication();

        mDetector = new GestureDetectorCompat(this, new MyGestureListener());

        mDownLoadView = (RecyclerView) findViewById(R.id.rv_downloading);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mDownLoadView.setLayoutManager(mLinearLayoutManager);
        mDownLoadView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST) {
        });


//        long dataId = mApp.getStoryDownloadList().get("50103456");
//        int[] bytesAndStatus = getBytesAndStatus(dataId);
//        if(bytesAndStatus[2] == DownloadManager.STATUS_SUCCESSFUL){
//            mTextDesc.setText("你已成功下载了该故事！");
//        }
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
            }
            return true;
        }
    }
}
