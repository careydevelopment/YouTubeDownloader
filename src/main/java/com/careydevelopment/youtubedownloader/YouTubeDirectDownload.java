package com.careydevelopment.youtubedownloader;

import java.io.File;
import java.net.URL;

import com.github.axet.vget.VGet;

public class YouTubeDirectDownload {

    public static void main(String[] args) {
        try {
            String url = "https://www.youtube.com/watch?v=5G5fzf7dyhU";
            String path = "/test/";
            VGet v = new VGet(new URL(url), new File(path));
            v.download();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
