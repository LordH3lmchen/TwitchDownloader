package com.trabauer.twitchtools.model;

import java.io.File;
import java.net.URL;

/**
 * Created by Flo on 25.01.2015.
 */
public class TwitchVideoPartsDownloadQueueItem extends DownloadQueueItem {
    private int partNumber;


    public TwitchVideoPartsDownloadQueueItem() {
    }

    public TwitchVideoPartsDownloadQueueItem(URL sourceURL, File destinationFile, int partNumber) {
        super(sourceURL, destinationFile);
        this.partNumber = partNumber;
    }

    public int getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(int partNumber) {
        this.partNumber = partNumber;
    }
}
