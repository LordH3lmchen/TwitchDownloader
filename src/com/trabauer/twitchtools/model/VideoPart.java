package com.trabauer.twitchtools.model;

import java.net.URL;

/**
 * Created by Flo on 06.11.2014.
 */
public class VideoPart {
    private URL url;
    private int partNumber;


    public VideoPart(URL url, int partNumber) {
        this.url = url;
        this.partNumber = partNumber;
    }

    @Override
    public String toString() {
        return "VideoPart{" +
                "url=" + url +
                '}';
    }

    public URL getUrl() {
        return url;
    }

    public int getPartNumber() {
        return partNumber;
    }
}
