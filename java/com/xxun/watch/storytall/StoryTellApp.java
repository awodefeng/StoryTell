package com.xxun.watch.storytall;


import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by zhangjun5 on 2017/9/6.
 */

public class StoryTellApp extends Application {
    private final String TAG="StoryTellApp";
    private static File baseDir;
    private static File logDir;
    private static File FileDir;
    private static File MusicDir;

    private ArrayList<String> storyPlayerList = new ArrayList<>();//故事列表
    public ArrayList<String> getStoryPlayerList(){
        return storyPlayerList;
    }

    public static File getLogDir() {
        if (!logDir.isDirectory()) {
            logDir.delete();
            logDir.mkdirs();
        }
        return logDir;
    }
    public static File getFileDir() {
        if (!FileDir.isDirectory()) {
            FileDir.delete();
            FileDir.mkdirs();
        }
        return FileDir;
    }
    public static File getMusicDir() {
        if (!MusicDir.isDirectory()) {
            MusicDir.delete();
            MusicDir.mkdirs();
        }
        return MusicDir;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        initFileDirs();
    }
    public void initStoryList(){
    	try{
        String filePath = getMusicDir().getPath();
        // 得到该路径文件夹下所有的文件
        File fileAll = new File(filePath);
	Log.e("FIle:",fileAll+":"+filePath+":"+fileAll.listFiles());
        File[] files = fileAll.listFiles();
        storyPlayerList.clear();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if(file.getName().contains(".mp3")
                    || file.getName().contains(".amr")
                    || file.getName().contains(".m4a")) {
                storyPlayerList.add(file.getName());
            }
        }
      }catch(Exception e){
      	Log.e("storyTell",e.toString());
      }
    }

    public void initFileDirs() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            if(MusicDir == null){
                MusicDir = new File(Environment.getExternalStorageDirectory(), Const.MY_MUSIC_DIR);
            }else
                MusicDir = new File(baseDir.getPath());

            if (MusicDir.exists() && !MusicDir.isDirectory()) {
                MusicDir.delete();
            }
            if (!MusicDir.exists()) {
                MusicDir.mkdirs();
            }

            if (baseDir == null)
                baseDir = new File(Environment.getExternalStorageDirectory(), Const.MY_BASE_DIR);
            else
                baseDir = new File(baseDir.getPath());

            if (baseDir.exists() && !baseDir.isDirectory()) {
                baseDir.delete();
            }
            if (!baseDir.exists()) {
                baseDir.mkdirs();
            }

            logDir = new File(baseDir, Const.MY_LOG_DIR);
            if (logDir.exists() && !logDir.isDirectory()) {
                logDir.delete();
            }

            if (!logDir.exists()) {
                logDir.mkdir();
            }

            FileDir = new File(baseDir, Const.MY_FILE_DIR);
            if (FileDir.exists() && !FileDir.isDirectory()) {
                FileDir.delete();
            }

            if (!FileDir.exists()) {
                FileDir.mkdir();
            }

        }
    }
    public void setStringValue(String key, String value) {
        final SharedPreferences preferences = getSharedPreferences(Const.SHARE_PREF_NAME, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }
    public String getStringValue(String key, String defValue) {
        String str = getSharedPreferences(Const.SHARE_PREF_NAME, Context.MODE_PRIVATE )
                .getString(key, defValue);
        return str;
    }

    public File getCurLogFile() {
        File file = null;
        File baseDir;
        File dir;
        baseDir = new File(Environment.getExternalStorageDirectory(), Const.MY_BASE_DIR);
        dir = new File(baseDir, Const.MY_LOG_DIR);
        Date nowtime = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String currDay = dateFormat.format(nowtime);
        StringBuilder fileNameBuff = new StringBuilder();
        fileNameBuff.append(currDay);//调整一下log文件命名方式，方便查找
        fileNameBuff.append("_");
        fileNameBuff.append("all");//修改sdcardlog,不区分eid，方便分析
        fileNameBuff.append(".log");

        file = new File(dir, fileNameBuff.toString());

        return file;
    }

    public void sdcardLog(String sMsg) {
        Date nowtime = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String currTime = dateFormat.format(nowtime);

        String logText = currTime + " " + sMsg + "\n";
        try {
            FileOutputStream fos = new FileOutputStream(getCurLogFile(), true);
            fos.write(logText.getBytes());
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
