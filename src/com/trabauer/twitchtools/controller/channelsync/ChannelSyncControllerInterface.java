package com.trabauer.twitchtools.controller.channelsync;

import com.trabauer.twitchtools.gui.vod.channelsync.SyncChannelMainPanel;
import com.trabauer.twitchtools.model.twitch.TwitchVideoInfo;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.net.URL;

/**
 * Created by Flo on 22.01.2015.
 */
public interface ChannelSyncControllerInterface extends ActionListener, PropertyChangeListener{

    public JPanel getMainPanel();

    void searchFldText(String text);

    void openUrlInBrowser(URL url);

    void loadMoreSearchResults();

    void downloadTwitchVideo(TwitchVideoInfo videoInfo);
}