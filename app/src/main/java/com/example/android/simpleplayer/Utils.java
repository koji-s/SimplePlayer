package com.example.android.simpleplayer;

import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.util.Log;

import org.apache.commons.lang.time.DurationFormatUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class Utils {
    static public List<String> getDirList(String filePath) {

        File[] files;
        files = new File(filePath).listFiles();

        List<String> songList = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory() || files[i].getName().endsWith(".mp3")) {
                Log.d("Dir/FileName", files[i].getName());
                songList.add(files[i].getName());
            }
        }
        return songList;
    }

    static public List<String> getFileList() {
        //
        String filePath = Environment.getExternalStorageDirectory().getPath() + "/Music"
                + "/Duo3_0 復習用";

        File[] files;
        files = new File(filePath).listFiles();

        List<String> songList = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile() && files[i].getName().endsWith(".mp3")) {
                Log.d("FileName", files[i].getName());
                songList.add(files[i].getName());
            }
        }
        return songList;
    }

    private static String format(long startTime, long endTime) {

        String diffTime = DurationFormatUtils.formatPeriod(startTime,
                endTime, "HH:mm:ss.SSS");

        return diffTime;

    }

    static public String formatTime(int durationMillis) {
        String formatedTime = DurationFormatUtils.formatDuration(durationMillis, "HH:mm:ss");
        return formatedTime;
    }

}
