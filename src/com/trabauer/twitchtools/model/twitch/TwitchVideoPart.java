package com.trabauer.twitchtools.model.twitch;

import com.google.gson.annotations.SerializedName;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Flo on 25.01.2015.
 */
public class TwitchVideoPart {
    @SerializedName("url")private String url;
    @SerializedName("length")private int length;
    @SerializedName("vod_count_url")private String vodCountUrl;
    @SerializedName("upkeep")private String upkeep;
    private int partNumber;


    public TwitchVideoPart(String url, int length, String vodCountUrl, String upkeep) {
        this.url = url;
        this.length = length;
        this.vodCountUrl = vodCountUrl;
        this.upkeep = upkeep;
    }

    public String getFileExtension() {
        try {
            String fileExtension = "";
            URL url = new URL(getUrl());
            int i = url.getFile().lastIndexOf('.');
            if(i>0) fileExtension = url.getFile().substring(i);
            fileExtension = fileExtension.replaceAll("\\?.*$", "");
            return fileExtension;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getVodCountUrl() {
        return vodCountUrl;
    }

    public void setVodCountUrl(String vodCountUrl) {
        this.vodCountUrl = vodCountUrl;
    }

    public String getUpkeep() {
        return upkeep;
    }

    public void setUpkeep(String upkeep) {
        this.upkeep = upkeep;
    }

    public int getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(int partNumber) {
        this.partNumber = partNumber;
    }
}
