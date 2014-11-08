package com.trabauer.twitchtools.twitch;

import com.google.gson.Gson;
import com.trabauer.twitchtools.Video;
import com.trabauer.twitchtools.VideoPart;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Created by flo on 03.11.14.
 */
public class Twitch {

    private static String apiURL = "https://api.twitch.tv";

    public static Video getPastBroadcastById(String id) {
        InputStream infoIs = null;
        InputStream dlInfoIs = null;
        Scanner infoSc = null;
        Scanner dlInfoSc = null;
        Video video = null;


        try {
            //StremInfo (api.twitch.tv/kraken/videos)
            URL InfoURL = new URL(new URL(apiURL), "/kraken/videos/a" + id);
            infoIs = InfoURL.openStream();
            infoSc = new Scanner(infoIs);
            String infoJsonStr = "";
            //DownloadInfo (api.twitch.tv/api/videos)
            URL dlInfoURL = new URL(new URL(apiURL), "/api/videos/a" + id);
            dlInfoIs = dlInfoURL.openStream();
            dlInfoSc = new Scanner(dlInfoIs);
            String dlInfoJsonStr = "";

            while(infoSc.hasNextLine()) {
                infoJsonStr += infoSc.nextLine();
            }

            while(dlInfoSc.hasNextLine()) {
                dlInfoJsonStr += dlInfoSc.nextLine();
            }

            Gson gson = new Gson();
            BroadCastInfo broadCastInfo = gson.fromJson(infoJsonStr, BroadCastInfo.class);
            DownloadInfo downloadInfo = gson.fromJson(dlInfoJsonStr, DownloadInfo.class);
            video = new Video(
                    broadCastInfo.getTitle(),
                    broadCastInfo.getDescription(),
                    broadCastInfo.getBroadcastId(),
                    broadCastInfo.getTagList(),
                    broadCastInfo.getId(),
                    broadCastInfo.getRecordedAt(),
                    broadCastInfo.getGame(),
                    broadCastInfo.getLength(),
                    broadCastInfo.getPreview(),
                    broadCastInfo.getUrl(),
                    broadCastInfo.getChannel().name,
                    broadCastInfo.getChannel().displayName
                    );

            for(String key: downloadInfo.getAllParts().keySet()) {
                for(DownloadInfo.BroadCastPart broadCastPart: downloadInfo.getAllParts().get(key)) {
                    video.addVideoPart(key, new VideoPart(new URL(broadCastPart.url), broadCastPart.length,
                            new URL(broadCastPart.vodCountUrl), broadCastPart.upkeep));
                }
            }

        } catch ( Exception e) {
            e.printStackTrace();
        } finally {
            if (dlInfoIs != null || infoIs != null) {
                try {
                    dlInfoIs.close();
                    infoIs.close();
                } catch (IOException e) {

                }
            }
        }



        return video;
    }

    public static Video getPastBroadcastByUrl(URL twitchUrl) {
        if(Pattern.matches("http://www.twitch.tv/\\w+/b/\\d+", twitchUrl.toString())) {
            String id = twitchUrl.toString().split("/")[5];
             return getPastBroadcastById(id);
        } else {
            return null;
        }
    }
}
