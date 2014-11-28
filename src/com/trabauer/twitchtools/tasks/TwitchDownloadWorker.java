package com.trabauer.twitchtools.tasks;

import com.trabauer.twitchtools.model.twitch.TwitchDownloadQueue;
import com.trabauer.twitchtools.model.twitch.TwitchVideoPart;

import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;

/**
 * This class represents a DownloadTask. It will download all parts of a TwitchVideo.
 * Multiple TwitchDownloadTasks could be used, to increase the speed.(they all use the same TwitchDownloadQueue)
 *
 * Created by Flo on 23.11.2014.
 *
 *
 */
public class TwitchDownloadWorker extends SwingWorker<Void, Void> {
    private File destinationFilename;
    private TwitchDownloadQueue downloadQueue;
    private TwitchVideoPart videoPart;


    /**
     *
     * @param destinationFilename
     * @param downloadQueue
     */
    public TwitchDownloadWorker(File destinationFilename, TwitchDownloadQueue downloadQueue) {
        this.destinationFilename = destinationFilename;
        this.downloadQueue = downloadQueue;
    }

    /**
     * Method Stub, implement usefull Download when ready.
     * @return
     * @throws Exception
     */
    @Override
    protected Void doInBackground() throws Exception {
        Random random = new Random(); //TODO Method Stub

        videoPart = null;

        while(! downloadQueue.isEmpty()) {
            int progress = 0;
            setVideoPart(downloadQueue.popNextVideoPart());
            setProgress(0);

            URL url = videoPart.getUrl();
            InputStream is = null;
            FileOutputStream fos = null;
            String fileExtension = "";

            int i = url.getFile().lastIndexOf('.');
            if(i>0) fileExtension = url.getFile().substring(i);


            try{
                URLConnection urlConnection = url.openConnection();
                int partSize = urlConnection.getContentLength();
                is = urlConnection.getInputStream();
                File file = new File(destinationFilename.toString()+"_"+String.valueOf(videoPart.getPartNumber())+fileExtension);
                System.out.println("Downloading " + file);
                fos = new FileOutputStream(file);
                byte[] buffer = new byte[4096];
                int len;
                long done = 0;
                float percent;
                while( (len=is.read(buffer)) > 0 ) {
                    fos.write(buffer, 0, len);
                    done+=len;
                    percent = (done*100)/partSize;
                    progress =(int)percent;
                    //System.out.printf("\rProgress %10d/%10d, %3d %%", done, partSize, progress);
                    setProgress(Math.min(progress, 100));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return null;
    }

    @Override
    protected void done() {
        super.done();
    }

    protected void setVideoPart(TwitchVideoPart videoPart) {
        firePropertyChange("videoPart", this.videoPart, videoPart);
        this.videoPart = videoPart;
    }
}
