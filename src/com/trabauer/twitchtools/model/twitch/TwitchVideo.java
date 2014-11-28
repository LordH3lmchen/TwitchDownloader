package com.trabauer.twitchtools.model.twitch;

import com.google.gson.Gson;
import com.trabauer.twitchtools.TwitchToolsApp;
import com.trabauer.twitchtools.model.Video;
import com.trabauer.twitchtools.model.VideoPart;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by Flo on 09.11.2014.
 */
public class TwitchVideo extends Video {
    /**
     * Representiert ein TwitchVideo und enthält sämtliche Informationen, die die Twitch API liefert.
     *
     */

    private String APIURL = "https://api.twitch.tv";

    private String broadcastId;
    private String tagList;
    private String id;
    private Calendar recordedAt;
    private String game;
    private URL preview; //Image
    private URL url; //URL To TwitchPage
    private int views;
    private URL channelLink;
    private URL selfLink;
    private String channelName;
    private String channelDisplayName;
    private HashMap<String, ArrayList<TwitchVideoPart>> twitchVideoParts;




    /**
     * Representiert ein TwitchVideo
     */
    public TwitchVideo() {
        super();
        this.twitchVideoParts = new HashMap<String, ArrayList<TwitchVideoPart>>();
    }

    /**
     * Erzeugt ein TwitchVideoObject mit den Informationen aus der angegeben URL aus dem twitch-profile
     *
     * Die URL muss eine URL aus den PastBroadcasts eines Twitch Profils sein.
     * (http://www.twitch.tv/taketv/profile/past_broadcasts)
     *
     * z.B. http://www.twitch.tv/taketv/b/590565753
     *
     * @param url Twitch URL
     */
    public TwitchVideo(URL url) {
        this();
        updateTwitchVideoByUrl(url);
    }


    /**
     * Erzeugt ein TwitchVideo mit den Informationen aus der Angegebenen VideoId von Twitch
     *
     * @param id
     */
    public TwitchVideo(String id) {
        this();
        updateTwitchVideoById(id);
    }

    /**
     * Erzeugt ein TwitchVideo mit den angegebenen Informationen
     *
     *
     * @param title                 video-title
     * @param description           Description
     * @param broadcastId           the Id of the broadcast (usually a number)
     * @param tagList               a taglist (usually empty right know on twitch)
     * @param id                    id of the Video (videoId isn't a brodcastId) a video could by a small part of a broadcast
     * @param recordedAt            recording date of the video
     * @param game                  the game that is played on this video
     * @param length                the length of the video in seconds
     * @param preview               Url to a preview image
     * @param url                   Url to the video source
     * @param channelName           name of the channel on twitch
     * @param channelDisplayName    display name of the channel
     */
    public TwitchVideo(String title, String description, String broadcastId, String tagList, String id, Calendar recordedAt,
                 String game, int length, URL preview, URL url, String channelName, String channelDisplayName){


        super(title, description, length);
        this.broadcastId = broadcastId;
        this.tagList = tagList;
        this.id = id;
        this.recordedAt = recordedAt;
        this.game = game;
        this.preview = preview;
        this.url = url;
        this.channelName = channelName;
        this.channelDisplayName = channelDisplayName;
        this.twitchVideoParts = new HashMap<String, ArrayList<TwitchVideoPart>>();
    }


    /**
     * Gibt die URL zum Vorschaubild des Videos zurück.
     * @return PreviewImageUrl
     */
    public URL getPreview() {
        return preview;
    }

    /**
     * setzt die URL zum PreviewImage des Videos
     * @param preview
     */
    public void setPreview(URL preview) {
        this.preview = preview;
    }

    /**
     * Liefert die Quell URL zum Video(lautAPI)
     * @return QuellUrl
     */
    public URL getUrl() {
        return url;
    }

    /**
     * Setzt die VideoQuelle auf die angegebene Url
     * @param url
     */
    public void setUrl(URL url) {
        this.url = url;
    }

    /**
     * Liefert den Namen des Channels dieser enthält keine Sonderzeichen und besondere Formatierungen
     * Der channelName sollte nur aus Kleinbuchstaben bestehen.
     * @return channelName
     */
    public String getChannelName() {
        return channelName;
    }

    /**
     * Setzt den Channel Namen. Dieser sollte keine Sonderzeichen und seltsame Formatierungen enthalten
     * @param channelName
     */
    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    /**
     * Liefert den Channel Display Namen. Dieser darf Sonderzeichen und Formatierungen enthalten.
     * @return
     */
    public String getChannelDisplayName() {
        return channelDisplayName;
    }


    /**
     * setzt den ChannelDisplay Namen dieser darf mit Sonderzeichen und seltsamen verzierungen versehen werden
     * @param channelDisplayName
     */
    public void setChannelDisplayName(String channelDisplayName) {
        this.channelDisplayName = channelDisplayName;
    }

    /**
     * Liefert die Id des Broacasts
     * @return
     */
    public String getBroadcastId() {
        return broadcastId;
    }

    /**
     * Setzt die Id des Broacasts
     * @param broadcastId
     */
    public void setBroadcastId(String broadcastId) {
        this.broadcastId = broadcastId;
    }

    /**
     * Liefert eine TagListe das Videos
     * @return tagListString
     */
    public String getTagList() {
        return tagList;
    }

    /**
     * Setzt eine TagListe das Videos
     * @param tagList
     */
    public void setTagList(String tagList) {
        this.tagList = tagList;
    }

    /**
     * Lifert die Id des Videos. Die VideoId ist nicht i
     * @return
     */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
        setChanged();
        notifyObservers();
    }

    public Calendar getRecordedAt() {
        return recordedAt;
    }

    public void setRecordedAt(Calendar recordedAt) {
        this.recordedAt = recordedAt;
    }

    public String getGame() {
        return game;
    }

    public void setGame(String game) {
        this.game = game;
    }

    public String getBestAvailableQuality() {
        ArrayList<String> orderedQualitiesDescending = new ArrayList<String>();
        orderedQualitiesDescending.add("source");
        orderedQualitiesDescending.add("high");
        orderedQualitiesDescending.add("mid");
        orderedQualitiesDescending.add("low");
        orderedQualitiesDescending.add("mobile");

        for(String quality: orderedQualitiesDescending) {
            if(getAvailableQualities().contains(quality))
                return quality;
        }
        return null;
    }

    /**
     *  Liefert eine HashMap mit Informationen zum Video.
     *  Kann z.B. verwender werden um Variablen in Filenamen zu verwenden.
     *
     * @return eine HashMap mit Informationen zum Video
     */
    public HashMap<String, String> getVideoInformation() {
        HashMap<String, String> videoInformation = new HashMap<String, String>();

        videoInformation.put("PreviewURL", getPreview().toString());
        videoInformation.put("URL", getUrl().toString());
        videoInformation.put("ChannelName", getChannelName());
        videoInformation.put("ChannelDisplayName", getChannelDisplayName());
        videoInformation.put("BroadcastId", getBroadcastId());
        videoInformation.put("TagList", getTagList());
        videoInformation.put("Id", getId());
        videoInformation.put("recordedAt", Integer.toString(getRecordedAt().get(Calendar.YEAR)));
        videoInformation.put("Game", getGame());
        videoInformation.put("BestAvailableQuality", getBestAvailableQuality());
        videoInformation.put("Title", getTitle());
        videoInformation.put("Description", getDescription());
        videoInformation.put("Length", Integer.toString(getLength()));

        return videoInformation;
    }



    private void addTwitchVideoPart(String quality, TwitchVideoPart videoPart) {
        if(! twitchVideoParts.containsKey(quality)) twitchVideoParts.put(quality, new ArrayList<TwitchVideoPart>());
        twitchVideoParts.get(quality).add(videoPart);
    }

    /**
     * Aktualisiert das TwitchVideo mit den Informationen von Twitch.tv
     * @param id    video id from Twitch.tv
     */
    public void updateTwitchVideoById(String id) {
        InputStream infoIs = null;
        InputStream dlInfoIs = null;
        Scanner infoSc = null;
        Scanner dlInfoSc = null;


        try {
            //StremInfo (api.twitch.tv/kraken/videos)
            URL InfoURL = new URL(new URL(APIURL), "/kraken/videos/" + id);
            infoIs = InfoURL.openStream();
            infoSc = new Scanner(infoIs);
            String infoJsonStr = "";
            //DownloadInfo (api.twitch.tv/api/videos)
            URL dlInfoURL = new URL(new URL(APIURL), "/api/videos/" + id);
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
            setTitle(broadCastInfo.getTitle());
            setDescription(broadCastInfo.getDescription());
            setBroadcastId(broadCastInfo.getBroadcastId());
            setTagList(broadCastInfo.getTagList());
            setId(broadCastInfo.getId());
            setRecordedAt(broadCastInfo.getRecordedAt());
            setGame(broadCastInfo.getGame());
            setLength(broadCastInfo.getLength());
            setPreview(broadCastInfo.getPreview());
            setUrl(broadCastInfo.getUrl());
            setChannelName(broadCastInfo.getChannel().name);
            setChannelDisplayName(broadCastInfo.getChannel().displayName);


            for(String key: downloadInfo.getAllParts().keySet()) {
                int partNumber = 0;
                for(DownloadInfo.BroadCastPart broadCastPart: downloadInfo.getAllParts().get(key)) {
                    this.addTwitchVideoPart(key, new TwitchVideoPart(new URL(broadCastPart.url), partNumber++, broadCastPart.length,
                            new URL(broadCastPart.vodCountUrl), broadCastPart.upkeep));
                }
            }

            setChanged();
            notifyObservers();

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
    }

    /**
     * Aktualisiert das Video mit den Informationen von Twitch.tv
     * @param twitchUrl    url zum PastBastBroadCast oder HighLight
     */
    public void updateTwitchVideoByUrl(URL twitchUrl) {
        if(Pattern.matches("http://www.twitch.tv/\\w+/b/\\d+", twitchUrl.toString())) {
            String id = twitchUrl.toString().split("/")[5];
            updateTwitchVideoById("a".concat(id));
        } else if(Pattern.matches("http://www.twitch.tv/\\w+/c/\\d+", twitchUrl.toString())) {
            String id = twitchUrl.toString().split("/")[5];
            updateTwitchVideoById("c".concat(id));
        }
    }

    @Override
    public LinkedHashMap<String, String> getStreamInformation() {
        LinkedHashMap<String, String> streamInformation = super.getStreamInformation();
        streamInformation.put("Channel", getChannelName());
        streamInformation.put("ChannelDisplayName", getChannelDisplayName());
        //streamInformation.put("RecordedAt", getRecordedAt().getDisplayName(Calendar.YEAR, Calendar.SHORT, Locale.ENGLISH));
        streamInformation.put("Game", getGame());
        streamInformation.put("BestQuality", getBestAvailableQuality());

        return streamInformation;
    }


    @Override
    public LinkedHashSet<String> getAvailableQualities() {
        return new LinkedHashSet<String>(twitchVideoParts.keySet());
    }

    public ArrayList<TwitchVideoPart> getTwitchVideoParts(String quality) {
        return twitchVideoParts.get(quality);
    }


}


