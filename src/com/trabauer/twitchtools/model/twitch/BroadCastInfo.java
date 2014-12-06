package com.trabauer.twitchtools.model.twitch;

import com.google.gson.annotations.SerializedName;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Flo on 06.11.2014.
 *
 * Diese Klasse representiert Ein Video Objekt von der Twitch api
 * https://api.twitch.tv/kraken/videos/a582145870
 *
 * Sie enthaelt Informationen zum PastBroadcast allerdings keine Informationen zu den VideoFiles auf den Twitch Servern
 *
 */
public class BroadCastInfo {
    private String title;
    private String description;
    @SerializedName("broadcast_id")
    private String broadcastId;
    @SerializedName("tag_list")
    private String tagList;
    @SerializedName("_id")
    private String id;
    @SerializedName("recorded_at")
    private String recordedAt;
    private String game;
    private int    length;
    private String preview;
    private String url;
    private int    views;

    @SerializedName("_links")
    private Links links;

    @SerializedName("channel")
    private Channel channel;

    public BroadCastInfo() {
    }

    class Links {
        public String self;
        public String channel;
    }

    class Channel {
        public String name;
        @SerializedName("display_name")
        public String displayName;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getBroadcastId() {
        return broadcastId;
    }

    public String getTagList() {
        return tagList;
    }

    public String getId() {
        return id;
    }

    public Calendar getRecordedAt() {
        String date = recordedAt.split("T")[0];
        String time = recordedAt.split("T")[1];
        int year = new Integer(date.split("-")[0]);
        int month = new Integer(date.split("-")[1]);
        int day = new Integer(date.split("-")[2]);
        int hourOfDay = new Integer(time.split(":")[0]);
        int minute = new Integer(time.split(":")[1]);
        int secound = new Integer(time.split(":")[2].substring(0,2));

        Calendar recordedAtCalendar = GregorianCalendar.getInstance();
        recordedAtCalendar.set(year, month-1, day, hourOfDay, minute, secound);
        System.out.printf("reacordedAt %tF %tT", recordedAtCalendar, recordedAtCalendar);
        return recordedAtCalendar;
    }

    public String getGame() {
        return game;
    }

    public int getLength() {
        return length;
    }

    public URL getPreview() throws MalformedURLException {
        return new URL(preview);
    }

    public URL getUrl() throws MalformedURLException {
        return new URL(url);
    }

    public int getViews() {
        return views;
    }

    public Links getLinks() {
        return links;
    }

    public Channel getChannel() {
        return channel;
    }
}
