package com.trabauer.twitchtools.model.twitch;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

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
public class TwitchDownloadInfo {
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
    private LinkedList<Observer> observers;



    public void addSourceTwitchBroadcastPart(TwitchVideoPart tbp) {
        if(chunks==null) chunks = new Chunks();
        chunks.source.add(tbp);
    }

    public void addHighTwitchBroadcastPart(TwitchVideoPart tbp) {
        if(chunks==null) chunks = new Chunks();
        chunks.high.add(tbp);
    }

    public void addMediumTwitchBroadcastPart(TwitchVideoPart tbp) {
        if(chunks==null) chunks = new Chunks();
        chunks.mid.add(tbp);
    }

    public void addLowTwitchBroadcastPart(TwitchVideoPart tbp) {
        if(chunks==null) chunks = new Chunks();
        chunks.low.add(tbp);
    }

    public void addMobileTwitchBroadcastPart(TwitchVideoPart tbp) {
        if(chunks==null) chunks = new Chunks();
        chunks.mobile.add(tbp);
    }

    public void addObserver(Observer observer) {
        this.observers.add(observer);

    }


    class Chunks {  //TODO maybe anyone has a solution to replace that with a Hashmap (Twitch could change the names of qualities from time to time)
        @SerializedName("live")private ArrayList<TwitchVideoPart> source;
        @SerializedName("240p")private ArrayList<TwitchVideoPart> mobile;
        @SerializedName("360p")private ArrayList<TwitchVideoPart> low;
        @SerializedName("480p")private ArrayList<TwitchVideoPart> mid;
        @SerializedName("720p")private ArrayList<TwitchVideoPart> high;

        public Chunks() {
            source = new ArrayList<TwitchVideoPart>();
            mobile = new ArrayList<TwitchVideoPart>();
            low = new ArrayList<TwitchVideoPart>();
            mid = new ArrayList<TwitchVideoPart>();
            high = new ArrayList<TwitchVideoPart>();
        }

        public ArrayList<TwitchVideoPart> getSource() {
            return source;
        }

        public ArrayList<TwitchVideoPart> getMobile() {
            return mobile;
        }

        public ArrayList<TwitchVideoPart> getLow() {
            return low;
        }

        public ArrayList<TwitchVideoPart> getMid() {
            return mid;
        }

        public ArrayList<TwitchVideoPart> getHigh() {
            return high;
        }



        @Override
        public String toString() {
            return source.toString();
        }
    }

    class Restrictions {

    }

    public ArrayList<TwitchVideoPart> getTwitchBroadcastParts(String quality) {
        if(quality.equals("source")) return chunks.getSource();
        else if(quality.equals("high")) return chunks.getHigh();
        else if(quality.equals("medium")) return chunks.getMid();
        else if(quality.equals("low")) return chunks.getLow();
        else if(quality.equals("mobile")) return chunks.getMobile();
        else return null;
    }


    public TwitchDownloadInfo() {
//        System.out.println("DownloadInfoContructor");
        this.pcs = new PropertyChangeSupport(this);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener);
    }

    public ArrayList<String> getAvailableQualities() {
        ArrayList<String> availableQualities = new ArrayList<>();
        if(chunks.getSource()!=null)
            if(! chunks.getSource().isEmpty() )
                availableQualities.add("source");
        if(chunks.getHigh()!=null)
            if(! chunks.getHigh().isEmpty() )
                availableQualities.add("high");
        if(chunks.getMid()!=null)
            if(! chunks.getMid().isEmpty() )
                availableQualities.add("medium");
        if(chunks.getLow()!=null)
            if(! chunks.getLow().isEmpty() )
                availableQualities.add("low");
        if(chunks.getMobile()!=null)
            if(! chunks.getMobile().isEmpty() )
                availableQualities.add("mobile");
        return availableQualities;
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

    public HashMap<String, ArrayList<TwitchVideoPart>> getAllParts() {
        HashMap<String, ArrayList<TwitchVideoPart>> allParts = new HashMap<String, ArrayList<TwitchVideoPart>>();
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

    public String getBestAvailableQuality() {
        ArrayList<String> preferedQulitiesDescendingOrder = new ArrayList<String>();
        preferedQulitiesDescendingOrder.add("source");
        preferedQulitiesDescendingOrder.add("high");
        preferedQulitiesDescendingOrder.add("medium");
        preferedQulitiesDescendingOrder.add("low");
        preferedQulitiesDescendingOrder.add("mobile");
        return getPreferedQuality(preferedQulitiesDescendingOrder);
    }

    public String getPreferedQuality(List<String> preferedQualitiesDescendingOrder) {
        for(String quality: preferedQualitiesDescendingOrder) {
            if(getAvailableQualities().contains(quality))
                return quality;
        }
        return null;
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
        HashMap<String, ArrayList<TwitchVideoPart>> oldParts;
        if(! (this.chunks==null)) oldParts = getAllParts();
        else oldParts = null;
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
        TwitchDownloadInfo dlInfo = new Gson().fromJson(ir, TwitchDownloadInfo.class);
        update(dlInfo);
    }

    private void update(TwitchDownloadInfo dlInfo) {
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
