package com.trabauer.twitchtools.model;

import java.util.*;

/**
 * Created by flo on 03.11.14.
 */
public class Video extends Observable {
    /**
     * Ein Video Objekt enthält Informationen zu einem Video
     */

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

    private int length;

    private HashMap<String, ArrayList<VideoPart>> videoParts;

    private LinkedHashMap<String, String> streamInformation;


    public Video() {
        this("None");
    }


    public Video(String title) {
        this(title, null, -1);

    }

    public Video(String title, String description,int length) {
        super();
        this.title = title;
        this.description = description;
        this.length = length;
        videoParts = new HashMap<String, ArrayList<VideoPart>>();
        streamInformation = new LinkedHashMap<String, String>();
    }

    @Override
    public String toString() {
        return "Video{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", videoParts=" + videoParts +
                '}';
    }

    public HashMap<String, ArrayList<VideoPart>> getVideoParts() {
        return videoParts;
    }

    public ArrayList<VideoPart> getVideoParts(String quality) {
        return videoParts.get(quality);
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

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public LinkedHashMap<String, String> getStreamInformation() {
        streamInformation = new LinkedHashMap<String, String>();
        streamInformation.put("Title", getTitle());
        streamInformation.put("Description", getDescription());
        return streamInformation;
    }

    protected void streamInformationPut(String key, Object value) {
        if(value != null) {
            streamInformation.put(key, value.toString());
        }
    }

}
