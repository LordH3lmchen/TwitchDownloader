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
import java.util.regex.Matcher;
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

    public enum State {
        INITIAL,
        SELECTED_FOR_DOWNLOAD,
        QUEUED_FOR_DOWNLOAD,
        DOWNLOADING,
        DOWNLOADED,
        SELECTED_FOR_CONVERT,
        QUEUED_FOR_CONVERT,
        CONVERTING,
        CONVERTED,
        LIVE // this broadcast is currently live
    }


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
    private HashMap<String, String> links;

    @SerializedName("channel")
    private TwitchChannel channel;

    private Image image;


    private TwitchDownloadInfo dlInfo;
    private boolean dlInfoNeedsUpdate = false;
    private TwitchVideoInfo.State state;
    private HashMap<String, File> relatedFiles;


    protected PropertyChangeSupport pcs;


    public TwitchVideoInfo() {
        this.pcs = new PropertyChangeSupport(this);
        dlInfoNeedsUpdate = false;
        this.state = State.INITIAL;
        this.relatedFiles = new HashMap<>();
    }

    public static TwitchVideoInfo getTwitchVideoInfo(String id) throws IOException {
        URL infoApiUrl = new URL(APIURL + "/kraken/videos/" +id);
        InputStream is = infoApiUrl.openStream();
        InputStreamReader ir = new InputStreamReader(is);
        TwitchVideoInfo tvi = new Gson().fromJson(ir, TwitchVideoInfo.class);
        ir.close();
        is.close();
        return tvi;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        TwitchVideoInfo.State oldState = this.state;
        this.state = state;
        pcs.firePropertyChange("state", oldState, this.state);
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TwitchVideoInfo that = (TwitchVideoInfo) o;

        if (length != that.length) return false;
        if (views != that.views) return false;
        if (broadcastId != null ? !broadcastId.equals(that.broadcastId) : that.broadcastId != null) return false;
        if (channel != null ? !channel.equals(that.channel) : that.channel != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (game != null ? !game.equals(that.game) : that.game != null) return false;
        if (!id.equals(that.id)) return false;
        if (links != null ? !links.equals(that.links) : that.links != null) return false;
        if (preview != null ? !preview.equals(that.preview) : that.preview != null) return false;
        if (recordedAt != null ? !recordedAt.equals(that.recordedAt) : that.recordedAt != null) return false;
        if (!title.equals(that.title)) return false;
        if (url != null ? !url.equals(that.url) : that.url != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = title.hashCode();
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (broadcastId != null ? broadcastId.hashCode() : 0);
        result = 31 * result + id.hashCode();
        result = 31 * result + (recordedAt != null ? recordedAt.hashCode() : 0);
        result = 31 * result + (game != null ? game.hashCode() : 0);
        result = 31 * result + length;
        result = 31 * result + (preview != null ? preview.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + views;
        result = 31 * result + (links != null ? links.hashCode() : 0);
        result = 31 * result + (channel != null ? channel.hashCode() : 0);
        return result;
    }

    public boolean relatedFileExists() {
        if(relatedFiles.isEmpty()) {
            return false;
        } else {
            if(relatedFiles.containsKey("default")) {
                return relatedFiles.get("default").exists();
            } else return false;
        }
    }

    public File getMainRelatedFileOnDisk() {
        if(relatedFiles.containsKey("default")) {
            return relatedFiles.get("default");
        } else {
            return null;
        }
    }

    public void setMainRelatedFileOnDisk(File relatedFileOnDisk) {
        File oldRelatedFile;
        if(relatedFiles.containsKey("default")) oldRelatedFile = relatedFiles.get("default");
        else oldRelatedFile = null;
        relatedFiles.put("default", relatedFileOnDisk);
        pcs.firePropertyChange("relatedFile", oldRelatedFile, relatedFiles.get("default"));
    }

    public ArrayList<File> getRelatedFiles() {
        return new ArrayList<File>(relatedFiles.values());
    }

    public void putRelatedFile(String key, File value) {
        relatedFiles.put(key, value);
    }

    public void removeRelatedFile(String key) {
        relatedFiles.remove(key);
    }

    public void deleteAllRelatedFiles() {
        for(File file: relatedFiles.values()) {
            if(file.exists()&& file.canWrite()) {
                file.delete();
            }
        }
        relatedFiles.clear();
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
        update(getTwitchVideoInfo(id));
    }

    public void update(TwitchVideoInfo tvi) {
        if(this.channel == null) this.channel = new TwitchChannel();
        if(this.links == null) this.links = new HashMap<>();
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
        return links.get("self");
    }


    public String getChannelLink () {
        return links.get("channel");
    }

    public String getChannelDisplaylName() {
        return channel.getDisplayName();
    }

    public String getChannelName() {
        return channel.getName();
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


    /**
     * The new VOD System uses m3u PLaylist instead. The REST-API returns empty lists.
     *
     * This Mehtod fetches the relevant Information and stores them in the same way as the old-VOD-System
     *
     * @throws IOException
     */
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
//                System.out.println(line);
                if (!Pattern.matches("^#.*$", line)) { //filter Out comment lines
                    String quality = line.split("/")[7];
                    URL playlistUrl = new URL(line);
                    String m3uFilename = new File(playlistUrl.getFile()).getName();
                    InputStream playlistIs = playlistUrl.openStream();
                    Scanner playlistSc = new Scanner(playlistIs);
                    Pattern partFileNameStringPattern = Pattern.compile("^(index-\\d{10}-\\w{4}\\.ts)\\?start_offset=(\\d+)&end_offset=(\\d+)$");
                    String previousPartFileName = null;
                    int startOffset = 0;
                    int endOffset = 0;
// TODO Test the new playlist parsing.
                    while (playlistSc.hasNextLine()) {
                        String partLine = playlistSc.nextLine();
//                        System.out.println(partLine);
                        if (partLine.isEmpty())
                            continue;
                        Matcher m = partFileNameStringPattern.matcher(partLine);
                        if (m.matches()) {
                            if(previousPartFileName == null) { //First File
                                previousPartFileName = m.group(1);
                                endOffset = Integer.parseInt(m.group(3));
                                startOffset = Integer.parseInt(m.group(2));
                                continue;
                            }
                            if(m.group(1).equals(previousPartFileName)) {
                                if(endOffset < Integer.parseInt(m.group(3)))
                                    endOffset = Integer.parseInt(m.group(3));
                                if(startOffset > Integer.parseInt(m.group(2)))
                                    startOffset = Integer.parseInt(m.group(3));
                                continue;
                            } else { // currentPartFileName != previousPartFileName
                                //insert previous partFileName with improved offset
                                String partURL = String.format("%s%s?start_offset=%d&end_offset=%d",
                                        line.replace(m3uFilename, ""),
                                        previousPartFileName,
                                        startOffset,
                                        endOffset);
                                System.out.println(partURL);
                                TwitchVideoPart tbp = new TwitchVideoPart(partURL, -1, null , null);
                                if(quality.equals("chunked")) dlInfo.addSourceTwitchBroadcastPart(tbp);
                                else if(quality.equals("high")) dlInfo.addHighTwitchBroadcastPart(tbp);
                                else if(quality.equals("medium")) dlInfo.addMediumTwitchBroadcastPart(tbp);
                                else if(quality.equals("low")) dlInfo.addLowTwitchBroadcastPart(tbp);
                                else if(quality.equals("mobile")) dlInfo.addMobileTwitchBroadcastPart(tbp);
                                //update variables with new part
                                previousPartFileName = m.group(1);
                                startOffset = Integer.parseInt(m.group(2));
                                endOffset = Integer.parseInt(m.group(3));
                            }
                        }
                    }

                    //insert last part
                    String partURL = String.format("%s%s?start_offset=%d&end_offset=%d",
                            line.replace(m3uFilename, ""),
                            previousPartFileName,
                            startOffset,
                            endOffset);
                    System.out.println(partURL);
                    TwitchVideoPart tbp = new TwitchVideoPart(partURL, -1, null , null);
                    if(quality.equals("chunked")) dlInfo.addSourceTwitchBroadcastPart(tbp);
                    else if(quality.equals("high")) dlInfo.addHighTwitchBroadcastPart(tbp);
                    else if(quality.equals("medium")) dlInfo.addMediumTwitchBroadcastPart(tbp);
                    else if(quality.equals("low")) dlInfo.addLowTwitchBroadcastPart(tbp);
                    else if(quality.equals("mobile")) dlInfo.addMobileTwitchBroadcastPart(tbp);
                }
            }
            dlInfoNeedsUpdate = false;
        }
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
        String oldChannelName = this.channel.getName();
        this.channel.setName(channelName);
        pcs.firePropertyChange("channelName", oldChannelName, this.channel.getName());
    }

    public void setChannelDisplayname(String channelDisplayname) {
        String oldChannelDisplayname = this.channel.getDisplayName();
        this.channel.setDisplayName(channelDisplayname);
        pcs.firePropertyChange("channelDisplayname", oldChannelDisplayname, channelDisplayname);
    }

    public void setViews(int views) {
        int oldViews = this.views;
        this.views = views;
        pcs.firePropertyChange("views", oldViews, this.views);
    }

    public void setSelfLink(String selfLink) {
        String oldSelfLink = this.links.get("self");
        this.links.put("self",selfLink);
        pcs.firePropertyChange("selfLink", oldSelfLink, this.links.get("self"));
    }

    public void setChannelLink(String channelLink) {
        String oldChannelLink = this.links.get("channel");
        this.links.put("channel", channelLink);
        pcs.firePropertyChange("channelLink", oldChannelLink, this.links.get("channel"));
    }

    public void setImage(Image image) {
        Image oldImage = this.image;
        this.image = image;
        pcs.firePropertyChange("image", oldImage, this.image);
    }


    @Override
    public String toString() {
        return "TwitchVideoInfo{" +
                "title='" + title + '\'' +
                '}';
    }

    public Image loadPreviewImage() throws MalformedURLException, IOException {
        if (image == null) {
            InputStream is = getPreviewUrl().openStream();
            Image image = ImageIO.read(is);
            this.image = image;
            pcs.firePropertyChange("previewImage", null, image);
            return image;
        } else {
            return this.image;
        }
    }

    public Image getPreviewImage() {
        return image;
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

    public TwitchChannel getChannel() {
        return channel;
    }

    public void setChannel(TwitchChannel channel) {
        this.channel = channel;
    }
}
