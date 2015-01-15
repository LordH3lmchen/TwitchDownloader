package com.trabauer.twitchtools.model.twitch;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Flo on 14.01.2015.
 */
public class TwitchVodAccessToken {
    @SerializedName("token")
    private String token;
    @SerializedName("sig")
    private String sig;

    public TwitchVodAccessToken() {
    }

    public String getToken() {
        return token;
    }

    public String getSig() {
        return sig;
    }
}
