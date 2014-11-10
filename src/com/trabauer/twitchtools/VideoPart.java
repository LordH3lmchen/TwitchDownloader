package com.trabauer.twitchtools;

import java.net.URL;

/**
 * Created by Flo on 06.11.2014.
 */
public class VideoPart {
    private URL url;


    public VideoPart(URL url) {
        this.url = url;
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
}
