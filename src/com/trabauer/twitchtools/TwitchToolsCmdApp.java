package com.trabauer.twitchtools;

import com.trabauer.twitchtools.twitch.Twitch;
import com.trabauer.twitchtools.twitch.TwitchVideo;
import com.trabauer.twitchtools.twitch.TwitchVideoPart;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by flo on 03.11.14.
 */
public class TwitchToolsCmdApp {
    public static void main(String[] argv) {

        TwitchVideo video = null;
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
        else {
            System.out.println("Invalid number of arguments.");
            System.exit(0);
        }

        video = Twitch.getPastBroadcastByUrl(twitchUrl);
        System.out.println("Downloading " + video.getTitle());




        System.out.println("Available Qualities:");
        for(String qual: video.getAvailableQualities()) {
            System.out.println("\t" + qual);
        }

        downloadBroadcast(video);




    }

    private static void downloadBroadcast(TwitchVideo video) {
        System.out.println("Best available quality is " + video.getBestAvailableQuality() + "-quality");
        for(VideoPart videoPart: video.getVideoParts(video.getBestAvailableQuality())) {
            downloadPart(videoPart);
        }
    }

    private static void downloadPart(VideoPart videoPart) {
        System.out.println("Downloading " + videoPart.getUrl());
    }

    private static void interactiveShell() {
            //TODO implement interactive mode
            System.out.println("Not implemented yet!");




    }
}
