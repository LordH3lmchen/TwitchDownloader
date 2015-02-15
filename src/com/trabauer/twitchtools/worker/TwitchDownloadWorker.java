package com.trabauer.twitchtools.worker;

import com.trabauer.twitchtools.model.WorkerQueue;
import com.trabauer.twitchtools.model.twitch.TwitchVideoPart;

import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
    private File destinationFilename;
    private WorkerQueue<TwitchVideoPart> downloadWorkerQueue;
    private TwitchVideoPart videoPart;


    public TwitchDownloadWorker() {
        this.downloadWorkerQueue = new WorkerQueue<TwitchVideoPart>();
    }

    /**
     *  @param destinationFilename
     * @param downloadWorkerQueue
     */
    public TwitchDownloadWorker(File destinationFilename, WorkerQueue<TwitchVideoPart> downloadWorkerQueue) {
        this.destinationFilename = destinationFilename;
        this.downloadWorkerQueue = downloadWorkerQueue;
    }

    /**
     * Method Stub, implement usefull Download when ready.
     * @return
     * @throws Exception
     */
    @Override
    protected Void doInBackground() throws IOException {

        videoPart = null;

        while(! downloadWorkerQueue.isEmpty()) {
            int progress = 0;
            setVideoPart(downloadWorkerQueue.pop());
            setProgress(0);

            URL url = new URL(videoPart.getUrl());
            InputStream is = null;
            FileOutputStream fos = null;


            try{
                URLConnection urlConnection = url.openConnection();
                int partSize = urlConnection.getContentLength();
                is = urlConnection.getInputStream();
                File file = new File(destinationFilename.toString()+"_"+String.valueOf(videoPart.getPartNumber())+ videoPart.getFileExtension());
                File parent = new File(file.getParent());
                if( ! parent.exists()) {
                    parent.mkdirs();
                }
                //System.out.println("Downloading " + file);
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
                setProgress(100);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                is.close();
                fos.close();
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

    public File getDestinationFilename() {
        return destinationFilename;
    }

    public void setDestinationFilename(File destinationFilename) {
        this.destinationFilename = destinationFilename;
    }

    public WorkerQueue<TwitchVideoPart> getDownloadWorkerQueue() {
        return downloadWorkerQueue;
    }

    public void setDownloadWorkerQueue(WorkerQueue<TwitchVideoPart> downloadWorkerQueue) {
        this.downloadWorkerQueue = downloadWorkerQueue;
    }
}
