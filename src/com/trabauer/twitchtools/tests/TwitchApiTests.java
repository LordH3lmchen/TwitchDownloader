package com.trabauer.twitchtools.tests;

import com.trabauer.twitchtools.model.twitch.TwitchChannel;
import com.trabauer.twitchtools.model.twitch.TwitchStream;
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
            takeTvStream = TwitchStream.getTwitchStream("taketv");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(takeTvStream);
//        cryaotic
        TwitchStream cryaoticStream = null;
        try {
            cryaoticStream = TwitchStream.getTwitchStream("cryaotic");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(cryaoticStream);

    }
}
