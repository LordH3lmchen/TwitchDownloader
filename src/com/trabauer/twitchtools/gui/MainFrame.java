package com.trabauer.twitchtools.gui;

import com.trabauer.twitchtools.controller.Controller;
import com.trabauer.twitchtools.utils.OsUtils;
import com.trabauer.twitchtools.utils.OsValidator;
import com.trabauer.twitchtools.utils.UnsupportedOsException;

import javax.smartcardio.Card;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by Flo on 13.11.2014.
 */
public class MainFrame extends JFrame {


    private MainForm mainForm;
    private DownloadStep1 downloadStep1Form;
    private DownloadStep2 downloadStep2Form;
    private DownloadStep3 downloadStep3Form;

    private final String DOWNLOAD1CARD = "DownloadStep1";
    private final String DOWNLOAD2CARD = "DownloadStep2";
    private final String DOWNLOAD3CARD = "DownloadStep3";


    private JPanel mainFramePanel;
    private JFileChooser fileChooser;



    public MainFrame (){
        super("Twitch - Past Broadcast Loader");

        mainFramePanel = (JPanel) getContentPane();
        //mainFramePanel = new JPanel(new CardLayout())
        mainFramePanel.setLayout(new CardLayout(5, 5));

        //mainFramePanel.

//        mainForm = new MainForm(controller);
//        mainForm.addMainFormListener(controller);


        downloadStep1Form = new DownloadStep1();
        downloadStep2Form = new DownloadStep2();
        downloadStep3Form = new DownloadStep3();

        mainFramePanel.add(downloadStep1Form.getMainPanel(), DOWNLOAD1CARD);
        mainFramePanel.add(downloadStep2Form.getMainPanel(), DOWNLOAD2CARD);
        mainFramePanel.add(downloadStep3Form.getMainPanel(), DOWNLOAD3CARD);


//        add(mainForm.getMainPanel());

        setMinimumSize(new Dimension(460, 180));
        setSize(460,180);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);

    }


    public void showNextCard() {
        CardLayout cardLayout = (CardLayout)mainFramePanel.getLayout();
        cardLayout.next(mainFramePanel);
    }

    public void showFirstStep() {
        CardLayout cardLayout = (CardLayout)mainFramePanel.getLayout();
        cardLayout.show(mainFramePanel, DOWNLOAD1CARD);
    }

    public void showPreviousCard() {
        CardLayout cardLayout = (CardLayout)mainFramePanel.getLayout();
        cardLayout.previous(mainFramePanel);
    }

    public File showDestinationDirChooser() {
        return showDestinationDirChooser(OsUtils.getUserHome());
    }

    public File showDestinationDirChooser(String path) {
        fileChooser = null;
        File file = null;

        fileChooser = new JFileChooser(path);

        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if(fileChooser != null) {
            int returnVal = fileChooser.showOpenDialog(this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                file = fileChooser.getSelectedFile();
            }


        }
        return file;
    }

    public DownloadStep1 getDownloadStep1Form() {
        return downloadStep1Form;
    }

    public DownloadStep2 getDownloadStep2Form() {
        return downloadStep2Form;
    }

    public DownloadStep3 getDownloadStep3Form() {
        return downloadStep3Form;
    }
}
