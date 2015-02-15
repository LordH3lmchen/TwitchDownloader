package com.trabauer.twitchtools.gui;

import com.trabauer.twitchtools.gui.images.TwitchToolsImages;

import javax.swing.*;
import java.awt.*;

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
