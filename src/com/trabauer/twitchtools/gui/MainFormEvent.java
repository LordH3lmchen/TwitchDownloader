package com.trabauer.twitchtools.gui;

import java.util.EventObject;

/**
 * Created by Flo on 13.11.2014.
 */
public class MainFormEvent extends EventObject {

    private String twitchUrl;
    private String destinationFolder;
    private String filename;
    private int bandwidthInMBitPerSecond;


    public MainFormEvent(Object source) {
        super(source);
    }

    public MainFormEvent(Object source, String twitchUrl, String destinationFolder, String filename, int bandwidthInMBitPerSecond) {
        this(source);

        this.filename = filename;
        this.destinationFolder = destinationFolder;
        this.twitchUrl = twitchUrl;
        this.bandwidthInMBitPerSecond = bandwidthInMBitPerSecond;
    }

    public String getTwitchUrl() {
        return twitchUrl;
    }

    public String getDestinationFolder() {
        return destinationFolder;
    }

    public String getFilename() {
        return filename;
    }

    public int getBandwidthInMBitPerSecond() {
        return bandwidthInMBitPerSecond;
    }
}
