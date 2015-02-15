package com.trabauer.twitchtools.gui.vod.channelsync;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Flo on 25.01.2015.
 */
public class ChannelSyncProgressFrame extends JFrame {

    private final JTextArea consoleOutputTextArea;
    private final JScrollPane consoleOutputScrollPane;
    private final JProgressBar progressBar;
    private final JLabel currentVodLabel;
    private final JLabel progressLbl;
    private final JLabel preProgressLabel;
    private int maximum;
    private int value;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ChannelSyncProgressFrame channelSyncProgressFrame = new ChannelSyncProgressFrame();
                channelSyncProgressFrame.setVisible(true);
            }
        });
    }


    public ChannelSyncProgressFrame() throws HeadlessException {
        super("Download & Convert");
        setSize(750, 550);

        JPanel mainPanel = (JPanel) getContentPane();
        mainPanel.setLayout(new GridBagLayout());

        consoleOutputTextArea = new JTextArea();
        consoleOutputScrollPane = new JScrollPane(consoleOutputTextArea);
        currentVodLabel = new JLabel("");


        progressBar = new JProgressBar();
        progressLbl = new JLabel("100%");
        preProgressLabel = new JLabel("Progress of current Video");


        configureComponents();
        layoutComponents();



    }

    public void setDescriptionText(String text) {
        currentVodLabel.setText(text);
    }

    public void setMaximum(int maximum) {
        this.maximum = maximum;
        progressBar.setMaximum(maximum);
        recalculateProgress();
    }

    public void setValue(int value) {
        this.value = value;
        progressBar.setValue(value);
        recalculateProgress();
    }

    public void increaseValue(int value) {
        int newValue = this.value + value;
        setValue(newValue);
    }


    private void recalculateProgress() {
        int percent = (value*100)/maximum;
        progressLbl.setText(String.format("%3d %%", percent));
    }


    public void setIndeterminate(boolean value) {
        progressBar.setIndeterminate(value);
    }



    private void configureComponents() {
        progressBar.setMaximum(100);
        progressBar.setMinimum(0);
        consoleOutputTextArea.setEditable(false);
        consoleOutputScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    }

    public void addOutputText(String text) {
        consoleOutputTextArea.append(text);
    }



    private void layoutComponents() {
        GridBagConstraints c = new GridBagConstraints();

        c.gridx = 0;
        c.gridy = 0;

        c.gridwidth = 3;
        c.weightx = 1.0;
        c.weighty = 1.0;

        c.fill = GridBagConstraints.BOTH;

        add(consoleOutputScrollPane, c);

        c.gridx = 0;

        c.insets = new Insets(5,5,5,5);
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;
        c.gridy++;
        c.gridwidth = 3;
        c.weighty = 0.0;

        add(currentVodLabel, c);

        c.gridy++;
        c.gridwidth = 1;
        c.gridx = 0;
        c.weightx = 0.0;
        c.anchor = GridBagConstraints.LINE_END;
        add(preProgressLabel, c);

        c.gridx = 1;
        c.weightx = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;
        add(progressBar, c);


        c.gridx = 2;
        c.weightx = 0.0;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.LINE_START;
        add(progressLbl, c);

    }


}
