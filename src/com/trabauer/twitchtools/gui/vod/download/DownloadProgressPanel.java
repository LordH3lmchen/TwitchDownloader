package com.trabauer.twitchtools.gui.vod.download;

import com.trabauer.twitchtools.controller.Controller;
import com.trabauer.twitchtools.model.twitch.TwitchVideoPart;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

/**
 * Created by Flo on 22.11.2014.
 *
 * This Panel contains the UI-stuff for one TwitchDownloadTask(thread) it listens to a DownloadTask.
 *
 */
public class DownloadProgressPanel extends JPanel implements PropertyChangeListener {

    private JLabel prefixLabel;
    private JLabel postfixLabel;
    private JProgressBar downloadProgressBar;
    private int partsToDownloadCount;

    private ArrayList<PropertyChangeListener> propertyChangeListenerArrayList;

    public DownloadProgressPanel() {
        super();
        setLayout(new FlowLayout());
        prefixLabel = new JLabel();
        postfixLabel = new JLabel();
        downloadProgressBar = new JProgressBar(0,100);
        downloadProgressBar.setValue(0);

        add(prefixLabel);
        add(downloadProgressBar);
        add(postfixLabel);
    }

    public DownloadProgressPanel(String prefix, String postfix) {
        this();
        prefixLabel.setText(prefix);
        postfixLabel.setText(postfix);
    }

    public DownloadProgressPanel(int partsToDownloadCount) {
        this();
        this.partsToDownloadCount = partsToDownloadCount;
    }

    public void setPrefixText(String prefixText) {
        prefixLabel.setText(prefixText);
    }

    public void setPostfixText(String postfixText) {
        postfixLabel.setText(postfixText);
    }

    public void setValue(int value) {
        downloadProgressBar.setValue(value);
    }

    public void setPercent(int percent) {
        downloadProgressBar.setValue(percent);
        postfixLabel.setText(String.format("%3d %%", percent));
    }

    public void setPartsToDownloadCount(int partsToDownloadCount) {
        this.partsToDownloadCount = partsToDownloadCount;
    }

    public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        this.propertyChangeListenerArrayList.add(propertyChangeListener);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if(evt.getPropertyName().equals("progress")) {
            setPercent((Integer)evt.getNewValue());
        } else if(evt.getPropertyName().equals("videoPart")) {
            if(evt.getNewValue().getClass().equals(TwitchVideoPart.class)) {
                TwitchVideoPart videoPart = (TwitchVideoPart)evt.getNewValue();
                setPrefixText(new String().format("%2d / %2d", videoPart.getPartNumber()+1, partsToDownloadCount));
            }
        } else if (evt.getPropertyName().equals("state")) {
            if(evt.getNewValue().toString().equals("DONE")) {
                postfixLabel.setText("DONE");
            }

        }

    }




}
