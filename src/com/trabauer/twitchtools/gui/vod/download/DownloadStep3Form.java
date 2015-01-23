package com.trabauer.twitchtools.gui.vod.download;

import com.trabauer.twitchtools.gui.MyGuiFormInterface;
import com.trabauer.twitchtools.gui.vod.download.DownloadProgressPanel;
import com.trabauer.twitchtools.gui.vod.download.OverallProgressPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Flo on 16.11.2014.
 */
public class DownloadStep3Form implements MyGuiFormInterface {
    private JPanel mainPanel;
    private JPanel downloadProgressOverviewPanel;
    private JPanel overallPanel;


    public DownloadStep3Form() {
        super();
        downloadProgressOverviewPanel.setLayout(new BoxLayout(downloadProgressOverviewPanel, BoxLayout.PAGE_AXIS));
        // nextButton.setActionCommand("nextButton3");
    }

    @Override
    public JPanel getMainPanel() {
        return mainPanel;
    }

    public void addDownloadProgressPanel(DownloadProgressPanel downloadProgressPanel) {
        downloadProgressOverviewPanel.add(downloadProgressPanel);
    }

    public void addOverallProgressPanel(OverallProgressPanel overallProgressPanel) {
        overallPanel.add(overallProgressPanel);
    }


}
