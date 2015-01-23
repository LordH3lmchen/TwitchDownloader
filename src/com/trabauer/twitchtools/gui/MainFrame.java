package com.trabauer.twitchtools.gui;

import com.trabauer.twitchtools.gui.images.TwitchToolsImages;
import com.trabauer.twitchtools.gui.vod.download.DownloadMainPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;

/**
 * Created by Flo on 16.01.2015.
 */
public class MainFrame extends JFrame {

    private MainFramePanel mainFramePanel;




    public MainFrame() throws HeadlessException {
        super("TwitchTools");

        Image icon = TwitchToolsImages.getTwitchDownloadToolImage();
        setIconImage(icon);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setSize(700, 550);
        setVisible(true);
    }

}
