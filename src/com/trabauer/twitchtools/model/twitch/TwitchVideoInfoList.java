package com.trabauer.twitchtools.model.twitch;

import com.google.common.base.Joiner;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class represents a Search Result from the folowing api
 * https://api.twitch.tv/kraken/channels/taketv/videos
 *
 * Valid Arguments are
 * <ul>
 * <li>broadcasts=true      (or false)</li>
 * <li>limit=10             (0-100)</li>
 * <li>offset=10</li>
 * </ul>
 *
 *
 * https://api.twitch.tv/kraken/channels/taketv/videos?broadcasts=true&limit=20&offset=20
 * https://api.twitch.tv/kraken/channels/taketv/videos?broadcasts=true&limit=20
 *
 * Gets the past broadcats 1-20.
 *
 * You are abel to update a TwitchVideoInfo list with its  update methods. S
 * So you don't have to worry about the TwitchAPI
 *
 *
 *
 * Created by Flo on 20.01.2015.
 */
public class TwitchVideoInfoList {

    private final PropertyChangeSupport pcs;

    @SerializedName("_total")
    private int size;
    @SerializedName("_links")
    private Links links;

    class Links {
        public String self;
        public String next;

        public Links() {
            this.self = null;
            this.next = null;
        }

        @Override
        public String toString() {
            return "Links{" +
                    "self='" + self + '\'' +
                    ", next='" + next + '\'' +
                    '}';
        }
    }


    @SerializedName("videos")
    ArrayList<TwitchVideoInfo> twitchVideoInfos;
//    TwitchVideoInfo[] twitchVideoInfos;

    /**
     * Creates a empty TwitchVideoInfoList
     */
    public TwitchVideoInfoList() {
        pcs = new PropertyChangeSupport(this);
    }


    /**
     * Adds a PropertyChangeListener to the TwitchVideoInfoList
     *
     * The following methods are able to fire a <code>PropertyChangeEvent</code>
     *
     *
     * <ul>
     *     <li><code>setSize(int size)</code></li>
     *     <li><code>setNextUrl(String url)</code></li>
     *     <li><code>setSelfUrl(String url)</code></li>
     *     <li><code>setTwitchVideoInfos(ArrayList<TwitchVideoInfo></></code></li>
     *     <li><code>loadMore()</code></li>
     * </ul>
     *
     *
     * @param listener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    /**
     * Remove the given PropertyChangeListener from the TwitchVideoInfoList
     * @param listener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }


    @Override
    public String toString() {
        return "TwitchVideoInfoList{" +
                "size=" + size +
                ", links=" + links +
                //", twitchVideoInfos=" + Arrays.toString(twitchVideoInfos) +
                '}';
    }


    /**
     * This TwitchVideoInfoList updates all its data from the source-TwitchVideoInfoList
     * It's like a reverse Copy Constructor.
     * @param source
     */
    public void update(TwitchVideoInfoList source) {
        this.setLinks(source.getLinks());
        this.setSize(source.getSize());
        this.setTwitchVideoInfos(source.getTwitchVideoInfos());
    }

    /**
     * Updates the <code>TwitchVideoInfoList</code> with the result of the given <code>URL</code>
     * @param apiUrl
     * @throws IOException
     */
    private void update(URL apiUrl) throws IOException {
        InputStream is = apiUrl.openStream();
        InputStreamReader ir = new InputStreamReader(is);
        TwitchVideoInfoList twitchVideoInfoList = new Gson().fromJson(ir, TwitchVideoInfoList.class); //deserialize
        ir.close();
        is.close();
        update(twitchVideoInfoList);
    }


    /**
     *
     *
     * Updates this list of videos ordered by time of creation, starting with the most recent from :channel.
     *
     * It uese the Folowing API
     * <a href="https://github.com/justintv/Twitch-API/blob/master/v3_resources/videos.md#get-channelschannelvideos">api.twitch.tv/kraken/channels/:channel/videos</a>
     *
     *
     * @param channelName   Channels's name
     * @param broadcasts    Returns only broadcasts when true. Otherwise only highlights are returned. Default is false.
     * @param limit         Maximum number of objects in array. Default is 10. Maximum is 100. If limit<=0 the Twitch API default is used
     * @param offset        Object offset for pagination. Default is 0.
     * @throws MalformedURLException
     */
    public void update(String channelName, boolean broadcasts, int limit, int offset) throws MalformedURLException {
        String urlStr = new String().format("https://api.twitch.tv/kraken/channels/%s/videos", channelName);
        ArrayList<String> parameters = new ArrayList<String>();
        if(broadcasts) parameters.add("broadcasts=true");
        if(limit>0) parameters.add(String.format("limit=%d", limit));
        if(offset>=0) parameters.add(String.format("offset=%d", offset));
        if( ! parameters.isEmpty()) {
            urlStr = urlStr.concat("?");
            String joinedParameters = Joiner.on('&').join(parameters);
            urlStr = urlStr.concat(joinedParameters);
        }
        try {
            update(new URL(urlStr));
        } catch (IOException e) {
            //shouldn't happen
            e.printStackTrace();
        }


    }

    /**
     * Works like <code>update(String channelName, boolean broadcasts, int limit, int offset)</code>
     *
     * The defualt values from <a href="https://github.com/justintv/Twitch-API/blob/master/v3_resources/videos.md#get-channelschannelvideos">Twitch</a>
     * are used for offset and limit
     *
     * @param channelName   Channels's name
     * @param broadcasts    Returns only broadcasts when true. Otherwise only highlights are returned. Default is false.
     * @throws MalformedURLException
     */
    public void update(String channelName, boolean broadcasts) throws MalformedURLException {
        update(channelName, broadcasts, -1, -1);
    }

    public void update(String channelName) throws MalformedURLException {
        update(channelName, false, -1, -1);
    }


    public URL getSelfUrl() throws MalformedURLException {
        return new URL(links.self);
    }

    public URL getNextUrl() throws MalformedURLException {
        return new URL(links.next);
    }

    public String getNextUrlString() {
        return this.links.next;
    }

    public String getSelfUrlString() {
        return this.links.self;
    }


    public ArrayList<TwitchVideoInfo> getTwitchVideoInfos() {
        return twitchVideoInfos;
    }


    public void setSize(int size) {
        int oldSize = this.size;
        this.size = size;
        this.pcs.firePropertyChange("size", oldSize, size);
    }

    public int getSize() {
        return size;
    }

    public void setNextUrl(String nextUrl) {
        if(this.links==null) this.links = new Links();
        String oldNext;
        if(links.next==null) oldNext = null;
        else oldNext = this.links.next;
        this.pcs.firePropertyChange("nextUrl", oldNext, this.links.next=nextUrl);
    }

    public void setSelfUrl(String selfUrl) {
        if(this.links==null) this.links = new Links();
        String oldSelf;
        if(this.links.self==null) oldSelf=null;
        else oldSelf=this.links.self;
        this.links.self = selfUrl;
        this.pcs.firePropertyChange("selfUrl", oldSelf, this.links.self);
    }

    public void setTwitchVideoInfos(ArrayList<TwitchVideoInfo> twitchVideoInfos) {
        ArrayList<TwitchVideoInfo> oldTwitchVideoInfos = this.twitchVideoInfos;
        this.twitchVideoInfos = twitchVideoInfos;
        this.pcs.firePropertyChange("twitchVideoInfos", oldTwitchVideoInfos, this.twitchVideoInfos);
    }

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        if(this.links == null) {
            this.links = new Links();
        }
        setNextUrl(links.next);
        setSelfUrl(links.self);
    }

    public void loadMore() {
        TwitchVideoInfoList tempVideoInfoList = new TwitchVideoInfoList();
        try {
            tempVideoInfoList.update(getNextUrl());
        } catch (IOException e) {
            e.printStackTrace();
        }
        setNextUrl(tempVideoInfoList.getNextUrlString());
        setSize(size+tempVideoInfoList.getSize());
        for(TwitchVideoInfo videoInfo: tempVideoInfoList.getTwitchVideoInfos()) {
            this.twitchVideoInfos.add(videoInfo);
            this.pcs.firePropertyChange("twitchVideoInfoAdded", null, videoInfo);
        }

    }

}


