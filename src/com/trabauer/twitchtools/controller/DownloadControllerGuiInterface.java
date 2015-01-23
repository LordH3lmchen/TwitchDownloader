package com.trabauer.twitchtools.controller;

import com.trabauer.twitchtools.gui.vod.download.DownloadProgressPanel;
import com.trabauer.twitchtools.gui.vod.download.OverallProgressPanel;
import com.trabauer.twitchtools.model.VideoQualityComboBoxModel;
import com.trabauer.twitchtools.utils.Consumer;

import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.LinkedList;

/**
 * Created by Flo on 18.01.2015.
 */
public interface DownloadControllerGuiInterface extends Consumer {
    /**
     * Returns the TwitchURL to Download
     * @return the URL REpresentation of if.
     */
//    public URL getTwitchURL();
    public File getDestinationFolder();
    public String getFilename();
    public String getFilenamePreviewText();
    public String getQuality();
    public int getWorkerCount();

    public void setConcatButtonActionCommand(String actionCommand);
    public void setConcatButtonText(String buttonText);

    public void setFileNameVariables(LinkedHashMap<String, String> fileNameVariables);

    public void setVideoQualityComboBoxModel(VideoQualityComboBoxModel videoQualityComboBoxModel);

    public void setFFMpegEnabled(Boolean bool);

//    public void setAvailableQualities(LinkedList<String> qualities);

    public void lockNavigationControls();

    public void unlockNavigationControls();

    public void addOverallProgressPanel(OverallProgressPanel overallProgressPanel);

    public void addDownloadProgressPanel(DownloadProgressPanel downloadProgressPanel);

    public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener);

    public void setFFMpegOptions(String ffMpegOptions);


}
