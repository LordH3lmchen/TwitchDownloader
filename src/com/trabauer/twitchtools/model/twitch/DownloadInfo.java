package com.trabauer.twitchtools.model.twitch;

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
import java.util.HashMap;

/**
 * Created by Flo on 06.11.2014.
 *
 *
 *
 * Diese Klasse representiert Ein VideoObjekt von der Twitch api
 * https://api.twitch.tv/api/videos/a582145870
 *
 * Dies ist die API-Schnitsstelle für den Player der so festellt welches VideoFile von wo bis wo abgespielt werden soll.
 *
 * Highlights stellen nur ausschnitte von BastBroadcasts dar. Das Heisst der startOffset und end Offset (angabe in Sekunden)
 * liegen nicht bei 0 und dem Ende des Broadcasts. Der Twitch PLayer z.B. berechnet sich vermutlich welches File er laden
 * und wohin er in diesem Springen muss.
 *
 *
 *
 */
public class DownloadInfo {
    @SerializedName("api_id")       private String  apiId;
    @SerializedName("start_offset") private int     startOffset;
    @SerializedName("end_offset")   private int     endOffset;
    @SerializedName("play_offset")  private int     playOffset;
    @SerializedName("increment_view_count_url") private String incrementViewCountUrl;
    @SerializedName("path") private String path;
    @SerializedName("duration") private int duration;
    @SerializedName("broadcaster_software") private String broadcasterSoftware;
    @SerializedName("channel") private String channel;
    @SerializedName("chunks") private Chunks chunks;
    @SerializedName("restrictions") private Restrictions restrictions;
    @SerializedName("preview_small") private String previewSmall;
    @SerializedName("preview") private String preview;
    @SerializedName("vod_ad_frequency") private String vodAdFrequency;
    @SerializedName("vod_ad_length") private String vodAdLength;
    @SerializedName("redirect_api_id") private String redirectApiId;
    @SerializedName("muted_segments") private String mutedSegments;

    private final PropertyChangeSupport pcs;

    public class BroadCastPart {
        @SerializedName("url")public String url;
        @SerializedName("length")public int length;
        @SerializedName("vod_count_url")public String vodCountUrl;
        @SerializedName("upkeep")public String upkeep;

        public String getUrl() {
            return url;
        }

        public int getLength() {
            return length;
        }

        public String getVodCountUrl() {
            return vodCountUrl;
        }

        public String getUpkeep() {
            return upkeep;
        }
    }

    class Chunks {
        @SerializedName("live")private ArrayList<BroadCastPart> source;
        @SerializedName("240p")private ArrayList<BroadCastPart> mobile;
        @SerializedName("360p")private ArrayList<BroadCastPart> low;
        @SerializedName("480p")private ArrayList<BroadCastPart> mid;
        @SerializedName("720p")private  ArrayList<BroadCastPart> high;

        public ArrayList<BroadCastPart> getSource() {
            return source;
        }

        public ArrayList<BroadCastPart> getMobile() {
            return mobile;
        }

        public ArrayList<BroadCastPart> getLow() {
            return low;
        }

        public ArrayList<BroadCastPart> getMid() {
            return mid;
        }

        public ArrayList<BroadCastPart> getHigh() {
            return high;
        }

        @Override
        public String toString() {
            return source.toString();
        }
    }

    class Restrictions {

    }




    public DownloadInfo(TwitchVideoInfo twitchVideoInfo) {
        this.pcs = new PropertyChangeSupport(this);


        //update(archiveId)
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener);
    }

    public String getApiId() {
        return apiId;
    }

    public int getStartOffset() {
        return startOffset;
    }

    public int getEndOffset() {
        return endOffset;
    }

    public int getPlayOffset() {
        return playOffset;
    }

    public String getIncrementViewCountUrl() {
        return incrementViewCountUrl;
    }

    public String getPath() {
        return path;
    }

    public int getDuration() {
        return duration;
    }

    public String getBroadcasterSoftware() {
        return broadcasterSoftware;
    }

    public String getChannel() {
        return channel;
    }

    public HashMap<String, ArrayList<BroadCastPart>> getAllParts() {
        HashMap<String, ArrayList<BroadCastPart>> allParts = new HashMap<String, ArrayList<BroadCastPart>>();
        if(chunks.getSource() != null)
            allParts.put("source", chunks.getSource());
        if(chunks.getHigh() != null)
            allParts.put("high", chunks.getHigh());
        if(chunks.getMid() != null)
            allParts.put("mid", chunks.getMid());
        if(chunks.getLow() != null)
            allParts.put("low", chunks.getLow());
        if(chunks.getMobile() != null)
            allParts.put("mobile", chunks.getMobile());

        return allParts;
    }

    public String getPreviewSmall() {
        return previewSmall;
    }

    public String getPreview() {
        return preview;
    }

    public String getVodAdFrequency() {
        return vodAdFrequency;
    }

    public String getVodAdLength() {
        return vodAdLength;
    }

    public String getRedirectApiId() {
        return redirectApiId;
    }

    public String getMutedSegments() {
        return mutedSegments;
    }

    public Restrictions getRestrictions() {
        return restrictions;
    }

    public void setApiId(String apiId) {
        String oldApId = this.apiId;
        this.apiId = apiId;
        this.pcs.firePropertyChange("apiId", oldApId, this.apiId);
    }

    public void setStartOffset(int startOffset) {
        int oldStartOffset = this.startOffset;
        this.startOffset = startOffset;
        this.pcs.firePropertyChange("startOffset", oldStartOffset, this.startOffset);
    }

    public void setEndOffset(int endOffset) {
        int oldEndOffset = this.endOffset;
        this.endOffset = endOffset;
        this.pcs.firePropertyChange("endOffset", oldEndOffset, this.endOffset);
    }

    public void setPlayOffset(int playOffset) {
        int oldPlayOffset = this.playOffset;
        this.playOffset = playOffset;
        this.pcs.firePropertyChange("playOffset", oldPlayOffset, this.playOffset);
    }

    public void setIncrementViewCountUrl(String incrementViewCountUrl) {
        String oldIncrementViewCountUrl = this.incrementViewCountUrl;
        this.incrementViewCountUrl = incrementViewCountUrl;
        this.pcs.firePropertyChange("incrementViewCountUrl", oldIncrementViewCountUrl, this.incrementViewCountUrl);
    }

    public void setPath(String path) {
        String oldPath = this.path;
        this.path = path;
        this.pcs.firePropertyChange("path", oldPath, this.path);
    }

    public void setDuration(int duration) {
        int oldDuration = this.duration;
        this.duration = duration;
        this.pcs.firePropertyChange("duration", oldDuration, this.duration);
    }

    public void setBroadcasterSoftware(String broadcasterSoftware) {
        String oldBroadCasterSofware = this.broadcasterSoftware;
        this.broadcasterSoftware = broadcasterSoftware;
        this.pcs.firePropertyChange("broadcasterSoftware", oldBroadCasterSofware, this.broadcasterSoftware);
    }

    public void setChannel(String channel) {
        String oldChannel = this.channel;
        this.channel = channel;
        this.pcs.firePropertyChange("channel", oldChannel, this.channel);
    }

    private void setChunks(Chunks chunks) {
        HashMap<String, ArrayList<BroadCastPart>> oldParts = getAllParts();
        this.chunks = chunks;
        this.pcs.firePropertyChange("chunks", oldParts, getAllParts());
    }

    public void setPreviewSmall(String previewSmall) {
        String oldPreviewSmall = this.previewSmall;
        this.previewSmall = previewSmall;
        this.pcs.firePropertyChange("previewSmall", oldPreviewSmall, this.previewSmall);
    }

    public void setPreview(String preview) {
        String oldPreview = this.preview;
        this.preview = preview;
        this.pcs.firePropertyChange("preview", oldPreview, this.preview);
    }

    public void setVodAdFrequency(String vodAdFrequency) {
        String oldVodAdFrequency = this.vodAdFrequency;
        this.vodAdFrequency = vodAdFrequency;
        this.pcs.firePropertyChange("vodAdFrequency", oldVodAdFrequency, this.vodAdFrequency);
    }

    public void setVodAdLength(String vodAdLength) {
        String oldVodAdLength = this.vodAdLength;
        this.vodAdLength = vodAdLength;
        this.pcs.firePropertyChange("vodAdLength", oldVodAdLength, this.vodAdLength);
    }

    public void setRedirectApiId(String redirectApiId) {
        String oldRedirectApiId = this.redirectApiId;
        this.redirectApiId = redirectApiId;
        this.pcs.firePropertyChange("redirectApiId", oldRedirectApiId, this.redirectApiId);
    }

    public void setMutedSegments(String mutedSegments) {
        String oldMutedSegments = this.mutedSegments;
        this.mutedSegments = mutedSegments;
        this.pcs.firePropertyChange("mutedSegments", oldMutedSegments, this.mutedSegments);
    }

    public void update(String archiveId) throws IOException {
        URL apiRequestUrl = new URL("https://api.twitch.tv/api/videos/".concat(archiveId));
        update(apiRequestUrl);
    }

    public void update(URL apiURL) throws IOException {
        InputStream is = apiURL.openStream();
        InputStreamReader ir = new InputStreamReader(is);
        DownloadInfo dlInfo = new Gson().fromJson(ir, DownloadInfo.class);
        update(dlInfo);
    }

    private void update(DownloadInfo dlInfo) {
        setApiId(dlInfo.apiId);
        setStartOffset(dlInfo.startOffset);
        setPlayOffset(dlInfo.playOffset);
        setIncrementViewCountUrl(dlInfo.incrementViewCountUrl);
        setPath(dlInfo.path);
        setDuration(dlInfo.duration);
        setBroadcasterSoftware(dlInfo.broadcasterSoftware);
        setChannel(dlInfo.channel);
        setChunks(dlInfo.chunks);
        setPreviewSmall(dlInfo.previewSmall);
        setPreview(dlInfo.preview);
        setVodAdFrequency(dlInfo.vodAdFrequency);
        setVodAdLength(dlInfo.vodAdLength);
        setRedirectApiId(dlInfo.redirectApiId);
        setMutedSegments(dlInfo.mutedSegments);

        this.pcs.firePropertyChange("fullUpdate", null, this);
    }
}
