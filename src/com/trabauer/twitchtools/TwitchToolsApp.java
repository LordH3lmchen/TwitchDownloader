package com.trabauer.twitchtools;

import com.trabauer.twitchtools.twitch.Twitch;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by flo on 03.11.14.
 */
public class TwitchToolsApp {
    public static void main(String[] argv) {

        Video video = null;
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


    }

    private static void interactiveShell() {


    }
}
