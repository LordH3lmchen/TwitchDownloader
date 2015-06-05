package com.trabauer.twitchtools.tests;

import com.trabauer.twitchtools.model.twitch.TwitchChannel;
import com.trabauer.twitchtools.model.twitch.TwitchStream;
import com.trabauer.twitchtools.model.twitch.TwitchVideoInfo;
import org.junit.Test;

import java.io.IOException;
import java.util.AbstractCollection;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;

/**
 * Created by flo on 5/31/15.
 */
public class TwitchApiTests {

    @Test
    public void TwitchChannelModelTests() throws IOException {
        TwitchChannel tChannel = null;
        tChannel = TwitchChannel.getTwitchChannel("taketv");
        assertEquals("taketv", tChannel.getName());

        // Getter and Setter Methods
        String mature = tChannel.getMature();
        mature += "FooBar";
        tChannel.setMature(mature);
        assertEquals(mature, tChannel.getMature());

        String status = tChannel.getStatus();
        status += "FooBar";
        tChannel.setStatus(status);
        assertEquals(status, tChannel.getStatus());

        String broadcasterLanguage = tChannel.getBroadcasterLanguage();
        broadcasterLanguage += "FooBar";
        tChannel.setBroadcasterLanguage(broadcasterLanguage);
        assertEquals(broadcasterLanguage, tChannel.getBroadcasterLanguage());

        String dispName = tChannel.getDisplayName().concat("FooBar");
        tChannel.setDisplayName(dispName);
        assertEquals(tChannel.getDisplayName(), dispName);

        String game = tChannel.getGame().concat("FooBar");
        tChannel.setGame(game);
        assertEquals(game, tChannel.getGame());

        int delay = tChannel.getDelay();
        tChannel.setDelay(++delay);
        assertEquals(delay, tChannel.getDelay());

        String language = tChannel.getLanguage().concat("FooBar");
        tChannel.setLanguage(language);
        assertEquals(language, tChannel.getLanguage());

        long id = tChannel.getId();
        tChannel.setId(++id);
        assertEquals(id, tChannel.getId());

        String name = tChannel.getName().concat("fooBar");
        tChannel.setName(name);
        assertEquals(name, tChannel.getName());

        String createdAt = tChannel.getCreatedAt().concat("FooBar");
        tChannel.setCreatedAt(createdAt);
        assertEquals(tChannel.getCreatedAt(), createdAt);

        String updatedAt = tChannel.getUpdatedAt().concat("FooBar");
        tChannel.setUpdatedAt(updatedAt);
        assertEquals(updatedAt, tChannel.getUpdatedAt());

        String logoUrlStr = tChannel.getLogoUrlString().concat("FooBar");
        tChannel.setLogoUrlString(logoUrlStr);
        assertEquals(logoUrlStr, tChannel.getLogoUrlString());

        String videoBannerUrlStr = tChannel.getVideoBannerUrlString().concat("FooBar");
        tChannel.setVideoBannerUrlString(videoBannerUrlStr);
        assertEquals(videoBannerUrlStr, tChannel.getVideoBannerUrlString());


        if(tChannel.getBackgroundUrlString() != null) {
            String backgroundUrlStr = tChannel.getBackgroundUrlString().concat("FooBar");
            tChannel.setBackgroundUrlString(backgroundUrlStr);
            assertEquals(backgroundUrlStr, tChannel.getBackgroundUrlString());
        }

        String bannerUrlStr = tChannel.getBannerUrlString().concat("FooBar");
        tChannel.setBannerUrlString(bannerUrlStr);
        assertEquals(bannerUrlStr, tChannel.getBannerUrlString());

        String profileBannerUrlString = tChannel.getProfileBannerUrlString().concat("FooBar");
        tChannel.setProfileBannerUrlString(profileBannerUrlString);
        assertEquals(profileBannerUrlString, tChannel.getProfileBannerUrlString());

        boolean isPartner = tChannel.isPartner();
        isPartner = (! isPartner);
        tChannel.setPartner(isPartner);
        assertEquals(isPartner, tChannel.isPartner());

        String profileBGColor = tChannel.getProfileBannerBackgroundColor().concat("FooBar");
        tChannel.setProfileBannerBackgroundColor(profileBGColor);
        assertEquals(profileBGColor, tChannel.getProfileBannerBackgroundColor());

        String urlStr = tChannel.getUrlString().concat("FooBar");
        tChannel.setUrlString(urlStr);
        assertEquals(urlStr, tChannel.getUrlString());

        int views = tChannel.getViews();
        tChannel.setViews(++views);
        assertEquals(views, tChannel.getViews());

        int followers = tChannel.getFollowers();
        tChannel.setFollowers(++followers);
        assertEquals(followers, tChannel.getFollowers());

        HashMap<String, String> links = tChannel.getLinks();
        links.put("Foo", "http://bar");
        tChannel.setLinks(links);
        assertEquals(links, tChannel.getLinks());


        // Getting Stream Object via Twitch API
        tChannel.setName("taketv");
        TwitchStream ts = tChannel.getStream();
        if(ts.isOnline()) {
            System.out.println(tChannel.getName() + " is online!");
        } else {
            System.out.println(tChannel.getName() + " is offline!");
        }
        tChannel.setStream(ts);

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
