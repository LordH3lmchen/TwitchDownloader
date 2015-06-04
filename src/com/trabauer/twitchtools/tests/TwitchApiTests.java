package com.trabauer.twitchtools.tests;

import com.trabauer.twitchtools.model.twitch.TwitchChannel;
import com.trabauer.twitchtools.model.twitch.TwitchStream;
import com.trabauer.twitchtools.model.twitch.TwitchVideoInfo;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by flo on 5/31/15.
 */
public class TwitchApiTests {

    @Test
    public void channelTest() {

        TwitchChannel tChannel = null;
        try {
            tChannel = TwitchChannel.getTwitchChannel("taketv");
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.print(tChannel);



    }

    @Test
    public void streamTest() {
        TwitchStream takeTvStream = null;
        try {
            takeTvStream = TwitchStream.getTwitchStreamFromAPI("taketv");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(takeTvStream);
//        cryaotic
        TwitchStream cryaoticStream = null;
        try {
            cryaoticStream = TwitchStream.getTwitchStreamFromAPI("cryaotic");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(cryaoticStream);

    }

    @Test
    public void highlightTest() {
        TwitchVideoInfo tvi = new TwitchVideoInfo();
        try {
            tvi.update("v5724050");
            tvi.getDownloadInfo();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(tvi);
    }
}
