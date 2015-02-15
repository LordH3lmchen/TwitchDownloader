package com.trabauer.twitchtools.model;

import java.io.File;
import java.net.URL;

/**
 * Bean for one item in a Queue. For a Simple File Download
 *
 * Created by Flo on 25.01.2015.
 */
public class DownloadQueueItem {
    private URL sourceURL;
    private File destinationFile;

    public DownloadQueueItem() {
    }

    public DownloadQueueItem(URL sourceURL, File destinationFile) {
        this.sourceURL = sourceURL;
        this.destinationFile = destinationFile;
    }

    public URL getSourceURL() {
        return sourceURL;
    }

    public void setSourceURL(URL sourceURL) {
        this.sourceURL = sourceURL;
    }

    public File getDestinationFile() {
        return destinationFile;
    }

    public void setDestinationFile(File destinationFile) {
        this.destinationFile = destinationFile;
    }
}
