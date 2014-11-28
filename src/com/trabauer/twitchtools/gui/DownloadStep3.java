package com.trabauer.twitchtools.gui;

import com.trabauer.twitchtools.controller.Controller;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Flo on 16.11.2014.
 */
public class DownloadStep3 implements myGuiForm{
    private JPanel mainPanel;
    private JButton finishButton;
    private JPanel downloadProgressOverviewPanel;
    private JPanel overallPanel;


    public DownloadStep3() {
        super();
        downloadProgressOverviewPanel.setLayout(new BoxLayout(downloadProgressOverviewPanel, BoxLayout.PAGE_AXIS));
        finishButton.setActionCommand("finishButton");
        finishButton.addActionListener(new ActionListener() {
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
                    finishButton.setEnabled(true);
                } else if (e.getActionCommand().equals("STARTED")) {
                    finishButton.setEnabled(false);
                }
            }
        });
        overallPanel.add(overallProgressPanel);
    }


    public void addActionListener(ActionListener listener) {
        finishButton.addActionListener(listener);
    }
}
