package com.trabauer.twitchtools.controller.channelsync;

import com.google.gson.Gson;
import com.trabauer.twitchtools.gui.vod.channelsync.SyncChannelMainPanel;
import com.trabauer.twitchtools.model.twitch.TwitchVideoInfo;
import com.trabauer.twitchtools.model.twitch.TwitchVideoInfoList;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by Flo on 20.01.2015.
 */
public class ChannelSyncController implements ChannelSyncControllerInterface {

    private final SyncChannelMainPanel mainPanel;
    private final TwitchVideoInfoList twitchVideoInfoList;

    public ChannelSyncController(TwitchVideoInfoList twitchVideoInfoList) {
        this.twitchVideoInfoList = twitchVideoInfoList;
        this.mainPanel = new SyncChannelMainPanel(this, twitchVideoInfoList);
    }

    @Override
    public JPanel getMainPanel() {
        return mainPanel;
    }

    @Override
    public void searchFldText(String text) {
        try {
//            twitchVideoInfoList.update(text);
            twitchVideoInfoList.update(text, true, 40, 0);
        } catch (MalformedURLException e) {
            e.printStackTrace(); //TODO show Error Dialog
        }
    }

    @Override
    public void openUrlInBrowser(URL url) {
        try {
            Desktop.getDesktop().browse(url.toURI());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void loadMoreSearchResults() {
        twitchVideoInfoList.loadMore();
    }

    @Override
    public void downloadTwitchVideo(TwitchVideoInfo videoInfo) {

    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

    }



}
