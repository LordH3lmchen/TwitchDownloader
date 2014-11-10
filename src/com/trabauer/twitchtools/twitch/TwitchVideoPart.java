package com.trabauer.twitchtools.twitch;

import com.trabauer.twitchtools.VideoPart;

import java.net.URL;

/**
 * Created by Flo on 09.11.2014.
 */
public class TwitchVideoPart extends VideoPart{
    private int length;
    private URL vodCountUrl;
    private String upkeep;

    public TwitchVideoPart(URL url, int length, URL vodCountUrl, String upkeep) {
        super(url);
        this.length = length;
        this.vodCountUrl = vodCountUrl;
        this.upkeep = upkeep;
    }


    public TwitchVideoPart(URL url) {
        this(url, -1, null, null);
    }

    public  TwitchVideoPart(URL url, int length) {
        this(url, length, null, null);
    }

    public int getLength() {
        return length;
    }

    public URL getVodCountUrl() {
        return vodCountUrl;
    }

    public String getUpkeep() {
        return upkeep;
    }
}
