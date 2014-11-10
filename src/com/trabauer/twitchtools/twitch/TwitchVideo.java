package com.trabauer.twitchtools.twitch;

import com.trabauer.twitchtools.Video;
import com.trabauer.twitchtools.VideoPart;

import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by Flo on 09.11.2014.
 */
public class TwitchVideo  extends Video {
    private String broadcastId;
    private String tagList;
    private String id;
    private Calendar recordedAt;
    private String game;
    private URL preview; //Image
    private URL url; //URL To TwitchPage
    private int views;
    private URL channelLink;
    private URL selfLink;
    private String channelName;
    private String channelDisplayName;


    public TwitchVideo(String title, String description, String broadcastId, String tagList, String id, Calendar recordedAt,
                 String game, int length, URL preview, URL url, String channelName, String channelDisplayName) {
        super(title, description, length);
        this.broadcastId = broadcastId;
        this.tagList = tagList;
        this.id = id;
        this.recordedAt = recordedAt;
        this.game = game;
        this.preview = preview;
        this.url = url;
        this.channelName = channelName;
        this.channelDisplayName = channelDisplayName;
    }


    public URL getPreview() {
        return preview;
    }

    public void setPreview(URL preview) {
        this.preview = preview;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getChannelDisplayName() {
        return channelDisplayName;
    }

    public void setChannelDisplayName(String channelDisplayName) {
        this.channelDisplayName = channelDisplayName;
    }

    public String getBroadcastId() {
        return broadcastId;
    }

    public void setBroadcastId(String broadcastId) {
        this.broadcastId = broadcastId;
    }

    public String getTagList() {
        return tagList;
    }

    public void setTagList(String tagList) {
        this.tagList = tagList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Calendar getRecordedAt() {
        return recordedAt;
    }

    public void setRecordedAt(Calendar recordedAt) {
        this.recordedAt = recordedAt;
    }

    public String getGame() {
        return game;
    }

    public void setGame(String game) {
        this.game = game;
    }

    public String getBestAvailableQuality() {
        ArrayList<String> orderedQualitiesDescending = new ArrayList<String>();
        orderedQualitiesDescending.add("source");
        orderedQualitiesDescending.add("high");
        orderedQualitiesDescending.add("mid");
        orderedQualitiesDescending.add("low");
        orderedQualitiesDescending.add("mobile");

        for(String quality: orderedQualitiesDescending) {
            if(getAvailableQualities().contains(quality))
                return quality;
        }
        return null;
    }
}
