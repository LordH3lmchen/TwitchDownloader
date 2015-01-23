package com.trabauer.twitchtools;

import com.trabauer.twitchtools.controller.DownloadController;
import com.trabauer.twitchtools.controller.MainController;
import com.trabauer.twitchtools.controller.channelsync.ChannelSyncController;
import com.trabauer.twitchtools.gui.MainFrame;
import com.trabauer.twitchtools.gui.images.TwitchToolsImages;
import com.trabauer.twitchtools.gui.vod.download.DownloadMainPanel;
import com.trabauer.twitchtools.model.twitch.TwitchVideoInfoList;

import javax.swing.*;
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
                JFrame mainFrame = new JFrame();
                mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                mainFrame.setIconImage(TwitchToolsImages.getTwitchDownloadToolImage());
                DownloadMainPanel downloadMainPanel = new DownloadMainPanel();
                DownloadController downloadController = new DownloadController(downloadMainPanel);
                downloadMainPanel.addPropertyChangeListener(downloadController);
                mainFrame.setContentPane(downloadMainPanel);
                mainFrame.setSize(550,450);
                mainFrame.setVisible(true);


//                DownloadController downloadController = new DownloadController();
//                MainController mainController = MainController.getInstance();
//                TwitchVideoInfoList twitchVideoInfoList = new TwitchVideoInfoList();
//                ChannelSyncController controller = new ChannelSyncController(twitchVideoInfoList);
//
//                JFrame mainFrame = new JFrame("ChannelSync");
//                mainFrame.getContentPane().add(controller.getMainPanel());
//                mainFrame.setVisible(true);
//                mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            }
        });


    }




}
