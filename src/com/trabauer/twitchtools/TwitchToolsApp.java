package com.trabauer.twitchtools;

import com.trabauer.twitchtools.controller.channelsync.ChannelSyncController;
import com.trabauer.twitchtools.gui.images.TwitchToolsImages;
import com.trabauer.twitchtools.gui.vod.channelsync.ChannelSyncMenuBar;

import javax.swing.*;
import java.awt.*;
import java.net.URISyntaxException;

/**
 * Created by Flo on 12.11.2014.
 */
public class TwitchToolsApp{


    public static void main (String[] args) throws URISyntaxException {

        // Set look & feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ChannelSyncController controller = new ChannelSyncController();
            }
        });


    }




}
