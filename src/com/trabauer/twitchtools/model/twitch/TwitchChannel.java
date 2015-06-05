package com.trabauer.twitchtools.model.twitch;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

/**
 *
 * https://github.com/justintv/Twitch-API/blob/master/v3_resources/channels.md
 *
 * Created by flo on 5/30/15.
 */
public class TwitchChannel {

//    API URL used by this class
    public static final String APIURL = "https://api.twitch.tv/kraken/channels/";


//    FIELDS returned by the API based on a example request.
//    ##############################################

//    "mature":false,
    @SerializedName("mature") private String mature;
//    "status":"[GER] WCS Premier League RO32 - Group G",
    @SerializedName("status") private String status;
//    "broadcaster_language":"de",
    @SerializedName("broadcaster_language") private String broadcasterLanguage;
//    "display_name":"TaKeTV",
    @SerializedName("display_name") private String displayName;
//    "game":"StarCraft II: Heart of the Swarm",
    @SerializedName("game") private String game;
//    "delay":0,
    @SerializedName("delay") private int delay;
//    "language":"de",
    @SerializedName("language") private String language;
//    "_id":30186974,
    @SerializedName("_id") private long id;
//    "name":"taketv",
    @SerializedName("name") private String name;
//    "created_at":"2012-04-30T21:39:56Z",
    @SerializedName("created_at") private String createdAt;
//    "updated_at":"2015-05-31T06:16:49Z",
    @SerializedName("updated_at") private String updatedAt;
//    "logo":"http://static-cdn.jtvnw.net/jtv_user_pictures/taketv-profile_image-9c8116e72285d7b0-300x300.jpeg",
    @SerializedName("logo") private String logoUrlString;
//    "banner":"http://static-cdn.jtvnw.net/jtv_user_pictures/taketv-channel_header_image-8f5fa61dc32c3ad2-640x125.jpeg",
    @SerializedName("banner") private String bannerUrlString;
//    "video_banner":"http://static-cdn.jtvnw.net/jtv_user_pictures/taketv-channel_offline_image-0f177e7e67d2126d-640x360.png",
    @SerializedName("video_banner") private  String videoBannerUrlString;
//    "background":null,
    @SerializedName("background") private String backgroundUrlString;
//    "profile_banner":"http://static-cdn.jtvnw.net/jtv_user_pictures/taketv-profile_banner-cac2fa6d2b34ea2a-480.png",
    @SerializedName("profile_banner") private String profileBannerUrlString;
//    "profile_banner_background_color":"#010108",
    @SerializedName("profile_banner_background_color") private String profileBannerBackgroundColor;
//    "partner":true,
    @SerializedName("partner") private boolean partner;
//    "url":"http://www.twitch.tv/taketv",
    @SerializedName("url") private String urlString;
//    "views":84951781,
    @SerializedName("views") private int views;
//    "followers":81522,
    @SerializedName("followers") private int followers;
//    "_links":{
//        "self":"https://api.twitch.tv/kraken/channels/taketv",
//        "follows":"https://api.twitch.tv/kraken/channels/taketv/follows",
//        "commercial":"https://api.twitch.tv/kraken/channels/taketv/commercial",
//        "stream_key":"https://api.twitch.tv/kraken/channels/taketv/stream_key",
//        "chat":"https://api.twitch.tv/kraken/chat/taketv",
//        "features":"https://api.twitch.tv/kraken/channels/taketv/features",
//        "subscriptions":"https://api.twitch.tv/kraken/channels/taketv/subscriptions",
//        "editors":"https://api.twitch.tv/kraken/channels/taketv/editors",
//        "teams":"https://api.twitch.tv/kraken/channels/taketv/teams",
//        "videos":"https://api.twitch.tv/kraken/channels/taketv/videos"
//        }
    @SerializedName("_links") private HashMap<String,String> links;
    //implemented as HashMap to avoid problems when Twitch decides to expand the Links

    private TwitchStream stream;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TwitchChannel channel = (TwitchChannel) o;

        if (!name.equals(channel.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        String output = super.toString();
        output.concat(name + '\n');
        output.concat(displayName + '\n');
        output.concat(logoUrlString + '\n');
        return output;
    }

    public static TwitchChannel getTwitchChannel(String channelName) throws IOException{
        URL channelApiUrl = new URL(APIURL + channelName);
        InputStream is = channelApiUrl.openStream();
        InputStreamReader ir = new InputStreamReader(is);
        TwitchChannel channel = new Gson().fromJson(ir, TwitchChannel.class);
        ir.close();
        is.close();
        return channel;
    }

    public void update(String channelName) throws IOException {
        update(getTwitchChannel(channelName));
    }

    public void update(TwitchChannel sourceChannel) {
        this.name = sourceChannel.name;
        this.displayName = sourceChannel.displayName;
        this.logoUrlString = sourceChannel.logoUrlString;
    }


    /**
     * Reloads the channel Informations. This can be used to get additional information of a channel
     * Some other APIs from Twitch don't deliver all fileds in a channel object (for example TwitchVideoInfo).
     * This Method reloads the channel using the
     * <a href="https://github.com/justintv/Twitch-API/blob/master/v3_resources/channels.md#get-channelschannel">channels API</a>
     *
     *
     *
     * @throws IOException
     */
    public void reload() throws IOException {
        update(getTwitchChannel(getName()));
    }




//    Getters and Setters


    public String getMature() {
        return mature;
    }

    public void setMature(String mature) {
        this.mature = mature;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBroadcasterLanguage() {
        return broadcasterLanguage;
    }

    public void setBroadcasterLanguage(String broadcasterLanguage) {
        this.broadcasterLanguage = broadcasterLanguage;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getGame() {
        return game;
    }

    public void setGame(String game) {
        this.game = game;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getLogoUrlString() {
        return logoUrlString;
    }

    public void setLogoUrlString(String logoUrlString) {
        this.logoUrlString = logoUrlString;
    }

    public String getVideoBannerUrlString() {
        return videoBannerUrlString;
    }

    public void setVideoBannerUrlString(String videoBannerUrlString) {
        this.videoBannerUrlString = videoBannerUrlString;
    }

    public String getBannerUrlString() {
        return bannerUrlString;
    }

    public void setBannerUrlString(String bannerUrlString) {
        this.bannerUrlString = bannerUrlString;
    }

    public String getBackgroundUrlString() {
        return backgroundUrlString;
    }

    public void setBackgroundUrlString(String backgroundUrlString) {
        this.backgroundUrlString = backgroundUrlString;
    }

    public String getProfileBannerUrlString() {
        return profileBannerUrlString;
    }

    public void setProfileBannerUrlString(String profileBannerUrlString) {
        this.profileBannerUrlString = profileBannerUrlString;
    }

    public String getProfileBannerBackgroundColor() {
        return profileBannerBackgroundColor;
    }

    public void setProfileBannerBackgroundColor(String profileBannerBackgroundColor) {
        this.profileBannerBackgroundColor = profileBannerBackgroundColor;
    }

    public boolean isPartner() {
        return partner;
    }

    public void setPartner(boolean partner) {
        this.partner = partner;
    }

    public String getUrlString() {
        return urlString;
    }

    public void setUrlString(String urlString) {
        this.urlString = urlString;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public int getFollowers() {
        return followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }

    public HashMap<String, String> getLinks() {
        return links;
    }

    public void setLinks(HashMap<String, String> links) {
        this.links = links;
    }

    public TwitchStream getStream() throws IOException {
        if(stream == null) {
            stream = TwitchStream.getTwitchStreamFromAPI(this.name);
        }
        return stream;
    }

    public void setStream(TwitchStream stream) {
        this.stream = stream;
    }
}
