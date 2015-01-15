package com.trabauer.twitchtools.model.twitch;

import com.trabauer.twitchtools.model.VideoPart;

import java.net.URL;

/**
 * Created by Flo on 09.11.2014.
 */
public class TwitchVideoPart extends VideoPart{
    private int length;
    private URL vodCountUrl;
    private String upkeep;
    private int partNumber;

    public TwitchVideoPart(URL url, int partNumber, int length, URL vodCountUrl, String upkeep) {
        super(url, partNumber);
        this.length = length;
        this.vodCountUrl = vodCountUrl;
        this.upkeep = upkeep;
        this.partNumber = partNumber;
    }

    public TwitchVideoPart(URL url, int length, URL vodCountUrl, String upkeep) {
        this(url, -1, length, vodCountUrl, upkeep);
    }

    public TwitchVideoPart(URL url) {
        this(url, -1, -1, null, null);
    }

    public TwitchVideoPart(URL url, int partNumber) {
        this(url, partNumber, -1, null, null);
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

    @Override
    public int getPartNumber() {
        return partNumber;
    }
}
