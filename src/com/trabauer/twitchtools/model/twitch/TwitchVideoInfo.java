package com.trabauer.twitchtools.model.twitch;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import javax.imageio.ImageIO;
import java.awt.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by Flo on 06.11.2014.
 *
 * Diese Klasse representiert Ein Video Objekt von der Twitch api
 * https://api.twitch.tv/kraken/videos/a582145870
 *
 * Sie enthaelt Informationen zum PastBroadcast allerdings keine Informationen zu den VideoFiles auf den Twitch Servern
 *
 */
public class TwitchVideoInfo extends Observable {
    public static final String APIURL = "https://api.twitch.tv";

    @SerializedName("title")
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

    private Image image;


    private TwitchDownloadInfo dlInfo;
    private boolean isSelectedForDownload;
    private boolean dlInfoNeedsUpdate = false;
    private PropertyChangeSupport pcs;


    private File relatedFileOnDisk;


    public TwitchVideoInfo() {
        this.pcs = new PropertyChangeSupport(this);
        dlInfoNeedsUpdate = false;
        isSelectedForDownload = false;
    }

    public boolean isDownloaded() {
        if(relatedFileOnDisk ==null) {
            return false;
        } else {
            return relatedFileOnDisk.exists();
        }
    }

    public File getRelatedFileOnDisk() {
        return relatedFileOnDisk;
    }

    public void setRelatedFileOnDisk(File relatedFileOnDisk) {
        File oldRelatedFile = this.relatedFileOnDisk;
        this.relatedFileOnDisk = relatedFileOnDisk;
        pcs.firePropertyChange("relatedFile", oldRelatedFile, this.relatedFileOnDisk);
    }





    private class Links {
        public String self;
        public String channel;
    }

    private class Channel {
        public String name;
        @SerializedName("display_name")
        public String displayName;
    }

    public void addPropertyChangeListenern(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }

    public void removeProbertyChangeListenern(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener);
    }



    public void update(URL twitchUrl) throws IOException {
        if(Pattern.matches("http://www.twitch.tv/\\w+/b/\\d+", twitchUrl.toString())) {
            String id = twitchUrl.toString().split("/")[5];
            update("a".concat(id));
        } else if(Pattern.matches("http://www.twitch.tv/\\w+/c/\\d+", twitchUrl.toString())) {
            String id = twitchUrl.toString().split("/")[5];
            update("c".concat(id));
        } else if(Pattern.matches("http://www.twitch.tv/\\w+/v/\\d+", twitchUrl.toString())) {
            String id = twitchUrl.toString().split("/")[5];
            update("v".concat(id));
        }
    }

    public void update(String id) throws IOException {
        URL infoApiUrl = new URL(APIURL + "/kraken/videos/" +id);
        InputStream is = infoApiUrl.openStream();
        InputStreamReader ir = new InputStreamReader(is);
        TwitchVideoInfo tvi = new Gson().fromJson(ir, TwitchVideoInfo.class);
        ir.close();
        is.close();
        update(tvi);
    }

    public void update(TwitchVideoInfo tvi) {
        setTitle(tvi.title);
        setDescription(tvi.description);
        setBroadcastId(tvi.broadcastId);
        setTagList(tvi.tagList);
        setId(tvi.id);
        setRecordedAt(tvi.recordedAt);
        setGame(tvi.game);
        setLength(tvi.getLength());
        setPreview(tvi.preview);
        setUrl(tvi.url);
        setViews(tvi.views);
        setChannelLink(tvi.getChannelLink());
        setSelfLink(tvi.getSelfLink());
        setChannelName(tvi.getChannelName());
        setChannelDisplayname(tvi.getChannelDisplaylName());
        setImage(tvi.image);
        dlInfoNeedsUpdate = true;
        pcs.firePropertyChange("fullUpdate", null, this);
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
        return recordedAtCalendar;
    }

    public String getGame() {
        return game;
    }

    public int getLength() {
        return length;
    }

    public URL getPreviewUrl() throws MalformedURLException {
        return new URL(preview);
    }

    public URL getUrl() throws MalformedURLException {
        return new URL(url);
    }

    public int getViews() {
        return views;
    }

    public String  getSelfLink() {
        return links.self;
    }

    public String getChannelLink () {
        return links.channel;
    }

    public String getChannelDisplaylName() {
        return channel.displayName;
    }

    public String getChannelName() {
        return channel.name;
    }

    public TwitchDownloadInfo getDownloadInfo() throws IOException {
        if(id.matches("^v\\d+")) { //VODs with that kind of Id are stored in the new TwitchVodSystem. The old API requests is empty
            updateDLinfoNewVodSystem();
        } else {
            updateDlInfoOldVodSystem();
        }
        return dlInfo;
    }

    private void updateDlInfoOldVodSystem() throws IOException {
        if(this.dlInfo==null || this.dlInfoNeedsUpdate) {
            if( this.dlInfo==null ) this.dlInfo = new TwitchDownloadInfo();
            dlInfo.update(this.id);
            dlInfoNeedsUpdate = false;
        }
    }


    private void updateDLinfoNewVodSystem() throws IOException {
        if(this.dlInfo==null || this.dlInfoNeedsUpdate) {
            if( this.dlInfo==null ) this.dlInfo = new TwitchDownloadInfo();

            String idNr = this.id.substring(1);
            URL tokenUrl = new URL("https://api.twitch.tv/api/vods/" + idNr + "/access_token");
            InputStream tokenIs = tokenUrl.openStream();
            InputStreamReader tokenIr = new InputStreamReader(tokenIs);
            TwitchVodAccessToken vodAccessToken = new Gson().fromJson(tokenIr, TwitchVodAccessToken.class);
            tokenIr.close();
            tokenIs.close();

            // URLEncoder is used to encode the Token(JSON) to a valid URL
            URL qualityPlaylistUrl = new URL("http://usher.twitch.tv/vod/" + idNr + "?nauth=" + URLEncoder.encode(vodAccessToken.getToken(), "UTF-8") + "&nauthsig=" + vodAccessToken.getSig());
            InputStream qualityPlaylistIs = qualityPlaylistUrl.openStream();
            Scanner qualityPlaylistSc = new Scanner(qualityPlaylistIs);
            while (qualityPlaylistSc.hasNextLine()) {
                String line = qualityPlaylistSc.nextLine();
                System.out.println(line);
                if (!Pattern.matches("^#.*$", line)) { //filter Out comment lines
                    String quality = line.split("/")[7];
                    URL playlistUrl = new URL(line);
                    InputStream playlistIs = playlistUrl.openStream();
                    Scanner playlistSc = new Scanner(playlistIs);
                    int partNumber = 0;
                    while (playlistSc.hasNextLine()) {
                        String partLine = playlistSc.nextLine();
//                        System.out.println(partLine);
                        if (partLine.isEmpty())
                            continue;
                        if (!Pattern.matches("^#.*$", partLine)) { // filter out Comments
                            partLine = line.replace("index-dvr.m3u8", "").concat(partLine);
//                            this.addTwitchVideoPart(quality, new TwitchVideoPart(new URL(partLine), partNumber++));
                            TwitchVideoPart tbp = new TwitchVideoPart(partLine, -1, null , null);
                            if(quality.equals("chunked")) dlInfo.addSourceTwitchBroadcastPart(tbp);
                            else if(quality.equals("high")) dlInfo.addHighTwitchBroadcastPart(tbp);
                            else if(quality.equals("medium")) dlInfo.addMediumTwitchBroadcastPart(tbp);
                            else if(quality.equals("low")) dlInfo.addLowTwitchBroadcastPart(tbp);
                            else if(quality.equals("mobile")) dlInfo.addMobileTwitchBroadcastPart(tbp);
                        }
                    }
                }
            }





            dlInfoNeedsUpdate = false;
        }
    }

    public void setSelectedForDownload(boolean selection) {
        if(isDownloaded()) {
            return;
        }
        boolean oldIsSelected = this.isSelectedForDownload;
        this.isSelectedForDownload = selection;
        this.pcs.firePropertyChange("isSelectedForDownload", oldIsSelected, this.isSelectedForDownload);
    }

    public void setTitle(String title) {
        String oldTitle = this.title;
        this.title = title;
        pcs.firePropertyChange("title", oldTitle, this.title);
    }

    public void setDescription(String description) {
        String oldDescription = this.description;
        this.description = description;
        pcs.firePropertyChange("description", oldDescription, this.description);
    }

    public void setBroadcastId(String broadcastId) {
        String oldBroadcastId = this.broadcastId;
        this.broadcastId = broadcastId;
        pcs.firePropertyChange("broadcastId", oldBroadcastId, this.broadcastId);
    }

    public void setTagList(String tagList) {
        String oldTaglist = this.tagList;
        this.tagList = tagList;
        pcs.firePropertyChange("tagList", oldTaglist, this.tagList);
    }

    public void setId(String id) {
        String oldId = this.id;
        this.id = id;
        pcs.firePropertyChange("id", oldId, this.id);
    }

    public void setRecordedAt(String recordedAt) {
        String oldRecordedAt = this.recordedAt;
        this.recordedAt = recordedAt;
        pcs.firePropertyChange("recordedAt", oldRecordedAt, recordedAt);
    }

    public void setGame(String game) {
        String oldGame = this.game;
        this.game = game;
        pcs.firePropertyChange("game", oldGame, this.game);
    }

    public void setLength(int length) {
        int oldLength = this.length;
        this.length = length;
        pcs.firePropertyChange("length", oldLength, length);
    }

    public void setPreview(String preview) {
        String oldPreview = this.preview;
        this.preview = preview;
        pcs.firePropertyChange("preview", oldPreview, preview);
    }

    public void setUrl(String url) {
        String oldUrl = this.url;
        this.url = url;
        pcs.firePropertyChange("url", oldUrl, this.url);
    }

    public void setChannelName(String channelName) {
        String oldChannelName = this.channel.name;
        this.channel.name = channelName;
        pcs.firePropertyChange("channelName", oldChannelName, this.channel.name);
    }

    public void setChannelDisplayname(String channelDisplayname) {
        String oldChannelDisplayname = this.channel.displayName;
        this.channel.displayName = channelDisplayname;
        pcs.firePropertyChange("channelDisplayname", oldChannelDisplayname, channelDisplayname);
    }

    public void setViews(int views) {
        int oldViews = this.views;
        this.views = views;
        pcs.firePropertyChange("views", oldViews, this.views);
    }

    public void setSelfLink(String selfLink) {
        String oldSelfLink = this.links.self;
        this.links.self = selfLink;
        pcs.firePropertyChange("selfLink", oldSelfLink, this.links.self);
    }

    public void setChannelLink(String channelLink) {
        String oldChannelLink = this.links.channel;
        this.links.channel = channelLink;
        pcs.firePropertyChange("channelLink", oldChannelLink, this.links.channel);
    }

    public void setImage(Image image) {
        Image oldImage = this.image;
        this.image = image;
        pcs.firePropertyChange("image", oldImage, this.image);
    }

    public boolean isSelectedForDownload() {
        return isSelectedForDownload;
    }

    @Override
    public String toString() {
        return "TwitchVideoInfo{" +
                "title='" + title + '\'' +
                '}';
    }

    public Image getPreviewImage() throws MalformedURLException, IOException {
        if (image == null) {
            InputStream is = getPreviewUrl().openStream();
            Image image = ImageIO.read(is);
            return image;
        } else {
            return this.image;
        }
    }

    public HashMap<String, String> getVideoInformation() throws IOException {
        HashMap<String, String> videoInformation = new HashMap<String, String>();

        videoInformation.put("PreviewURL", getPreviewUrl().toString());
        videoInformation.put("URL", getUrl().toString());
        videoInformation.put("ChannelName", getChannelName());
        videoInformation.put("ChannelDisplayName", getChannelDisplaylName());
        videoInformation.put("BroadcastId", getBroadcastId());
        videoInformation.put("TagList", getTagList());
        videoInformation.put("Id", getId());
        videoInformation.put("recordedAt", Integer.toString(getRecordedAt().get(Calendar.YEAR)));
        videoInformation.put("Game", getGame());
        videoInformation.put("BestAvailableQuality", getDownloadInfo().getBestAvailableQuality());
        videoInformation.put("Title", getTitle());
        videoInformation.put("Description", getDescription());
        videoInformation.put("Length", Integer.toString(getLength()));

        return videoInformation;
    }


    public LinkedHashMap<String, String> getStreamInformation() throws IOException {
        //LinkedHashMap<String, String> streamInfo = super.getStreamInformation();
        LinkedHashMap<String, String> streamInformation = new LinkedHashMap<String, String>();
        streamInformation.put("Title", getTitle());
        streamInformation.put("Description", getDescription());
        streamInformation.put("Channel", getChannelName());
        streamInformation.put("ChannelDisplayName", getChannelDisplaylName());
        //streamInformation.put("RecordedAt", getRecordedAt().getDisplayName(Calendar.YEAR, Calendar.SHORT, Locale.ENGLISH));
        streamInformation.put("Game", getGame());
        streamInformation.put("BestQuality", getDownloadInfo().getBestAvailableQuality());
        streamInformation.put("broadcastId", getBroadcastId());
        streamInformation.put("tagList", getTagList());
        streamInformation.put("id", getId());
        streamInformation.put("previewImageURL", getPreviewUrl().toString());
        streamInformation.put("url", getUrl().toString());
        streamInformation.put("views", String.valueOf(views));
        streamInformation.put("channelLink", getChannelLink());
        streamInformation.put("selfLink", getChannelLink());

        if(getRecordedAt()!= null) {
            String timestamp = "".format("%tF_%tT", getRecordedAt(), getRecordedAt());
            streamInformation.put("recorded-at", timestamp);
            streamInformation.put("date", new String().format("%tF", getRecordedAt()));
            streamInformation.put("time", new String().format("%tT", getRecordedAt()));
        }

        return streamInformation;
    }




}
