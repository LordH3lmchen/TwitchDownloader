package com.trabauer.twitchtools;

import com.trabauer.twitchtools.model.twitch.TwitchVideoPart;
import com.trabauer.twitchtools.model.twitch.TwitchVideoInfo;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by flo on 03.11.14.
 */
public class TwitchToolsCmdApp {

    private static final String destinationDir = "D:\\twitchStreams";

    public static void main(String[] argv) throws IOException {

        TwitchVideoInfo video = new TwitchVideoInfo();
        URL twitchUrl = null;
        String quality = null;


        if(argv.length == 0) {
            System.out.println("starting interactive shell");
            interactiveShell();
        }

        if(argv.length >= 1)
            try {
                twitchUrl = new URL(argv[0]);
            } catch (MalformedURLException e) {
                System.out.println("Invlaid URL Specification. \n" + e.toString());
                System.exit(1);
            }
        if(argv.length == 2)
            quality = argv[1];
        if(argv.length > 2) {
            System.out.println("Invalid number of arguments.");
            System.exit(0);
        }

        try {
            video.update(twitchUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Downloading " + video.getTitle());




        System.out.println("Available Qualities:");
        for(String qual: video.getDownloadInfo().getAvailableQualities()) {
            System.out.println("\t" + qual);
        }

//        String filename = video.getGame() + "\\" + video.getChannelName() + "\\" + video.getTitle();
        String filename = "WCS_Test_2014";
        try {
            downloadBroadcast(video ,quality, destinationDir, filename);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private static void downloadBroadcast(TwitchVideoInfo video, String quality, String destinationDir, String filename) throws IOException {
        if(quality == null)
            quality = video.getDownloadInfo().getBestAvailableQuality();
        int partNr = 0;
        for(TwitchVideoPart videoPart: video.getDownloadInfo().getTwitchBroadcastParts(quality)) {
            downloadPart(videoPart, destinationDir + "\\" + filename + "_" + quality + String.format("_%03d", partNr++), partNr, video.getDownloadInfo().getTwitchBroadcastParts(quality).size());
        }
    }

    private static void downloadPart(TwitchVideoPart videoPart, String filename, int partNumber, int partCount) throws MalformedURLException {
        URL url = new URL(videoPart.getUrl());
        InputStream is = null;
        FileOutputStream fos = null;
        String fileExt = "";
        {
            int i = url.getFile().lastIndexOf('.');
            if(i>0)
                fileExt = url.getFile().substring(i);
        }
        try {
            URLConnection urlConnection = url.openConnection();
            int partSize = urlConnection.getContentLength();
            is = urlConnection.getInputStream();
            fos = new FileOutputStream(filename + fileExt);
            byte[] buffer = new byte[4096];
            int len;
            int done = 0;
            while ((len = is.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
                done += len;
                int hashCount = (done*10)/partSize;
                int blankCount = 10 - hashCount;
                String progressBar = "";
                for(int i=0; i<hashCount; i++)
                    progressBar += "#";
                for(int i=0; i<blankCount; i++)
                    progressBar += " ";
                float percent = (done*100)/partSize;
                System.out.printf("\r%2d/%2d [%s]%3.1f%% %4dMiB/%4dMiB",
                        partNumber,
                        partCount,
                        progressBar,
                        percent,
                        done/(1024*1024),
                        partSize/(1024*1024));
            }
            System.out.printf("\r%2d/%2d [%s]%3d%% %s\n", partNumber, partCount, "##########",100 , filename + fileExt);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void interactiveShell() {

            System.out.println("Not implemented yet!");




    }
}
