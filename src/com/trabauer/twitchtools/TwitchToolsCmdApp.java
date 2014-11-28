package com.trabauer.twitchtools;

import com.trabauer.twitchtools.model.VideoPart;
import com.trabauer.twitchtools.model.twitch.TwitchVideo;

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

    public static void main(String[] argv) {

        TwitchVideo video = new TwitchVideo();
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

        video.updateTwitchVideoByUrl(twitchUrl);
        System.out.println("Downloading " + video.getTitle());




        System.out.println("Available Qualities:");
        for(String qual: video.getAvailableQualities()) {
            System.out.println("\t" + qual);
        }

//        String filename = video.getGame() + "\\" + video.getChannelName() + "\\" + video.getTitle();
        String filename = "WCS_Test_2014";
        downloadBroadcast(video ,quality, destinationDir, filename);


    }

    private static void downloadBroadcast(TwitchVideo video, String quality, String destinationDir, String filename) {
        if(quality == null)
            quality = video.getBestAvailableQuality();
        int partNr = 0;
        for(VideoPart videoPart: video.getVideoParts(quality)) {
            downloadPart(videoPart, destinationDir + "\\" + filename + "_" + quality + String.format("_%03d", partNr++), partNr, video.getVideoParts(quality).size());
        }
    }

    private static void downloadPart(VideoPart videoPart, String filename, int partNumber, int partCount) {
        URL url = videoPart.getUrl();
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
            //TODO implement interactive mode
            System.out.println("Not implemented yet!");




    }
}
