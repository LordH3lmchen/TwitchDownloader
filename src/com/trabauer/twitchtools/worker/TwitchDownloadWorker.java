package com.trabauer.twitchtools.worker;

import com.trabauer.twitchtools.model.twitch.TwitchVideoPart;

import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;

/**
 * This class represents a DownloadTask. It will download all parts of a TwitchVideo.
 * Multiple TwitchDownloadTasks could be used, to increase the speed.(they all use the same TwitchDownloadQueue)
 *
 * Created by Flo on 23.11.2014.
 *
 *
 */
public class TwitchDownloadWorker extends SwingWorker<Void, Void> {
    private File destinationFile;
    private TwitchVideoPart videoPart;
    private int attempt;


    /**
     *  @param destinationFile
     *  @param videoPart
     */
    public TwitchDownloadWorker(File destinationFile, TwitchVideoPart videoPart) {
        this.destinationFile = destinationFile;
        this.videoPart = videoPart;
        this.attempt = 0;
    }

    /**
     * Method Stub, implement usefull Download when ready.
     * @return
     * @throws Exception
     */
    @Override
    protected Void doInBackground() throws TwitchDownloadException {

        int progress = 0;
        setProgress(0);

        URL url = null;
        InputStream is = null;
        FileOutputStream fos = null;

        int len;
        long done = 0;
        float percent = 0.0F;

        try{
            url = new URL(videoPart.getUrl());
//            System.out.printf("Open Connection to %s\n", url.toString());
            attempt++;
            URLConnection urlConnection = url.openConnection();
            urlConnection.setReadTimeout(30000);
            int partSize = urlConnection.getContentLength();
            if(attempt > 1) {
                System.err.printf("Attempt nr. %3d\n", attempt);
            }
            System.out.printf("Downloading \"%s\"\t content length = %10d byte\n from: %s\n", destinationFile.getName(), partSize, url.toString());
            is = urlConnection.getInputStream();
            File parent = new File(destinationFile.getParent());
            if( ! parent.exists()) {
                parent.mkdirs();
            }
            //System.out.println("Downloading " + file);
            fos = new FileOutputStream(destinationFile);
            byte[] buffer = new byte[4096];
            while( (len=is.read(buffer)) > 0 ) {
                fos.write(buffer, 0, len);
                done+=len;
                percent = (done*100)/partSize;
                progress =(int)percent;
                //System.out.printf("\rProgress %10d/%10d, %3d %%", done, partSize, progress);
                setProgress(Math.min(progress, 100));
            }
            if(done>=partSize) {
                System.out.printf("Download of %s complete\n", destinationFile.getName());
            } else {
                System.err.printf("Incomplete Download of %s\n", destinationFile.getName());
                throw new SocketTimeoutException("incomplete download");
            }
            setProgress(100);
        } catch (SocketTimeoutException te) {
            //te.printStackTrace();
            System.err.println(te.getMessage());
            System.err.printf("TimeOut at %.2f%% restarting download of %s\n URL: %s\n", percent, destinationFile.getName(), url.toString()); //TODO implement timeout handling
            try {
                is.close();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.destinationFile.delete();
            setProgress(0);
            if(attempt <= 10) {
                this.doInBackground();
            } else {
                System.err.printf("Tried to download to often skipping file %s\n", destinationFile.getName());
                throw new TwitchDownloadException(
                        String.format("Tried to download %s to often skipping file \n", destinationFile.getName()),
                        videoPart);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                is.close();
                fos.close();
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

    public File getDestinationFile() {
        return destinationFile;
    }

    public void setDestinationFile(File destinationFile) {
        this.destinationFile = destinationFile;
    }

    public TwitchVideoPart getVideoPart() {
        return videoPart;
    }

}
