package com.trabauer.twitchtools.model.twitch;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Flo on 06.11.2014.
 *
 *
 *
 * Diese Klasse representiert Ein Video Objekt von der Twitch api
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

    class BroadCastPart {
        @SerializedName("url")public String url;
        @SerializedName("length")public int length;
        @SerializedName("vod_count_url")public String vodCountUrl;
        @SerializedName("upkeep")public String upkeep;
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
}
