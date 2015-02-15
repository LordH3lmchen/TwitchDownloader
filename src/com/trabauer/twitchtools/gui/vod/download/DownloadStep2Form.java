package com.trabauer.twitchtools.gui.vod.download;

import com.trabauer.twitchtools.controller.DownloadController;
import com.trabauer.twitchtools.model.FilenamePatternsComboBoxModel;
import com.trabauer.twitchtools.model.VideoQualityComboBoxModel;
import com.trabauer.twitchtools.model.twitch.TwitchVideoInfo;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Flo on 16.11.2014.
 */
public class DownloadStep2Form implements Observer, ChangeListener {
    private JPanel mainPanel;
    private JTextField destFolderTextField;
    private JComboBox filenameComboBox;
    private JLabel fileNamePreviewLabel;
    private JButton selectDestinationFolderButton;
    private JComboBox qualityComboBox;
    private JSpinner downloadThreadsSpinner;
    private JLabel bandWithUsageJLabel;

    private JFileChooser fileChooser;
    private SpinnerNumberModel threadCountSpinnerNumberModel;

    private ArrayList<ActionListener> actionListeners;


    public DownloadStep2Form() {
        super();
        this.actionListeners = new ArrayList<ActionListener>();


        //nextButton.setActionCommand("nextButton2");
        //backButton.setActionCommand("backButton2");
        selectDestinationFolderButton.setActionCommand("selectDestDirBtnPressed");
        filenameComboBox.setActionCommand("filenameChanged");
        qualityComboBox.setActionCommand("qualityChanged");

        threadCountSpinnerNumberModel = new SpinnerNumberModel(1, 1, 20, 1);

        downloadThreadsSpinner.setModel(threadCountSpinnerNumberModel);
        downloadThreadsSpinner.addChangeListener(this);


    }

    public JPanel getMainPanel() {
        return mainPanel;
    }


    public void setFilenamePatternsComboBoxModel(FilenamePatternsComboBoxModel filenamePatternsComboBoxModel) {
        filenameComboBox.setModel(filenamePatternsComboBoxModel);
    }

    public void setQualityComboBoxModel(VideoQualityComboBoxModel videoQualityComboBoxModel) {
        qualityComboBox.setModel(videoQualityComboBoxModel);
    }



    public void setDestFolderTextField(String s) {
        destFolderTextField.setText(s);
    }

    public File getDestinationFolder() {
        return new File(destFolderTextField.getText());
    }

    public void addActionListener(ActionListener actionListener) {
        this.actionListeners.add(actionListener);
        //nextButton.addPropertyChangeListener(actionListener);
        //backButton.addPropertyChangeListener(actionListener);
        selectDestinationFolderButton.addActionListener(actionListener);
        filenameComboBox.addActionListener(actionListener);
        qualityComboBox.addActionListener(actionListener);
    }


    public void setFileNamePreview(String filename) {
        fileNamePreviewLabel.setText(filename);
    }

    public String getFilenameSelection() {
        return filenameComboBox.getSelectedItem().toString();
    }

    public String getFilenamePreview() {
        return fileNamePreviewLabel.getText();
    }

    public String getQuality() {
        return (String)qualityComboBox.getSelectedItem();
    }

    public void setBandWithUsageJLabelText(String text) {
        bandWithUsageJLabel.setText(text);
    }

    public int getThreadCount() {
        String spinnerValue =  downloadThreadsSpinner.getValue().toString();
        return Integer.parseInt(spinnerValue);
    }


//    public


    @Override
    public void update(Observable o, Object arg) {
        if(o.getClass().equals(TwitchVideoInfo.class)) {
            TwitchVideoInfo video = (TwitchVideoInfo) o;
            String quality = null;
            try {
                quality = video.getDownloadInfo().getBestAvailableQuality();
                int partCount = video.getDownloadInfo().getTwitchBroadcastParts(quality).size();
                threadCountSpinnerNumberModel.setMaximum(Math.min(10, partCount));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void stateChanged(ChangeEvent e) {
        if(e.getSource().equals(downloadThreadsSpinner)) {
            ActionEvent ae = new ActionEvent(this, 201, "threadCountChanged");
            notifyActionListeners(ae);
        }
    }

    private void notifyActionListeners(ActionEvent ae) {
        for(ActionListener actionListener: actionListeners) {
            actionListener.actionPerformed(ae);
        }
    }

}
