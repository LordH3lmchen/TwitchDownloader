package com.trabauer.twitchtools.gui;

import com.trabauer.twitchtools.controller.Controller;
import com.trabauer.twitchtools.model.FilenamePatternsComboBoxModel;
import com.trabauer.twitchtools.model.VideoQualityComboBoxModel;
import com.trabauer.twitchtools.model.twitch.TwitchVideo;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Flo on 16.11.2014.
 */
public class DownloadStep2 implements Observer, ChangeListener {
    private JPanel mainPanel;
    private JTextField destFolderTextField;
    private JComboBox filenameComboBox;
    private JLabel fileNamePreviewLabel;
    private JButton nextButton;
    private JButton selectDestinationFolderButton;
    private JButton backButton;
    private JComboBox qualityComboBox;
    private JSpinner downloadThreadsSpinner;
    private JLabel bandWithUsageJLabel;

    private JFileChooser fileChooser;
    private SpinnerNumberModel threadCountSpinnerNumberModel;

    private ArrayList<ActionListener> actionListeners;


    public DownloadStep2() {
        super();
        this.actionListeners = new ArrayList<ActionListener>();


        nextButton.setActionCommand("nextButton2");
        backButton.setActionCommand("backButton2");
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

    public String getDestinationFolder() {
        return destFolderTextField.getText();
    }

    public void addActionListener(Controller controller) {
        this.actionListeners.add(controller);
        nextButton.addActionListener(controller);
        backButton.addActionListener(controller);
        selectDestinationFolderButton.addActionListener(controller);
        filenameComboBox.addActionListener(controller);
        qualityComboBox.addActionListener(controller);
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
        if(o.getClass().equals(TwitchVideo.class)) {
            TwitchVideo video = (TwitchVideo) o;
            String quality = video.getBestAvailableQuality();
            int partcount = video.getTwitchVideoParts(quality).size();
            threadCountSpinnerNumberModel.setMaximum(Math.min(25, partcount));
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
