package com.trabauer.twitchtools;

import java.net.URL;

/**
 * Created by Flo on 06.11.2014.
 */
public class VideoPart {
    private URL url;
    private int length;
    private URL vodCountUrl;
    private String upkeep;

    public VideoPart(URL url, int length, URL vodCountUrl, String upkeep) {
        this.url = url;
        this.length = length;
        this.vodCountUrl = vodCountUrl;
        this.upkeep = upkeep;
    }

    @Override
    public String toString() {
        return "VideoPart{" +
                "url=" + url +
                ", length=" + length +
                ", vodCountUrl=" + vodCountUrl +
                ", upkeep='" + upkeep + '\'' +
                '}';
    }

    public VideoPart(URL url) {
        this(url, -1, null, null);
    }

    public  VideoPart(URL url, int length) {
        this(url, length, null, null);
    }

    public URL getUrl() {
        return url;
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
