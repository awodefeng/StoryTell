package com.xxun.watch.storytall.storyUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import android.util.Log;


/**
 * Created by zhangjun5 on 2017/10/18.
 */

public class StoryTellUtil {
    public static String getNameByStoryInfo(String storyInfo) {
        String storyName = "";
        String name[] = storyInfo.split("_");
        if (name.length == 1) {
            storyName = name[0];
        } else if (name.length > 1) {
            for (int i = 0; i < name.length - 1; i++) {
                storyName += name[i];
            }
        }

        return storyName;

    }

    public static int getPositionByStoryInfo(ArrayList<String> storyList, String storyName) {
        if (storyList == null || storyName == null) {
            return 0;
        }
        int position = 0;
        for (String fileName : storyList) {
            Log.e("compare:", fileName + ":" + storyName);
            if (fileName.equals(storyName)) {
                break;
            }
            position++;
        }
        if (position == storyList.size()) {
            position = 0;
        }
        return position;

    }


    public static String getFileMD5(File file) throws NoSuchAlgorithmException, IOException {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest;
        FileInputStream in;
        byte buffer[] = new byte[1024];
        int len;
        digest = MessageDigest.getInstance("MD5");
        in = new FileInputStream(file);
        while ((len = in.read(buffer, 0, 1024)) != -1) {
            digest.update(buffer, 0, len);
        }
        in.close();
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(16);
    }

    public static String getFileMD5(InputStream is) throws NoSuchAlgorithmException, IOException {
        if (is == null) {
            return null;
        }
        MessageDigest digest;
        byte buffer[] = new byte[1024];
        int len;
        digest = MessageDigest.getInstance("MD5");
        while ((len = is.read(buffer, 0, 1024)) != -1) {
            digest.update(buffer, 0, len);
        }
        is.close();
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(16);
    }
}
