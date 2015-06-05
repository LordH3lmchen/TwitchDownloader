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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

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

//    @SerializedName("_total")
//    private int size;
    @SerializedName("_links")
    private HashMap<String, String> links;



    @SerializedName("videos")
    ArrayList<TwitchVideoInfo> twitchVideoInfos;
//    TwitchVideoInfo[] twitchVideoInfos;

    /**
     * Creates a empty TwitchVideoInfoList
     */
    public TwitchVideoInfoList() {
        pcs = new PropertyChangeSupport(this);
        twitchVideoInfos = new ArrayList<TwitchVideoInfo>();
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
                "size=" + getSize() +
                ", links=" + links +
                //", twitchVideoInfos=" + Arrays.toString(twitchVideoInfos) +
                '}';
    }


    /**
     * This TwitchVideoInfoList updates all its data from the source-TwitchVideoInfoList
     * It's like a Copy Constructor but the Object remains still the same. Listeners getting informed of the update.
     *
     * This Method fires a PropertyChangeEvent with the PropertyName="contentUpdate" to inform the listeners
     *
     * @param source
     */
    public void update(TwitchVideoInfoList source, List<TwitchVideoInfo> cachedTVIs) {
        ArrayList<TwitchVideoInfo> oldInfos = this.twitchVideoInfos;
        this.setLinks(source.getLinks());
        this.setTwitchVideoInfos(source.getTwitchVideoInfos());
        if(cachedTVIs!=null) {
            for (TwitchVideoInfo cachedTVI : cachedTVIs) {
                int index = this.twitchVideoInfos.indexOf(cachedTVI);
                if(index>=0) { //replace it with the cached instance of the Object
                    this.twitchVideoInfos.remove(index);
                    this.twitchVideoInfos.add(index, cachedTVI);
                    try {
                        cachedTVI.loadPreviewImage();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        this.pcs.firePropertyChange("contentUpdate", oldInfos, twitchVideoInfos);
    }

    /**
     * Updates the <code>TwitchVideoInfoList</code> with the result of the given <code>URL</code>
     * @param apiUrl
     * @throws IOException
     */
    private void update(URL apiUrl , List<TwitchVideoInfo> cachedTVIs) throws IOException {
        InputStream is = apiUrl.openStream();
        InputStreamReader ir = new InputStreamReader(is);
        TwitchVideoInfoList twitchVideoInfoList = new Gson().fromJson(ir, TwitchVideoInfoList.class); //deserialize
        ir.close();
        is.close();
        update(twitchVideoInfoList, cachedTVIs);
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
     * @param cachedTVIs
     * @throws MalformedURLException
     */
    public void update(String channelName, boolean broadcasts, int limit, int offset, List<TwitchVideoInfo> cachedTVIs) throws IOException {
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
        update(new URL(urlStr), cachedTVIs);
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
    public void update(String channelName, boolean broadcasts) throws IOException {
        update(channelName, broadcasts, -1, -1, null);
    }

    public void update(String channelName) throws IOException {
        update(channelName, false, -1, -1, null);
    }


    public URL getSelfUrl() throws MalformedURLException {
        return new URL(links.get("self"));
    }

    public URL getNextUrl() throws MalformedURLException {
        return new URL(links.get("next"));
    }

    public String getNextUrlString() {
        return this.links.get("next");
    }

    public String getSelfUrlString() {
        return this.links.get("self");
    }


    public ArrayList<TwitchVideoInfo> getTwitchVideoInfos() {
        return twitchVideoInfos;
    }

    public int getSize() {
        return twitchVideoInfos.size();
    }

    public void setNextUrl(String nextUrl) {
        if(this.links==null) this.links = new HashMap<>();
        String oldNext = links.get("next");
        links.put("next", nextUrl);
        this.pcs.firePropertyChange("nextUrl", oldNext, links.get("next"));
    }

    public void setSelfUrl(String selfUrl) {
        if(this.links==null) this.links = new HashMap<>();
        String oldSelf = links.get("self");
        this.links.put("self", selfUrl);
        this.pcs.firePropertyChange("selfUrl", oldSelf, this.links.get("self"));
    }

    private void setTwitchVideoInfos(ArrayList<TwitchVideoInfo> twitchVideoInfos) {
        ArrayList<TwitchVideoInfo> oldTwitchVideoInfos = this.twitchVideoInfos;
        this.twitchVideoInfos = twitchVideoInfos;
        this.pcs.firePropertyChange("twitchVideoInfos", oldTwitchVideoInfos, this.twitchVideoInfos);
    }

    public HashMap<String, String> getLinks() {
        return links;
    }

    public void setLinks(HashMap<String, String> links) {
        this.links = links;
    }

    public void addTwitchVideoInfo(TwitchVideoInfo tvi) {
        if(this.twitchVideoInfos==null) this.twitchVideoInfos = new ArrayList<TwitchVideoInfo>();
        this.twitchVideoInfos.add(tvi);
        this.pcs.firePropertyChange("twitchVideoInfoAdded", null, tvi);
    }

    public void loadMore(List<TwitchVideoInfo> cachedTVIs) {
        TwitchVideoInfoList tempVideoInfoList = new TwitchVideoInfoList();
        try {
            tempVideoInfoList.update(getNextUrl(), cachedTVIs);
        } catch (IOException e) {
            e.printStackTrace();
        }
        setNextUrl(tempVideoInfoList.getNextUrlString());
        for(TwitchVideoInfo videoInfo: tempVideoInfoList.getTwitchVideoInfos()) {
            addTwitchVideoInfo(videoInfo);
        }
        this.pcs.firePropertyChange("moreLoaded", null, twitchVideoInfos );

    }

    public TwitchVideoInfoList getMostRecent(int ageInDays) {
        TwitchVideoInfoList mostRecentList = new TwitchVideoInfoList();

        for(TwitchVideoInfo tvi: twitchVideoInfos) {
            int age = 0 - ageInDays;
            Calendar dateLimit = Calendar.getInstance();
            dateLimit.add(Calendar.DATE, age);
            if(tvi.getRecordedAt().compareTo(dateLimit)>0) {
                mostRecentList.addTwitchVideoInfo(tvi);
            }
        }
        return mostRecentList;
    }

    public void selectMostRecentForDownload(int ageInDays) {
        ArrayList<TwitchVideoInfo> mostRecentList = this.getMostRecent(ageInDays).twitchVideoInfos;
        for(TwitchVideoInfo tvi: this.twitchVideoInfos) {
            if(tvi.getState().equals(TwitchVideoInfo.State.SELECTED_FOR_DOWNLOAD)){ //Reset old selection
                tvi.setState(TwitchVideoInfo.State.INITIAL);
            }
        }
        for(TwitchVideoInfo tvi: mostRecentList) { //Select most recent videos
            if(tvi.getState().equals(TwitchVideoInfo.State.INITIAL)) {
                tvi.setState(TwitchVideoInfo.State.SELECTED_FOR_DOWNLOAD);
            }
        }
    }

    /**
     * Takes AllSelecte
     * @return
     */
    public ArrayList<TwitchVideoInfo> getAllSelected() {
        ArrayList<TwitchVideoInfo> selectedVideos = new ArrayList<TwitchVideoInfo>();
        for(TwitchVideoInfo tvi: twitchVideoInfos) {
            if(tvi.getState().equals(TwitchVideoInfo.State.SELECTED_FOR_DOWNLOAD)) {
                selectedVideos.add(tvi);
            }
        }
        return selectedVideos;
    }

    public TwitchVideoInfo get(int index) {
        return twitchVideoInfos.get(index);
    }

}


