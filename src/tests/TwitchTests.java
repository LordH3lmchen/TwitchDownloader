package tests;

import com.google.gson.Gson;
import com.trabauer.twitchtools.model.Video;
import com.trabauer.twitchtools.model.twitch.BroadCastInfo;
import com.trabauer.twitchtools.model.twitch.DownloadInfo;
import com.trabauer.twitchtools.model.twitch.TwitchVideo;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by Flo on 05.11.2014.
 */
public class TwitchTests {

    @Test
    public void gsonDeserializeJsonFromTwitchAPI() {

        String apiURL = "https://api.twitch.tv";
        String id = "582145870";

        InputStream infoIs = null;
        InputStream dlInfoIs = null;
        Scanner infoSc = null;
        Scanner dlInfoSc = null;
        Video video = null;





        try {
            URL InfoURL = new URL(new URL(apiURL), "/kraken/videos/a" + id);
            infoIs = InfoURL.openStream();
            infoSc = new Scanner(infoIs);
            String infoJsonStr = "";

            URL dlInfoURL = new URL(new URL(apiURL), "/api/videos/a" + id);
            dlInfoIs = dlInfoURL.openStream();
            dlInfoSc = new Scanner(dlInfoIs);
            String dlInfoJsonStr = "";

            while (infoSc.hasNextLine()) {
                infoJsonStr += infoSc.nextLine();
            }

            while (dlInfoSc.hasNextLine()) {
                dlInfoJsonStr += dlInfoSc.nextLine();
            }

            Gson gson = new Gson();
            BroadCastInfo broadCastInfo = gson.fromJson(infoJsonStr, BroadCastInfo.class);
            DownloadInfo downloadInfo = gson.fromJson(dlInfoJsonStr, DownloadInfo.class);

            System.out.println("weird!  :| ");

            System.out.println("First Part: " + downloadInfo.getAllParts());





        } catch (Exception e) {
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

    @Test
    public void gettingVideoInfo() {
        System.out.println("Getting Video Info");

        String expectedResult = "Video{title='Against the Odds Final Day', description='null', broadcastId='11514936512', tagList='', id='a582145870', recordedAt=java.util.GregorianCalendar[time=?,areFieldsSet=false,areAllFieldsSet=true,lenient=true,zone=sun.util.calendar.ZoneInfo[id=\"Europe/Berlin\",offset=3600000,dstSavings=3600000,useDaylight=true,transitions=143,lastRule=java.util.SimpleTimeZone[id=Europe/Berlin,offset=3600000,dstSavings=3600000,useDaylight=true,startYear=0,startMode=2,startMonth=2,startDay=-1,startDayOfWeek=1,startTime=3600000,startTimeMode=2,endMode=2,endMonth=9,endDay=-1,endDayOfWeek=1,endTime=3600000,endTimeMode=2]],firstDayOfWeek=2,minimalDaysInFirstWeek=4,ERA=1,YEAR=2014,MONTH=10,WEEK_OF_YEAR=45,WEEK_OF_MONTH=1,DAY_OF_MONTH=26,DAY_OF_YEAR=312,DAY_OF_WEEK=7,DAY_OF_WEEK_IN_MONTH=2,AM_PM=1,HOUR=3,HOUR_OF_DAY=13,MINUTE=0,SECOND=2,MILLISECOND=401,ZONE_OFFSET=3600000,DST_OFFSET=0], game='StarCraft II: Heart of the Swarm', length=19470, preview=http://static-cdn.jtvnw.net/jtv.thumbs/archive-582145870-320x240.jpg, url=http://www.twitch.tv/taketv/b/582145870, views=0, channelLink=null, selfLink=null, channelName='taketv', channelDisplayName='TaKeTV', videoParts={source=[VideoPart{url=http://media-cdn.twitch.tv/store47.media47/archives/2014-10-26/live_user_taketv_1414328428.flv, length=1710, vodCountUrl=http://countess.twitch.tv/ping.gif?u=%7B%22type%22%3A%22vod%22%2C%22id%22%3A5821458704%7D, upkeep='null'}, VideoPart{url=http://media-cdn.twitch.tv/store152.media101/archives/2014-10-26/live_user_taketv_1414330139.flv, length=1711, vodCountUrl=http://countess.twitch.tv/ping.gif?u=%7B%22type%22%3A%22vod%22%2C%22id%22%3A5821524844%7D, upkeep='null'}, VideoPart{url=http://media-cdn.twitch.tv/store151.media100/archives/2014-10-26/live_user_taketv_1414331850.flv, length=1710, vodCountUrl=http://countess.twitch.tv/ping.gif?u=%7B%22type%22%3A%22vod%22%2C%22id%22%3A5821591034%7D, upkeep='null'}, VideoPart{url=http://media-cdn.twitch.tv/store89.media66/archives/2014-10-26/live_user_taketv_1414333561.flv, length=1711, vodCountUrl=http://countess.twitch.tv/ping.gif?u=%7B%22type%22%3A%22vod%22%2C%22id%22%3A5821658414%7D, upkeep='null'}, VideoPart{url=http://media-cdn.twitch.tv/store157.media103/archives/2014-10-26/live_user_taketv_1414335273.flv, length=1710, vodCountUrl=http://countess.twitch.tv/ping.gif?u=%7B%22type%22%3A%22vod%22%2C%22id%22%3A5821726934%7D, upkeep='null'}, VideoPart{url=http://media-cdn.twitch.tv/store121.media86/archives/2014-10-26/live_user_taketv_1414336983.flv, length=1711, vodCountUrl=http://countess.twitch.tv/ping.gif?u=%7B%22type%22%3A%22vod%22%2C%22id%22%3A5821798234%7D, upkeep='null'}, VideoPart{url=http://media-cdn.twitch.tv/store137.media93/archives/2014-10-26/live_user_taketv_1414338695.flv, length=1711, vodCountUrl=http://countess.twitch.tv/ping.gif?u=%7B%22type%22%3A%22vod%22%2C%22id%22%3A5821870584%7D, upkeep='null'}, VideoPart{url=http://media-cdn.twitch.tv/store152.media101/archives/2014-10-26/live_user_taketv_1414340406.flv, length=1711, vodCountUrl=http://countess.twitch.tv/ping.gif?u=%7B%22type%22%3A%22vod%22%2C%22id%22%3A5821944864%7D, upkeep='null'}, VideoPart{url=http://media-cdn.twitch.tv/store75.media59/archives/2014-10-26/live_user_taketv_1414342118.flv, length=1710, vodCountUrl=http://countess.twitch.tv/ping.gif?u=%7B%22type%22%3A%22vod%22%2C%22id%22%3A5822023674%7D, upkeep='null'}, VideoPart{url=http://media-cdn.twitch.tv/store153.media101/archives/2014-10-26/live_user_taketv_1414343828.flv, length=1711, vodCountUrl=http://countess.twitch.tv/ping.gif?u=%7B%22type%22%3A%22vod%22%2C%22id%22%3A5822104334%7D, upkeep='null'}, VideoPart{url=http://media-cdn.twitch.tv/store72.media53/archives/2014-10-26/live_user_taketv_1414345539.flv, length=1710, vodCountUrl=http://countess.twitch.tv/ping.gif?u=%7B%22type%22%3A%22vod%22%2C%22id%22%3A5822187634%7D, upkeep='null'}, VideoPart{url=http://media-cdn.twitch.tv/store151.media100/archives/2014-10-26/live_user_taketv_1414347249.flv, length=654, vodCountUrl=http://countess.twitch.tv/ping.gif?u=%7B%22type%22%3A%22vod%22%2C%22id%22%3A5822218924%7D, upkeep='null'}], high=[VideoPart{url=http://media-cdn.twitch.tv/store48.media48/archives/2014-10-26/format_720p_582145870.flv, length=1710, vodCountUrl=http://countess.twitch.tv/ping.gif?u=%7B%22type%22%3A%22vod%22%2C%22id%22%3A5821458703%7D, upkeep='null'}, VideoPart{url=http://media-cdn.twitch.tv/store164.media107/archives/2014-10-26/format_720p_582152484.flv, length=1711, vodCountUrl=http://countess.twitch.tv/ping.gif?u=%7B%22type%22%3A%22vod%22%2C%22id%22%3A5821524843%7D, upkeep='null'}, VideoPart{url=http://media-cdn.twitch.tv/store60.media52/archives/2014-10-26/format_720p_582159103.flv, length=1710, vodCountUrl=http://countess.twitch.tv/ping.gif?u=%7B%22type%22%3A%22vod%22%2C%22id%22%3A5821591033%7D, upkeep='null'}, VideoPart{url=http://media-cdn.twitch.tv/store128.media89/archives/2014-10-26/format_720p_582165841.flv, length=1711, vodCountUrl=http://countess.twitch.tv/ping.gif?u=%7B%22type%22%3A%22vod%22%2C%22id%22%3A5821658413%7D, upkeep='null'}, VideoPart{url=http://media-cdn.twitch.tv/store131.media90/archives/2014-10-26/format_720p_582172693.flv, length=1710, vodCountUrl=http://countess.twitch.tv/ping.gif?u=%7B%22type%22%3A%22vod%22%2C%22id%22%3A5821726933%7D, upkeep='null'}, VideoPart{url=http://media-cdn.twitch.tv/store161.media105/archives/2014-10-26/format_720p_582179823.flv, length=1711, vodCountUrl=http://countess.twitch.tv/ping.gif?u=%7B%22type%22%3A%22vod%22%2C%22id%22%3A5821798233%7D, upkeep='null'}, VideoPart{url=http://media-cdn.twitch.tv/store108.media72/archives/2014-10-26/format_720p_582187058.flv, length=1711, vodCountUrl=http://countess.twitch.tv/ping.gif?u=%7B%22type%22%3A%22vod%22%2C%22id%22%3A5821870583%7D, upkeep='null'}, VideoPart{url=http://media-cdn.twitch.tv/store26.media82/archives/2014-10-26/format_720p_582194486.flv, length=1711, vodCountUrl=http://countess.twitch.tv/ping.gif?u=%7B%22type%22%3A%22vod%22%2C%22id%22%3A5821944863%7D, upkeep='null'}, VideoPart{url=http://media-cdn.twitch.tv/store121.media86/archives/2014-10-26/format_720p_582202367.flv, length=1710, vodCountUrl=http://countess.twitch.tv/ping.gif?u=%7B%22type%22%3A%22vod%22%2C%22id%22%3A5822023673%7D, upkeep='null'}, VideoPart{url=http://media-cdn.twitch.tv/store132.media91/archives/2014-10-26/format_720p_582210433.flv, length=1711, vodCountUrl=http://countess.twitch.tv/ping.gif?u=%7B%22type%22%3A%22vod%22%2C%22id%22%3A5822104333%7D, upkeep='null'}, VideoPart{url=http://media-cdn.twitch.tv/store68.media56/archives/2014-10-26/format_720p_582218763.flv, length=1710, vodCountUrl=http://countess.twitch.tv/ping.gif?u=%7B%22type%22%3A%22vod%22%2C%22id%22%3A5822187633%7D, upkeep='null'}, VideoPart{url=http://media-cdn.twitch.tv/store141.media95/archives/2014-10-26/format_720p_582221892.flv, length=654, vodCountUrl=http://countess.twitch.tv/ping.gif?u=%7B%22type%22%3A%22vod%22%2C%22id%22%3A5822218923%7D, upkeep='null'}], mid=[VideoPart{url=http://media-cdn.twitch.tv/store135.media92/archives/2014-10-26/format_480p_582145870.flv, length=1710, vodCountUrl=http://countess.twitch.tv/ping.gif?u=%7B%22type%22%3A%22vod%22%2C%22id%22%3A5821458702%7D, upkeep='null'}, VideoPart{url=http://media-cdn.twitch.tv/store82.media62/archives/2014-10-26/format_480p_582152484.flv, length=1711, vodCountUrl=http://countess.twitch.tv/ping.gif?u=%7B%22type%22%3A%22vod%22%2C%22id%22%3A5821524842%7D, upkeep='null'}, VideoPart{url=http://media-cdn.twitch.tv/store148.media99/archives/2014-10-26/format_480p_582159103.flv, length=1710, vodCountUrl=http://countess.twitch.tv/ping.gif?u=%7B%22type%22%3A%22vod%22%2C%22id%22%3A5821591032%7D, upkeep='null'}, VideoPart{url=http://media-cdn.twitch.tv/store132.media91/archives/2014-10-26/format_480p_582165841.flv, length=1711, vodCountUrl=http://countess.twitch.tv/ping.gif?u=%7B%22type%22%3A%22vod%22%2C%22id%22%3A5821658412%7D, upkeep='null'}, VideoPart{url=http://media-cdn.twitch.tv/store113.media74/archives/2014-10-26/format_480p_582172693.flv, length=1710, vodCountUrl=http://countess.twitch.tv/ping.gif?u=%7B%22type%22%3A%22vod%22%2C%22id%22%3A5821726932%7D, upkeep='null'}, VideoPart{url=http://media-cdn.twitch.tv/store23.media78/archives/2014-10-26/format_480p_582179823.flv, length=1711, vodCountUrl=http://countess.twitch.tv/ping.gif?u=%7B%22type%22%3A%22vod%22%2C%22id%22%3A5821798232%7D, upkeep='null'}, VideoPart{url=http://media-cdn.twitch.tv/store42.media42/archives/2014-10-26/format_480p_582187058.flv, length=1711, vodCountUrl=http://countess.twitch.tv/ping.gif?u=%7B%22type%22%3A%22vod%22%2C%22id%22%3A5821870582%7D, upkeep='null'}, VideoPart{url=http://media-cdn.twitch.tv/store110.media73/archives/2014-10-26/format_480p_582194486.flv, length=1711, vodCountUrl=http://countess.twitch.tv/ping.gif?u=%7B%22type%22%3A%22vod%22%2C%22id%22%3A5821944862%7D, upkeep='null'}, VideoPart{url=http://media-cdn.twitch.tv/store114.media79/archives/2014-10-26/format_480p_582202367.flv, length=1710, vodCountUrl=http://countess.twitch.tv/ping.gif?u=%7B%22type%22%3A%22vod%22%2C%22id%22%3A5822023672%7D, upkeep='null'}, VideoPart{url=http://media-cdn.twitch.tv/store134.media92/archives/2014-10-26/format_480p_582210433.flv, length=1711, vodCountUrl=http://countess.twitch.tv/ping.gif?u=%7B%22type%22%3A%22vod%22%2C%22id%22%3A5822104332%7D, upkeep='null'}, VideoPart{url=http://media-cdn.twitch.tv/store163.media106/archives/2014-10-26/format_480p_582218763.flv, length=1710, vodCountUrl=http://countess.twitch.tv/ping.gif?u=%7B%22type%22%3A%22vod%22%2C%22id%22%3A5822187632%7D, upkeep='null'}, VideoPart{url=http://media-cdn.twitch.tv/store86.media64/archives/2014-10-26/format_480p_582221892.flv, length=654, vodCountUrl=http://countess.twitch.tv/ping.gif?u=%7B%22type%22%3A%22vod%22%2C%22id%22%3A5822218922%7D, upkeep='null'}], low=[VideoPart{url=http://media-cdn.twitch.tv/store159.media104/archives/2014-10-26/format_360p_582145870.flv, length=1710, vodCountUrl=http://countess.twitch.tv/ping.gif?u=%7B%22type%22%3A%22vod%22%2C%22id%22%3A5821458701%7D, upkeep='null'}, VideoPart{url=http://media-cdn.twitch.tv/store68.media56/archives/2014-10-26/format_360p_582152484.flv, length=1711, vodCountUrl=http://countess.twitch.tv/ping.gif?u=%7B%22type%22%3A%22vod%22%2C%22id%22%3A5821524841%7D, upkeep='null'}, VideoPart{url=http://media-cdn.twitch.tv/store134.media92/archives/2014-10-26/format_360p_582159103.flv, length=1710, vodCountUrl=http://countess.twitch.tv/ping.gif?u=%7B%22type%22%3A%22vod%22%2C%22id%22%3A5821591031%7D, upkeep='null'}, VideoPart{url=http://media-cdn.twitch.tv/store69.media57/archives/2014-10-26/format_360p_582165841.flv, length=1711, vodCountUrl=http://countess.twitch.tv/ping.gif?u=%7B%22type%22%3A%22vod%22%2C%22id%22%3A5821658411%7D, upkeep='null'}, VideoPart{url=http://media-cdn.twitch.tv/store148.media99/archives/2014-10-26/format_360p_582172693.flv, length=1710, vodCountUrl=http://countess.twitch.tv/ping.gif?u=%7B%22type%22%3A%22vod%22%2C%22id%22%3A5821726931%7D, upkeep='null'}, VideoPart{url=http://media-cdn.twitch.tv/store154.media102/archives/2014-10-26/format_360p_582179823.flv, length=1711, vodCountUrl=http://countess.twitch.tv/ping.gif?u=%7B%22type%22%3A%22vod%22%2C%22id%22%3A5821798231%7D, upkeep='null'}, VideoPart{url=http://media-cdn.twitch.tv/store142.media96/archives/2014-10-26/format_360p_582187058.flv, length=1711, vodCountUrl=http://countess.twitch.tv/ping.gif?u=%7B%22type%22%3A%22vod%22%2C%22id%22%3A5821870581%7D, upkeep='null'}, VideoPart{url=http://media-cdn.twitch.tv/store52.media50/archives/2014-10-26/format_360p_582194486.flv, length=1711, vodCountUrl=http://countess.twitch.tv/ping.gif?u=%7B%22type%22%3A%22vod%22%2C%22id%22%3A5821944861%7D, upkeep='null'}, VideoPart{url=http://media-cdn.twitch.tv/store60.media52/archives/2014-10-26/format_360p_582202367.flv, length=1710, vodCountUrl=http://countess.twitch.tv/ping.gif?u=%7B%22type%22%3A%22vod%22%2C%22id%22%3A5822023671%7D, upkeep='null'}, VideoPart{url=http://media-cdn.twitch.tv/store77.media60/archives/2014-10-26/format_360p_582210433.flv, length=1711, vodCountUrl=http://countess.twitch.tv/ping.gif?u=%7B%22type%22%3A%22vod%22%2C%22id%22%3A5822104331%7D, upkeep='null'}, VideoPart{url=http://media-cdn.twitch.tv/store121.media86/archives/2014-10-26/format_360p_582218763.flv, length=1710, vodCountUrl=http://countess.twitch.tv/ping.gif?u=%7B%22type%22%3A%22vod%22%2C%22id%22%3A5822187631%7D, upkeep='null'}, VideoPart{url=http://media-cdn.twitch.tv/store63.media54/archives/2014-10-26/format_360p_582221892.flv, length=654, vodCountUrl=http://countess.twitch.tv/ping.gif?u=%7B%22type%22%3A%22vod%22%2C%22id%22%3A5822218921%7D, upkeep='null'}], mobile=[VideoPart{url=http://media-cdn.twitch.tv/store146.media98/archives/2014-10-26/format_240p_582145870.flv, length=1710, vodCountUrl=http://countess.twitch.tv/ping.gif?u=%7B%22type%22%3A%22vod%22%2C%22id%22%3A5821458700%7D, upkeep='null'}, VideoPart{url=http://media-cdn.twitch.tv/store89.media66/archives/2014-10-26/format_240p_582152484.flv, length=1711, vodCountUrl=http://countess.twitch.tv/ping.gif?u=%7B%22type%22%3A%22vod%22%2C%22id%22%3A5821524840%7D, upkeep='null'}, VideoPart{url=http://media-cdn.twitch.tv/store143.media96/archives/2014-10-26/format_240p_582159103.flv, length=1710, vodCountUrl=http://countess.twitch.tv/ping.gif?u=%7B%22type%22%3A%22vod%22%2C%22id%22%3A5821591030%7D, upkeep='null'}, VideoPart{url=http://media-cdn.twitch.tv/store104.media69/archives/2014-10-26/format_240p_582165841.flv, length=1711, vodCountUrl=http://countess.twitch.tv/ping.gif?u=%7B%22type%22%3A%22vod%22%2C%22id%22%3A5821658410%7D, upkeep='null'}, VideoPart{url=http://media-cdn.twitch.tv/store154.media102/archives/2014-10-26/format_240p_582172693.flv, length=1710, vodCountUrl=http://countess.twitch.tv/ping.gif?u=%7B%22type%22%3A%22vod%22%2C%22id%22%3A5821726930%7D, upkeep='null'}, VideoPart{url=http://media-cdn.twitch.tv/store64.media54/archives/2014-10-26/format_240p_582179823.flv, length=1711, vodCountUrl=http://countess.twitch.tv/ping.gif?u=%7B%22type%22%3A%22vod%22%2C%22id%22%3A5821798230%7D, upkeep='null'}, VideoPart{url=http://media-cdn.twitch.tv/store86.media64/archives/2014-10-26/format_240p_582187058.flv, length=1711, vodCountUrl=http://countess.twitch.tv/ping.gif?u=%7B%22type%22%3A%22vod%22%2C%22id%22%3A5821870580%7D, upkeep='null'}, VideoPart{url=http://media-cdn.twitch.tv/store44.media44/archives/2014-10-26/format_240p_582194486.flv, length=1711, vodCountUrl=http://countess.twitch.tv/ping.gif?u=%7B%22type%22%3A%22vod%22%2C%22id%22%3A5821944860%7D, upkeep='null'}, VideoPart{url=http://media-cdn.twitch.tv/store163.media106/archives/2014-10-26/format_240p_582202367.flv, length=1710, vodCountUrl=http://countess.twitch.tv/ping.gif?u=%7B%22type%22%3A%22vod%22%2C%22id%22%3A5822023670%7D, upkeep='null'}, VideoPart{url=http://media-cdn.twitch.tv/store117.media81/archives/2014-10-26/format_240p_582210433.flv, length=1711, vodCountUrl=http://countess.twitch.tv/ping.gif?u=%7B%22type%22%3A%22vod%22%2C%22id%22%3A5822104330%7D, upkeep='null'}, VideoPart{url=http://media-cdn.twitch.tv/store140.media95/archives/2014-10-26/format_240p_582218763.flv, length=1710, vodCountUrl=http://countess.twitch.tv/ping.gif?u=%7B%22type%22%3A%22vod%22%2C%22id%22%3A5822187630%7D, upkeep='null'}, VideoPart{url=http://media-cdn.twitch.tv/store81.media62/archives/2014-10-26/format_240p_582221892.flv, length=654, vodCountUrl=http://countess.twitch.tv/ping.gif?u=%7B%22type%22%3A%22vod%22%2C%22id%22%3A5822218920%7D, upkeep='null'}]}}";
        Video video = new TwitchVideo("586266937");


        URL takeTvWcsFinals2014 = null;
        try {
            takeTvWcsFinals2014 = new URL("http://www.twitch.tv/taketv/b/586266937");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        // http://www.twitch.tv/taketv/c/4668355



        Video video2 = new TwitchVideo(takeTvWcsFinals2014);

        video.toString().equals(video2.toString());
        //org.junit.Assert.assertEquals("getPastBroadcastById", expectedResult, video.toString());
        //org.junit.Assert.assertEquals("getPastBroadcastByUrl", expectedResult, video2.toString());
        //org.junit.Assert.assertEquals("Should be true", video.toString(), video2.toString());






        System.out.println(video);
    }


}
