package com.careydevelopment.youtubedownloader;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import com.github.axet.vget.VGet;
import com.github.axet.vget.info.VGetParser;
import com.github.axet.vget.info.VideoFileInfo;
import com.github.axet.vget.info.VideoInfo;
import com.github.axet.vget.info.VideoInfo.States;
import com.github.axet.vget.vhs.VimeoInfo;
import com.github.axet.vget.vhs.YouTubeInfo;
import com.github.axet.wget.SpeedInfo;
import com.github.axet.wget.info.DownloadInfo;
import com.github.axet.wget.info.DownloadInfo.Part;
import com.github.axet.wget.info.ex.DownloadInterruptedError;

public class YouTubeManagedDownload {
    
    static class VGetStatus implements Runnable {
        VideoInfo videoinfo;
        long last;

        Map<VideoFileInfo, SpeedInfo> map = new HashMap<VideoFileInfo, SpeedInfo>();

        public VGetStatus(VideoInfo i) {
            this.videoinfo = i;
        }

        public SpeedInfo getSpeedInfo(VideoFileInfo dinfo) {
            SpeedInfo speedInfo = map.get(dinfo);
            if (speedInfo == null) {
                speedInfo = new SpeedInfo();
                speedInfo.start(dinfo.getCount());
                map.put(dinfo, speedInfo);
            }
            return speedInfo;
        }

        
        @Override
        public void run() {
            List<VideoFileInfo> dinfoList = videoinfo.getInfo();

            switch (videoinfo.getState()) {
            case EXTRACTING:
            case EXTRACTING_DONE:
            case DONE:
                if (videoinfo instanceof YouTubeInfo) {
                    YouTubeInfo i = (YouTubeInfo) videoinfo;
                    System.out.println(videoinfo.getState() + " " + i.getVideoQuality());
                } else if (videoinfo instanceof VimeoInfo) {
                    VimeoInfo i = (VimeoInfo) videoinfo;
                    System.out.println(videoinfo.getState() + " " + i.getVideoQuality());
                } else {
                    System.out.println("downloading unknown quality");
                }
                
                for (VideoFileInfo d : videoinfo.getInfo()) {
                    SpeedInfo speedInfo = getSpeedInfo(d);
                    speedInfo.end(d.getCount());
                    System.out.println(String.format("file:%d - %s (%s)", dinfoList.indexOf(d), d.targetFile,
                            formatSpeed(speedInfo.getAverageSpeed())));
                }
                
                break;
            case ERROR:
                System.out.println(videoinfo.getState() + " " + videoinfo.getDelay());

                if (dinfoList != null) {
                    for (DownloadInfo dinfo : dinfoList) {
                        System.out.println("file:" + dinfoList.indexOf(dinfo) + " - " + dinfo.getException() + " delay:"
                                + dinfo.getDelay());
                    }
                }
                
                break;
            case RETRYING:
                System.out.println(videoinfo.getState() + " " + videoinfo.getDelay());

                if (dinfoList != null) {
                    for (DownloadInfo dinfo : dinfoList) {
                        System.out.println("file:" + dinfoList.indexOf(dinfo) + " - " + dinfo.getState() + " "
                                + dinfo.getException() + " delay:" + dinfo.getDelay());
                    }
                }
                
                break;
            case DOWNLOADING:
                long now = System.currentTimeMillis();
                if (now - 1000 > last) {
                    last = now;

                    String parts = "";

                    for (VideoFileInfo dinfo : dinfoList) {
                        SpeedInfo speedInfo = getSpeedInfo(dinfo);
                        speedInfo.step(dinfo.getCount());

                        List<Part> pp = dinfo.getParts();
                        if (pp != null) {
                            for (Part p : pp) {
                                if (p.getState().equals(States.DOWNLOADING)) {
                                    parts += String.format("part#%d(%.2f) ", p.getNumber(),
                                            p.getCount() / (float) p.getLength());
                                }
                            }
                        }
                        System.out.println(String.format("file:%d - %s %.2f %s (%s)", dinfoList.indexOf(dinfo),
                                videoinfo.getState(), dinfo.getCount() / (float) dinfo.getLength(), parts,
                                formatSpeed(speedInfo.getCurrentSpeed())));
                    }
                }
                
                break;
            default:
                break;
            }
        }
    }

    
    public static String formatSpeed(long s) {
        if (s > 0.1 * 1024 * 1024 * 1024) {
            float f = s / 1024f / 1024f / 1024f;
            return String.format("%.1f GB/s", f);
        } else if (s > 0.1 * 1024 * 1024) {
            float f = s / 1024f / 1024f;
            return String.format("%.1f MB/s", f);
        } else {
            float f = s / 1024f;
            return String.format("%.1f kb/s", f);
        }
    }

    
    public static void main(String[] args) {
        String url = "https://www.youtube.com/watch?v=5G5fzf7dyhU";
        File path = new File("/test/");

        try {
            final AtomicBoolean stop = new AtomicBoolean(false);

            //create the URL object
            URL web = new URL(url);
            
            //instantiate the parser
            VGetParser user = VGet.parser(web);

            //instantiate VideoInfo object
            VideoInfo videoinfo = user.info(web);

            //instantiate VGet
            VGet v = new VGet(videoinfo, path);

            //create the inner class to track the status of the download
            VGetStatus notify = new VGetStatus(videoinfo);

            //call to get info like the title and download link
            v.extract(user, stop, notify);

            //print out the title
            System.out.println("Title: " + videoinfo.getTitle());
            
            //print out the download URL
            List<VideoFileInfo> list = videoinfo.getInfo();
            if (list != null) {
                for (VideoFileInfo d : list) {
                    // [OPTIONAL] setTarget file for each download source video/audio
                    // use d.getContentType() to determine which or use
                    // v.targetFile(dinfo, ext, conflict) to set name dynamically or
                    // d.targetFile = new File("/Downloads/CustomName.mp3");
                    // to set file name manually.
                    System.out.println("Download URL: " + d.getSource());
                }
            }

            //start the download
            v.download(user, stop, notify);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
