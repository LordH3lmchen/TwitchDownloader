package com.trabauer.twitchtools.gui.vod.download;

import com.trabauer.twitchtools.gui.myGuiForm;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Flo on 16.11.2014.
 */
public class DownloadStep3 implements myGuiForm {
    private JPanel mainPanel;
    private JButton nextButton;
    private JPanel downloadProgressOverviewPanel;
    private JPanel overallPanel;


    public DownloadStep3() {
        super();
        downloadProgressOverviewPanel.setLayout(new BoxLayout(downloadProgressOverviewPanel, BoxLayout.PAGE_AXIS));
        nextButton.setActionCommand("nextButton3");
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }

    @Override
    public JPanel getMainPanel() {
        return mainPanel;
    }

    public void addDownloadProgressPanel(DownloadProgressPanel downloadProgressPanel) {
        downloadProgressOverviewPanel.add(downloadProgressPanel);
    }

    public void addOverallProgressPanel(OverallProgressPanel overallProgressPanel) {
        overallProgressPanel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(e.getActionCommand().equals("DONE")) {
                    nextButton.setEnabled(true);
                } else if (e.getActionCommand().equals("STARTED")) {
                    nextButton.setEnabled(false);
                }
            }
        });
        overallPanel.add(overallProgressPanel);
    }


    public void addActionListener(ActionListener listener) {
        nextButton.addActionListener(listener);
    }
}
