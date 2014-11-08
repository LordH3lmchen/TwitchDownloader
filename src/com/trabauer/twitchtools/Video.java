package com.trabauer.twitchtools;

import java.net.URL;
import java.util.*;

/**
 * Created by flo on 03.11.14.
 */
public class Video {

    /*
    {
    "title":"Against the Odds Final Day",
    "description":null,
    "broadcast_id":11514936512,
    "tag_list":"",
    "_id":"a582145870",
    "recordedAt":"2014-10-26T13:00:28Z",
    "game":"StarCraft II: Heart of the Swarm",
    "length":19470,
    "preview":"http://static-cdn.jtvnw.net/jtv.thumbs/archive-582145870-320x240.jpg",
    "url":"http://www.twitch.tv/taketv/b/582145870",
    "views":522,
    "_links":{
                "self":"https://api.twitch.tv/kraken/videos/a582145870",
                "channel":"https://api.twitch.tv/kraken/channels/taketv"
                },
    "channel":{
                "name":"taketv",
                "display_name":"TaKeTV"
               }
    }
     */

    private String title;
    private String description;
    private String broadcastId;
    private String tagList;
    private String id;
    private Calendar recordedAt;
    private String game;
    private int length; // Vermutlich int
    private URL preview; //Image
    private URL url; //URL To TwitchPage
    private int views;
    private URL channelLink;
    private URL selfLink;
    private String channelName;
    private String channelDisplayName;

    private HashMap<String, ArrayList<VideoPart>> videoParts;

    public Video(String title) {
        this(title, null, null, null, null, null, null, -1, null, null, null, null);

    }

    public Video(String title, String description, String broadcastId, String tagList, String id, Calendar recordedAt,
                 String game, int length, URL preview, URL url, String channelName, String channelDisplayName) {
        this.title = title;
        this.description = description;
        this.broadcastId = broadcastId;
        this.tagList = tagList;
        this.id = id;
        this.recordedAt = recordedAt;
        this.game = game;
        this.length = length;
        this.preview = preview;
        this.url = url;
        this.channelName = channelName;
        this.channelDisplayName = channelDisplayName;
        videoParts = new HashMap<String, ArrayList<VideoPart>>();
    }

    @Override
    public String toString() {
        return "Video{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", broadcastId='" + broadcastId + '\'' +
                ", tagList='" + tagList + '\'' +
                ", id='" + id + '\'' +
                ", recordedAt=" + recordedAt +
                ", game='" + game + '\'' +
                ", length=" + length +
                ", preview=" + preview +
                ", url=" + url +
                ", views=" + views +
                ", channelLink=" + channelLink +
                ", selfLink=" + selfLink +
                ", channelName='" + channelName + '\'' +
                ", channelDisplayName='" + channelDisplayName + '\'' +
                ", videoParts=" + videoParts +
                '}';
    }

    public HashMap<String, ArrayList<VideoPart>> getVideoParts() {
        return videoParts;
    }

    public void setDownloadURLs(String quality, ArrayList<VideoPart> urls) {
        this.videoParts.put(quality, urls);
    }

    public void addVideoPart(String quality, VideoPart videoPart) {
        if( videoParts.containsKey(quality) ) {
            videoParts.get(quality).add(videoPart);
        } else {
            videoParts.put(quality, new ArrayList<VideoPart>());
            videoParts.get(quality).add(videoPart);
        }
    }

    public LinkedHashSet<String> getAvailableQualities() {
        return new LinkedHashSet<String>(videoParts.keySet());
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
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
}
