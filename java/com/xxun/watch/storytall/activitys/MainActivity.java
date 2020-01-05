package com.xxun.watch.storytall.activitys;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.RelativeLayout;
import android.media.MediaScannerConnection;
import android.net.Uri;

import com.xxun.watch.storytall.R;
import com.xxun.watch.storytall.StoryTellApp;
import com.xxun.watch.storytall.Const;
import com.xxun.watch.storytall.storyUtil.StoryTellUtil;

import android.content.pm.PackageManager;

import com.xiaoxun.statistics.XiaoXunStatisticsManager;
import com.xxun.watch.storytall.IMyAidlBinderStory;

import android.content.ServiceConnection;
import android.content.ComponentName;
import android.os.IBinder;
import android.os.Build;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends Activity implements View.OnClickListener {

    final String TAG = "MainActivity";
    private Context mContext;

    private ImageView mLoadingList;
    private ImageView mPlayerList;
    private ImageView mSongPre;
    private ImageView mSongNext;
    private ImageView mVolumeBigger;
    private ImageView mVolumeLow;
    private ImageView mPlayerStart;
    private ImageView mVolumeSize;
    private TextView mSongName;
    private RelativeLayout rPlayerStart;
    private RelativeLayout rPlayerList;
    private IMyAidlBinderStory iMyAidlBinderStory;

    private int song_player_state = 0;   //播放器的播放状态 播放-暂停
    private int maxVolume = 0;
    private int curVolume = 0;
    private int stepVolume = 0;
    private int curPlayStory = 0;

    private GestureDetectorCompat mDetector;
    //    private MediaPlayer mediaPlayer = null;// 播放器
    private AudioManager audioMgr = null; // Audio管理器，用了控制音量
    private AssetManager assetMgr = null; // 资源管理器
    private XiaoXunStatisticsManager statisticsManager;

    private StoryTellApp mApp;
    private BroadcastReceiver receiver;


    //连接远程服务
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            iMyAidlBinderStory = IMyAidlBinderStory.Stub.asInterface(service);
            Log.i("TAG", "bind service successful");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            iMyAidlBinderStory = null;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_main);
//        mDetector = new GestureDetectorCompat(this, new MyGestureListener());

        mApp = (StoryTellApp) getApplication();
//        curVolume = Integer.valueOf(mApp.getStringValue(Const.SHARE_PREF_MEDIA_VOLUME,"-1"));
        statisticsManager = (XiaoXunStatisticsManager) getSystemService("xun.statistics.service");
        statisticsManager.stats(XiaoXunStatisticsManager.STATS_STORY);

        mApp.initStoryList();
        initView();

        int default_story_status = Integer.valueOf(mApp.getStringValue(Const.SHARE_PREF_DEFAULT_STORY_STATUS, "0"));
        initMiTuMusic(default_story_status);
        initPlayer();
        initRevicer();

        bindService();
        Intent _intent = new Intent(Const.ACTION_BROAST_STORY_BEGIN);
        _intent.setPackage("com.xxun.watch.storydownloadservice");
        sendBroadcast(_intent);

        //start the story Service
//        Intent _intent = new Intent(new Intent(MainActivity.this,StoryReceiver.class));
//        _intent.setAction(Const.ACTION_BROAST_STORY_BEGIN);
//        Intent _intent = new Intent();
//        _intent.setAction(Const.ACTION_BROAST_STORY_BEGIN);
//        _intent.setPackage("com.xxun.watch.storydownloadservice");
//        sendBroadcast(_intent);
    }

    private void bindService() {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.xxun.watch.storydownloadservice", "com.xxun.watch.storydownloadservice.StoryBinderService"));
//        intent.setAction("com.xxun.watch.storydownloadservice.StoryBinderService");
//        intent.setPackage("com.xxun.watch.storydownloadservice");
        bindService(intent, conn, Context.BIND_AUTO_CREATE);

//        if (Build.VERSION.SDK_INT >= 26) {
//            mContext.startForegroundService(intent);
//        } else {
//            // Pre-O behavior.
//            mContext.startService(intent);
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initMediaStatus();
    }

    private void initRevicer() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.e("XunStoryTell", intent.getAction());
                if (intent.getAction().equals("action.update.story.notice")) {
                    Log.e("story.notice", " have is a get!");
                    try {
                        mApp.initStoryList();
                        Intent _intent = new Intent(Const.ACTION_BROAST_STORY_CHANGE_LIST_DATA);
                        sendBroadcast(_intent);
                    } catch (Exception exception) {
                        Log.e("story exception", exception.toString());
                    }
                } else if (intent.getAction().equals("action.update.story.position")) {
                    mApp.initStoryList();
                    String postion = intent.getStringExtra("story_position");
                    String opt_type = intent.getStringExtra("story_type");
                    if (opt_type.equals("play")) {
                        showPlayUiByPostion(Integer.valueOf(postion));
                        playLocalFile(Integer.valueOf(postion));
                    } else if (opt_type.equals("delete")) {
                        //ACTION_BROAST_MEDIA_STATUS_DELETE
                        Intent _intent = new Intent(Const.ACTION_BROAST_MEDIA_STATUS_DELETE);
                        sendBroadcast(_intent);
                        refreshUiByList(Integer.valueOf(postion));
                    }
                } else if (intent.getAction().equals(Const.ACTION_BROAST_MEDIA_SONG_STATUS_REQ)) {
                    String playStatue = intent.getStringExtra(Const.MEDIA_STORY_STATUE_PLAY_INFO);
                    String fileName = intent.getStringExtra(Const.MEDIA_STORY_STATUE_INTENT_DATA);
                    if (fileName != null && !fileName.equals("") && mApp.getStoryPlayerList().size() > 0) {
                        int position = StoryTellUtil.getPositionByStoryInfo(mApp.getStoryPlayerList(), fileName);
                        mSongName.setText(StoryTellUtil.getNameByStoryInfo(mApp.getStoryPlayerList().get(position)));
                        curPlayStory = position;
                    }
                    if (mApp.getStoryPlayerList().size() == 0) {
                        mSongName.setText(getResources().getText(R.string.story_main_page_no_story));
                        curPlayStory = 0;
                        song_player_state = 0;
                        pause();
                        mPlayerStart.setBackgroundResource(R.drawable.btn_action_start);
                    }
                    if (playStatue == null || playStatue.equals("1")) {
                        int position = StoryTellUtil.getPositionByStoryInfo(mApp.getStoryPlayerList(), fileName);
                        Log.e("get req", mApp.getStoryPlayerList().size() + ":" + fileName + ":" + position);
                        showPlayUiByPostion(position);
                    } else {
                        song_player_state = 0;
                        mPlayerStart.setBackgroundResource(R.drawable.btn_action_start);
                    }
                } else if (intent.getAction().equals("com.xiaoxun.xxun.story.finish")) {
                    MainActivity.this.finish();
                    System.exit(0);
                }

            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction("action.update.story.notice");
        filter.addAction("action.update.story.position");
        filter.addAction("com.xiaoxun.xxun.story.finish");
        filter.addAction(Const.ACTION_BROAST_MEDIA_SONG_STATUS_REQ);
        registerReceiver(receiver, filter);

    }

    private void initMiTuMusic(int status) {
        if (status == 2 || status == 1) {
            try {
                assetMgr = this.getAssets();
                InputStream inputStream = assetMgr.open("qimiaoli.mp3");
                String yuanMD5 = StoryTellUtil.getFileMD5(inputStream);

                String story_qimiaoli_name = this.getResources().getText(R.string.story_qimiaoli_name) + "_222222.mp3";
                File file = new File(mApp.getMusicDir(), story_qimiaoli_name);
                if (file.exists()) {
                    String xianMD5 = StoryTellUtil.getFileMD5(file);
                    if (!yuanMD5.equals(xianMD5)) {
                        Log.i(TAG, "initMiTuMusic: 音频文件损坏 重新写入");
                        file.delete();
                        FileOutputStream fileOutputStream = new FileOutputStream(file);
                        inputStream = assetMgr.open("qimiaoli.mp3");
                        byte[] buffer = new byte[1024];
                        int len = -1;
                        while ((len = inputStream.read(buffer)) != -1) {
                            fileOutputStream.write(buffer, 0, len);
                        }
                        fileOutputStream.close();
                        inputStream.close();
                    } else {
                        Log.i(TAG, "initMiTuMusic: 音频文件正常！");
                    }
                }
            } catch (IOException e) {
            } catch (NoSuchAlgorithmException e) {
            }
        } else {
            try {
                assetMgr = this.getAssets();
                InputStream isQimiaoli = assetMgr.open("qimiaoli.mp3");
                String story_qimiaoli_name = this.getResources().getText(R.string.story_qimiaoli_name) + "_222222.mp3";
                File fileQimiaoli = new File(mApp.getMusicDir(), story_qimiaoli_name);

                if (!fileQimiaoli.exists()) {
                    FileOutputStream fileOutputStream = new FileOutputStream(fileQimiaoli);
                    byte[] buffer = new byte[1024];
                    int len = -1;
                    while ((len = isQimiaoli.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, len);
                    }
                    fileOutputStream.close();
                    isQimiaoli.close();
                }
                mApp.setStringValue(Const.SHARE_PREF_DEFAULT_STORY_STATUS, "1");
                mSongName.setText(getResources().getText(R.string.story_qimiaoli_name));
                scanFile(MainActivity.this, new String[]{mApp.getMusicDir() + "/" + story_qimiaoli_name});
            } catch (Exception e) {
                e.printStackTrace();
            }
            mApp.initStoryList();
        }
    }

    private void initPlayer() {
        audioMgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        // 获取最大音乐音量
        maxVolume = audioMgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        // 每次调整的音量大概为最大音量的1/5
        stepVolume = maxVolume / 5;
        // 初始化音量大概为2*stepVolume
        Log.e("volume", curVolume + ":");
        if (curVolume == -1) {
            curVolume = stepVolume * 2;
        }

        curVolume = audioMgr.getStreamVolume(AudioManager.STREAM_MUSIC);

//        mediaPlayer = new MediaPlayer();
        updateVolumeView(curVolume / stepVolume);
        adjustVolume();
    }

    private void initView() {
        mLoadingList = (ImageView) findViewById(R.id.iv_loadding_list);
        mPlayerList = (ImageView) findViewById(R.id.iv_play_list);
        mSongPre = (ImageView) findViewById(R.id.iv_song_pre);
        mVolumeBigger = (ImageView) findViewById(R.id.iv_volume_top);
        mSongNext = (ImageView) findViewById(R.id.iv_song_next);
        mVolumeLow = (ImageView) findViewById(R.id.iv_volume_bottom);
        mPlayerStart = (ImageView) findViewById(R.id.iv_play_start);
        mVolumeSize = (ImageView) findViewById(R.id.iv_volume_size);
        rPlayerStart = (RelativeLayout) findViewById(R.id.rl_play_start);
        rPlayerList = (RelativeLayout) findViewById(R.id.rl_play_list);

        mSongName = (TextView) findViewById(R.id.tv_song_name);
        if (mApp.getStoryPlayerList() == null || mApp.getStoryPlayerList().size() == 0) {
            mSongName.setText(getResources().getText(R.string.story_main_page_no_story));
        } else {
            mSongName.setText(StoryTellUtil.getNameByStoryInfo(mApp.getStoryPlayerList().get(curPlayStory)));
        }

        mLoadingList.setOnClickListener(this);
        rPlayerList.setOnClickListener(this);
        mSongPre.setOnClickListener(this);
        mVolumeBigger.setOnClickListener(this);
        mSongNext.setOnClickListener(this);
        mVolumeLow.setOnClickListener(this);
        rPlayerStart.setOnClickListener(this);
    }

    private void refreshUiByList(int positon) {
        if (mApp.getStoryPlayerList().size() == 0) {
            mSongName.setText(getResources().getText(R.string.story_main_page_no_story));
            curPlayStory = 0;
            song_player_state = 0;
            pause();
            mPlayerStart.setBackgroundResource(R.drawable.btn_action_start);
            return;
        }
        if (positon == curPlayStory) {
            if (positon == 0) {
                curPlayStory = 0;
            } else {
                curPlayStory = curPlayStory - 1;
            }
        } else if (positon > curPlayStory) {

        } else if (positon < curPlayStory) {
            curPlayStory = curPlayStory - 1;
        }
        mSongName.setText(StoryTellUtil.getNameByStoryInfo(mApp.getStoryPlayerList().get(curPlayStory)));

        if (song_player_state == 1) {
            playLocalFile(curPlayStory);
        }
    }

    private void showPlayUiByPostion(int postion) {
        mSongName.setText(StoryTellUtil.getNameByStoryInfo(mApp.getStoryPlayerList().get(postion)));
        curPlayStory = postion;
        song_player_state = 1;
        mPlayerStart.setBackgroundResource(R.drawable.player_pause);
        mPlayerStart.invalidate();
    }

    private void initMediaStatus() {
        Intent _intent = new Intent(Const.ACTION_BROAST_MEDIA_SONG_STATUS);
        sendBroadcast(_intent);
    }

    private void playLocalFile(int postion) {
        try {
            Log.e("playLocalFile:", mApp.getStoryPlayerList().size() + "size");
            if (mApp.getStoryPlayerList().size() == 0) {
                return;
            }
            String mediaName = mApp.getStoryPlayerList().get(postion);
            Log.e("mediapath:", mediaName);
            Intent _intent = new Intent(Const.ACTION_BROAST_MEDIA_STATUS_PLAY);
            _intent.setPackage("com.xxun.watch.storydownloadservice");
            _intent.putExtra(Const.MEDIA_STORY_STATUE_INTENT_DATA, mediaName);
            sendBroadcast(_intent);
//            mediaPlayer.reset();
            // 使用MediaPlayer加载指定的声音文件。
//            mediaPlayer.setDataSource(mediaPath);
            // 准备声音
//            mediaPlayer.prepare();
            // 播放
//            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void scanFile(Context context, String[] path) {
        MediaScannerConnection.scanFile(context, path,
                null, new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String s, Uri uri) {
                        Log.e("TAG", "download Story onScanCompleted");
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent _intent = new Intent(Const.ACTION_BROAST_STORY_PREVIEW_FINISH);
        _intent.putExtra("from","StoryTell");
        sendBroadcast(_intent);
        finish();
        overridePendingTransition(R.anim.activity_slide_in_right, R.anim.activity_slide_out_right);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if(mediaPlayer != null){
//            mediaPlayer.release();
//            mediaPlayer = null;
//        }
        unregisterReceiver(receiver);
        unbindService(conn);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_loadding_list:
                break;
            case R.id.rl_play_list:
                Log.e("hello:", "onclick play list");
                Intent intent1 = new Intent(MainActivity.this, PlayerListActivity.class);
                startActivity(intent1);
                overridePendingTransition(R.anim.activity_slide_in_left, R.anim.activity_slide_out_left);
                break;
            case R.id.iv_song_pre:
                if (mApp.getStoryPlayerList().size() == 0) {
                    return;
                }
                if (curPlayStory == 0) {
                    curPlayStory = mApp.getStoryPlayerList().size() - 1;
                } else if (curPlayStory > 0) {
                    curPlayStory--;
                }
                mSongName.setText(StoryTellUtil.getNameByStoryInfo(mApp.getStoryPlayerList().get(curPlayStory)));
                if (song_player_state == 1) {
                    playLocalFile(curPlayStory);
                }
                break;
            case R.id.iv_song_next:
                if (mApp.getStoryPlayerList().size() == 0) {
                    return;
                }
                if (curPlayStory == mApp.getStoryPlayerList().size() - 1) {
                    curPlayStory = 0;
                } else if (curPlayStory < mApp.getStoryPlayerList().size() - 1) {
                    curPlayStory++;
                }
                mSongName.setText(StoryTellUtil.getNameByStoryInfo(mApp.getStoryPlayerList().get(curPlayStory)));
                if (song_player_state == 1) {
                    playLocalFile(curPlayStory);
                }
                break;
            case R.id.iv_volume_top:
                curVolume += stepVolume;
                if (curVolume >= maxVolume) {
                    curVolume = maxVolume;
                }
                Log.e("volume", curVolume + ":" + maxVolume + ":" + stepVolume);
                updateVolumeView(curVolume / stepVolume);
                adjustVolume();
//                mApp.setStringValue(Const.SHARE_PREF_MEDIA_VOLUME,String.valueOf(curVolume));
                break;
            case R.id.iv_volume_bottom:
                curVolume -= stepVolume;
                if (curVolume <= stepVolume) {
                    curVolume = stepVolume;
                }
                updateVolumeView(curVolume / stepVolume);
                adjustVolume();
//                mApp.setStringValue(Const.SHARE_PREF_MEDIA_VOLUME,String.valueOf(curVolume));
                break;
            case R.id.rl_play_start:
                if (mApp.getStoryPlayerList().size() == 0) {
                    return;
                }
                if (song_player_state == 0) {
                    song_player_state = 1;
                    mPlayerStart.setBackgroundResource(R.drawable.player_pause);
                    playLocalFile(curPlayStory);
                } else {
                    song_player_state = 0;
                    pause();
                    mPlayerStart.setBackgroundResource(R.drawable.btn_action_start);
                }
                break;
        }
    }

    private void pause() {
        Intent _intent = new Intent(Const.ACTION_BROAST_MEDIA_STATUS_PAUSE);
        sendBroadcast(_intent);
//        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
//            mediaPlayer.pause();
//        }
    }

    /**
     * user:zhangjun5 time:14:21 date:2017/9/4
     * desc:调整音量
     **/
    private void adjustVolume() {
//        audioMgr.setStreamVolume(AudioManager.STREAM_MUSIC, curVolume,
//                AudioManager.FLAG_PLAY_SOUND);
//        mediaPlayer.setVolume(curVolume/5,curVolume/5);
        Log.e("curVolume", String.valueOf(curVolume));
        Intent _intent = new Intent(Const.ACTION_BROAST_STORY_CHANGE_SOUND);
        _intent.putExtra(Const.MEDIA_STORY_STATUE_SOUND_CHANGE, String.valueOf(curVolume));
        sendBroadcast(_intent);
    }

    private void updateVolumeView(int volume_size) {
        if (volume_size > 5) {
            mVolumeSize.setBackgroundResource(R.drawable.volume_size_5);
            return;
        }
        if (volume_size < 0) {
            mVolumeSize.setBackgroundResource(R.drawable.volume_size_0);
            return;
        }

        if (volume_size == 0) {
            mVolumeSize.setBackgroundResource(R.drawable.volume_size_0);
        } else if (volume_size == 1) {
            mVolumeSize.setBackgroundResource(R.drawable.volume_size_1);
        } else if (volume_size == 2) {
            mVolumeSize.setBackgroundResource(R.drawable.volume_size_2);
        } else if (volume_size == 3) {
            mVolumeSize.setBackgroundResource(R.drawable.volume_size_3);
        } else if (volume_size == 4) {
            mVolumeSize.setBackgroundResource(R.drawable.volume_size_4);
        } else if (volume_size == 5) {
            mVolumeSize.setBackgroundResource(R.drawable.volume_size_5);
        }
    }

    //modify by liaoyi 19/3/4 由于右滑退出主题冲突touch事件 改为dispath监听
    float downX;
    float moveX;
    boolean canJump = true;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        float x = ev.getX();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = x;
                moveX = x;
                canJump = true;
                break;
            case MotionEvent.ACTION_MOVE:
                if(canJump) {
                    float move = moveX - x;
                    if (move < 0) {
                        canJump = false;
                    }
                    moveX = x;
                }
                break;
            case MotionEvent.ACTION_UP:
                float upMove = downX - x;
                if (upMove > 50 && canJump) {
                    Intent intent = new Intent(MainActivity.this, PlayerListActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.activity_slide_in_left, R.anim.activity_slide_out_left);
                }
                break;
        }

        return super.dispatchTouchEvent(ev);
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        Log.i(TAG, "onTouchEvent:  main activity");
//        this.mDetector.onTouchEvent(event);
//        return super.onTouchEvent(event);
//    }
//end

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final String DEBUG_TAG = "Gestures";
        private int verticalMinDistance = 20;

        private int minVelocity = 0;

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {
            Log.d(DEBUG_TAG, "onFling: " + event1.toString() + event2.toString());
            if (event1.getX() - event2.getX() > verticalMinDistance && Math.abs(velocityX) > minVelocity) {
                Intent intent = new Intent(MainActivity.this, PlayerListActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.activity_slide_in_left, R.anim.activity_slide_out_left);
            } else if (event2.getX() - event1.getX() > verticalMinDistance && Math.abs(velocityX) > minVelocity) {
                Intent _intent = new Intent(Const.ACTION_BROAST_STORY_PREVIEW_FINISH);
                sendBroadcast(_intent);
                finish();
                overridePendingTransition(R.anim.activity_slide_in_right, R.anim.activity_slide_out_right);
            }
            return true;
        }
    }
}
