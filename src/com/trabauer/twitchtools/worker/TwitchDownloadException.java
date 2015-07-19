package com.trabauer.twitchtools.worker;

import com.trabauer.twitchtools.model.twitch.TwitchVideoPart;

/**
 * Created by flo on 7/19/15.
 */
public class TwitchDownloadException extends Exception {
    private TwitchVideoPart videoPart;

    public TwitchDownloadException() {
    }

    public TwitchDownloadException(String message) {
        super(message);
    }

    public TwitchDownloadException(TwitchVideoPart videoPart) {
        this.videoPart = videoPart;
    }

    public TwitchDownloadException(String message, TwitchVideoPart videoPart) {
        super(message);
        this.videoPart = videoPart;
    }

    public TwitchVideoPart getVideoPart() {
        return videoPart;
    }

    public void setVideoPart(TwitchVideoPart videoPart) {
        this.videoPart = videoPart;
    }
}
