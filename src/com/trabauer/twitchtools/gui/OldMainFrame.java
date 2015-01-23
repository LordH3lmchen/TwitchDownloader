package com.trabauer.twitchtools.gui;

import com.trabauer.twitchtools.gui.vod.download.DownloadStep1Form;
import com.trabauer.twitchtools.gui.vod.download.DownloadStep2Form;
import com.trabauer.twitchtools.gui.vod.download.DownloadStep3Form;
import com.trabauer.twitchtools.gui.vod.download.DownloadStep4Form;
import com.trabauer.twitchtools.utils.OsUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * Created by Flo on 13.11.2014.
 */
public class OldMainFrame extends JFrame {

    private DownloadStep1Form downloadStep1Form;
    private DownloadStep2Form downloadStep2Form;
    private DownloadStep3Form downloadStep3Form;
    private DownloadStep4Form downloadStep4Form;

    public final String DOWNLOAD1CARD = "DownloadStep1Form";
    public final String DOWNLOAD2CARD = "DownloadStep2Form";
    public final String DOWNLOAD3CARD = "DownloadStep3Form";
    public final String DOWNLOAD4CARD = "DownloadStep4Form";


    private JPanel mainFramePanel;
    private JFileChooser fileChooser;



    public OldMainFrame(){
        super("Twitch - Past Broadcast Loader");

        mainFramePanel = (JPanel) getContentPane();
        //mainFramePanel = new JPanel(new CardLayout())
        mainFramePanel.setLayout(new CardLayout(5, 5));


        //mainFramePanel.

//        mainForm = new MainForm(controller);
//        mainForm.addMainFormListener(controller);


        Image icon = null;
        try {
            icon = ImageIO.read(getClass().getResource("twitchTool.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        setIconImage(icon);

        downloadStep1Form = new DownloadStep1Form(icon);
        downloadStep2Form = new DownloadStep2Form();
        downloadStep3Form = new DownloadStep3Form();
        downloadStep4Form = new DownloadStep4Form();

        mainFramePanel.add(downloadStep1Form.getMainPanel(), DOWNLOAD1CARD);
        mainFramePanel.add(downloadStep2Form.getMainPanel(), DOWNLOAD2CARD);
        mainFramePanel.add(downloadStep3Form.getMainPanel(), DOWNLOAD3CARD);
        mainFramePanel.add(downloadStep4Form.getMainPanel(), DOWNLOAD4CARD);


//        add(mainForm.getMainPanel());


        setMinimumSize(new Dimension(460, 180));
        setSize(1500,1024);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);

    }


    public void showNextCard() {
        CardLayout cardLayout = (CardLayout)mainFramePanel.getLayout();
        cardLayout.next(mainFramePanel);
    }

    public void showFirstStep() {
        showCard(DOWNLOAD1CARD);
    }

    public void showCard(String cardname) {
        CardLayout cardLayout = (CardLayout)mainFramePanel.getLayout();
        cardLayout.show(mainFramePanel, cardname);
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

    public DownloadStep1Form getDownloadStep1Form() {
        return downloadStep1Form;
    }

    public DownloadStep2Form getDownloadStep2Form() {
        return downloadStep2Form;
    }

    public DownloadStep3Form getDownloadStep3Form() {
        return downloadStep3Form;
    }

    public DownloadStep4Form getDownloadStep4Form() {
        return downloadStep4Form;
    }
}
