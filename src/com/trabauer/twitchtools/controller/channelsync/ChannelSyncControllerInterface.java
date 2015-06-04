package com.trabauer.twitchtools.controller.channelsync;
import com.trabauer.twitchtools.model.twitch.TwitchVideoInfo;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Flo on 22.01.2015.
 */
public interface ChannelSyncControllerInterface extends ActionListener, PropertyChangeListener{

    public JPanel getMainPanel();

    void searchFldText(String text, boolean pastBroadcasts) throws IOException;

    void openUrlInBrowser(URL url);

    void loadMoreSearchResults();

    void downloadTwitchVideo(TwitchVideoInfo videoInfo);

    void selectMostRecent(Integer value);

    void downloadAllSelectedTwitchVideos();

    void convert2mp4(TwitchVideoInfo relatedTwitchVideoInfoObject);

    void delete(TwitchVideoInfo relatedTwitchVideoInfoObject);
}
