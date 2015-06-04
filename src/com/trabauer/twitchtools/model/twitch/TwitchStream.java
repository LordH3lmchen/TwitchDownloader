package com.trabauer.twitchtools.model.twitch;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;

/**
 * Represents a Stream and is able to update it self using the Twitch API
 * https://github.com/justintv/Twitch-API/blob/master/v3_resources/streams.md
 *
 *
 * Created by flo on 5/30/15.
 */
public class TwitchStream {

    //    API URL used by this class
    public static final String APIURL = "https://api.twitch.tv/kraken/streams/";

//    ///////////////////////////////////////////////////////
//    FIELDS returned by the API based on a example request.
//    ///////////////////////////////////////////////////////


//    "_links":
//    {
//        "self":"https://api.twitch.tv/kraken/streams/taketv",
//            "channel":"https://api.twitch.tv/kraken/channels/taketv"
//    },
    @SerializedName("_links") private HashMap<String,String> links;

    private class InternalStream {
//        "stream":
//        {
//            "_id":14646739120,
//                "game":"StarCraft II: Heart of the Swarm",
//                "viewers":1778,
//                "created_at":"2015-05-30T16:46:05Z",
//                "video_height":1080,
//                "average_fps":24.9810901001,
//                "_links":
//            {
//                "self":"https://api.twitch.tv/kraken/streams/taketv"
//            },
//            "preview":
//            {
//                "small":"http://static-cdn.jtvnw.net/previews-ttv/live_user_taketv-80x45.jpg",
//                    "medium":"http://static-cdn.jtvnw.net/previews-ttv/live_user_taketv-320x180.jpg",
//                    "large":"http://static-cdn.jtvnw.net/previews-ttv/live_user_taketv-640x360.jpg",
//                    "template":"http://static-cdn.jtvnw.net/previews-ttv/live_user_taketv-{width}x{height}.jpg"
//            },
//            "channel":
//        }
        @SerializedName("_id") long id;
        @SerializedName("game") String game;
        @SerializedName("viewers") int viewers;
        @SerializedName("created_at") String createdAt;
        @SerializedName("video_height") int videoHeight;
        @SerializedName("average_fps") double averageFps;
        @SerializedName("preview") HashMap<String, String> previewUrlStrings;
        @SerializedName("channel") TwitchChannel channel;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            InternalStream that = (InternalStream) o;

            return id == that.id;

        }

        @Override
        public int hashCode() {
            return (int) (id ^ (id >>> 32));
        }
    }

    @SerializedName("stream") private InternalStream stream;

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TwitchStream that = (TwitchStream) o;

        if (!links.equals(that.links)) return false;
        return !(stream != null ? !stream.equals(that.stream) : that.stream != null);

    }

    @Override
    public int hashCode() {
        int result = links.hashCode();
        result = 31 * result + (stream != null ? stream.hashCode() : 0);
        return result;
    }

    /**
     * Creates a TwitchStream object from https://api.twitch.tv/kraken/channels/channelname
     *
     *
     * @param channelName
     * @return
     * @throws IOException
     */
    public static TwitchStream getTwitchStreamFromAPI(String channelName) throws IOException {
        URL streamApiURL = new URL(APIURL + channelName);
        InputStream is = streamApiURL.openStream();
        InputStreamReader ir = new InputStreamReader(is);
        TwitchStream tStream = new Gson().fromJson(ir, TwitchStream.class);
        ir.close();
        is.close();
        return tStream;
    }


//    ///////////////////////
//    Getters and Setters
//    ///////////////////////

    public HashMap<String, String> getLinks() {
        return links;
    }

    public void setLinks(HashMap<String, String> links) {
        this.links = links;
    }

//    @SerializedName("_id") int id;
    public long getId() {
        return stream.id;
    }

    public void setId(int id) {
        this.stream.id = id;
    }

//    @SerializedName("game") String game;
    public String getGame() {
        return stream.game;
    }

    public void setGame(String game) {
        stream.game = game;
    }

//    @SerializedName("viewers") int viewers;
    public int getViewers() {
        return stream.viewers;
    }

    public void setViewers(int viewers) {
        stream.viewers = viewers;
    }

//    @SerializedName("created_at") String createdAt;
    public String getCreatedAt() {
        return stream.createdAt;
    }

    public void setCreatedAt(String createdAt) {
        stream.createdAt = createdAt;
    }

//    @SerializedName("video_height") int videoHeight;
    public int getVideoHeight () {
        return stream.videoHeight;
    }

    public void setVideoHeight(int videoHeight) {
        stream.videoHeight = videoHeight;
    }

//    @SerializedName("average_fps") double averageFps;
    public double getAverageFps() {
        return stream.averageFps;
    }

    public void setAverageFps(double averageFps) {
        stream.averageFps = averageFps;
    }

//    @SerializedName("preview") HashMap<String, String> previewUrlStrings;
    public HashMap<String, String> getPreviewUrls() {
        return stream.previewUrlStrings;
    }

    public void setPreiviewUrlStrings (HashMap<String, String> previewUrlStrings) {
        stream.previewUrlStrings = previewUrlStrings;
    }

//    @SerializedName("channel") TwitchChannel channel;
    public TwitchChannel getChannel() {
        return stream.channel;
    }

    public void setChannel(TwitchChannel channel) {
        this.stream.channel = channel;
    }
}
