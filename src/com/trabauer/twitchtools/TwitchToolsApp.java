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

                //Old GUI
//                JFrame mainFrame = new JFrame();
//                mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//                mainFrame.setIconImage(TwitchToolsImages.getTwitchDownloadToolImage());
//                DownloadMainPanel downloadMainPanel = new DownloadMainPanel();
//                DownloadController downloadController = null;
//                try {
//                    downloadController = new DownloadController(downloadMainPanel);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                downloadMainPanel.addPropertyChangeListener(downloadController);
//                mainFrame.setContentPane(downloadMainPanel);
//                mainFrame.setSize(550,450);
//                mainFrame.setVisible(true);

//                MainController mainController = MainController.getInstance();
//                TwitchVideoInfoList twitchVideoInfoList = new TwitchVideoInfoList();


                // New GUI
                JFrame mainFrame = new JFrame("Twitch VOD Downloader");
                ChannelSyncController controller = new ChannelSyncController();
                JMenuBar mainMenuBar = new ChannelSyncMenuBar(controller, mainFrame);
                mainFrame.getContentPane().add(controller.getMainPanel());
                mainFrame.setSize(750, 550);
                mainFrame.setMinimumSize(new Dimension(550, 450));
                mainFrame.setVisible(true);
                mainFrame.setIconImage(TwitchToolsImages.getTwitchDownloadToolImage());

                mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            }
        });


    }




}
