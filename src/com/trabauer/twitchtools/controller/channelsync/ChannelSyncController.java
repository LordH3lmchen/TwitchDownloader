package com.trabauer.twitchtools.controller.channelsync;

import com.trabauer.twitchtools.TwitchToolsApp;
import com.trabauer.twitchtools.gui.vod.channelsync.ChannelSyncProgressFrame;
import com.trabauer.twitchtools.gui.vod.channelsync.SyncChannelMainPanel;
import com.trabauer.twitchtools.model.*;
import com.trabauer.twitchtools.model.twitch.TwitchVideoInfo;
import com.trabauer.twitchtools.model.twitch.TwitchVideoInfoList;
import com.trabauer.twitchtools.model.twitch.TwitchVideoPart;
import com.trabauer.twitchtools.utils.OsUtils;
import com.trabauer.twitchtools.utils.TwitchToolPreferences;
import com.trabauer.twitchtools.worker.FFMpegConverterWorker;
import com.trabauer.twitchtools.worker.TwitchDownloadWorker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;
import java.util.prefs.Preferences;
import java.util.regex.Pattern;

/**
 * Created by Flo on 20.01.2015.
 */
public class ChannelSyncController implements ChannelSyncControllerInterface {

    private final SyncChannelMainPanel mainPanel;
    private ChannelSyncProgressFrame progressFrame;

    private final TwitchVideoInfoList twitchVideoInfoList;
    private TwitchVideoInfoList selectedTwitchVideoInfoList;
    private Preferences prefs;
    private ArrayList<TwitchDownloadWorker> twitchDownloadWorkers;
    private final WorkerQueue<TwitchVideoPart> workerQueue;
    private final WorkerQueue<TwitchVideoInfo> twitchVideoInfoWorkerQueue;
    private TwitchVideoInfo currentTwitchVideoInfo;
    private File playlist;
    private File ffmpegFileList;
    String playlistFolderPath;

    private final ThreadPoolExecutor ffmpegExecutorService;
    private File ffmpegExecutable;



    public ChannelSyncController() {
        this.twitchVideoInfoList = new TwitchVideoInfoList();
        this.mainPanel = new SyncChannelMainPanel(this, twitchVideoInfoList);
        this.progressFrame = new ChannelSyncProgressFrame();
        this.prefs = TwitchToolPreferences.getInstance();
        this.twitchDownloadWorkers = new ArrayList<TwitchDownloadWorker>();
        this.playlistFolderPath = TwitchToolPreferences.getInstance().get(TwitchToolPreferences.DESTINATION_DIR_PREFKEY, OsUtils.getUserHome()) + "/playlists/";
        this.ffmpegExecutorService = new ThreadPoolExecutor(1, 1, 5000L, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<Runnable>());


        for(int i = 0; i<10; i++) {
            TwitchDownloadWorker tdw = new TwitchDownloadWorker();
            tdw.addPropertyChangeListener(this);
            twitchDownloadWorkers.add(tdw);

        }
        workerQueue = new WorkerQueue<TwitchVideoPart>();
        twitchVideoInfoWorkerQueue = new WorkerQueue<TwitchVideoInfo>();

        try {
            ffmpegExecutable = new File(new File(getJarURI().getPath()).getParent().concat("/ffmpeg.exe"));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public JPanel getMainPanel() {
        return mainPanel;
    }

    @Override
    public void searchFldText(String text) throws IOException {
        twitchVideoInfoList.update(text, true, 40, 0);
        for(TwitchVideoInfo tvi: twitchVideoInfoList.getTwitchVideoInfos()) { //Search related file on disk
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HHmm");
            String dateTimeStr = sdf.format(tvi.getRecordedAt().getTime());
            String destinationDir = TwitchToolPreferences.getInstance().get(TwitchToolPreferences.DESTINATION_DIR_PREFKEY, OsUtils.getUserHome());
            File playlist = new File(destinationDir +"/playlists/" + tvi.getId() + ".m3u");
            if(playlist.exists() && playlist.isFile() && playlist.canRead()) {
                tvi.setRelatedFileOnDisk(playlist);
            }
            File mp4Video = new File(destinationDir + "/" + OsUtils.getValidFilename(tvi.getChannelName()) + "/" + OsUtils.getValidFilename(tvi.getTitle()) + "_" + dateTimeStr + ".mp4");
            if(mp4Video.exists() && mp4Video.isFile() && mp4Video.canRead()) {
                tvi.setRelatedFileOnDisk(mp4Video);
            }
        }
    }

    @Override
    public void openUrlInBrowser(URL url) {
        try {
            Desktop.getDesktop().browse(url.toURI());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void loadMoreSearchResults() {
        twitchVideoInfoList.loadMore();
    }

    @Override
    public void downloadTwitchVideo(TwitchVideoInfo videoInfo) {

    }

    @Override
    public void selectMostRecent(Integer age) {
        twitchVideoInfoList.selectMostRecent(age);
    }

    @Override
    public void downloadAllSelectedTwitchVideos() {
        twitchVideoInfoWorkerQueue.resetQueue(twitchVideoInfoList.getAllSelected());

        progressFrame.setVisible(true);


        try {
            initializeDownload();
        } catch (IOException e) {
            e.printStackTrace(); //TODO JDialog Output
        }

    }

    @Override
    public void convert2mp4(TwitchVideoInfo relatedTwitchVideoInfoObject) {
        String destinationDirPath = TwitchToolPreferences.getInstance().get(TwitchToolPreferences.DESTINATION_DIR_PREFKEY, OsUtils.getUserHome());
        playlistFolderPath = destinationDirPath + "/playlists/";
        ffmpegFileList = new File(playlistFolderPath + relatedTwitchVideoInfoObject.getId() + ".ffmpeglist");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HHmm");
        String dateTimeStr = sdf.format(relatedTwitchVideoInfoObject.getRecordedAt().getTime());
        File destinationVideoFile = new File(destinationDirPath + "/" + OsUtils.getValidFilename(relatedTwitchVideoInfoObject.getChannelName()) + "/" + OsUtils.getValidFilename(relatedTwitchVideoInfoObject.getTitle()) + "_" + dateTimeStr + ".mp4");

        LinkedList<String> ffmpegOptions = null;
        try {
            ffmpegOptions = getRecomendedFfmpegParameters(relatedTwitchVideoInfoObject);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


        FFMpegConverterWorker ffMpegConverterWorker = new FFMpegConverterWorker(destinationVideoFile, ffmpegFileList, ffmpegExecutable, ffmpegOptions);
        ffMpegConverterWorker.addPropertyChangeListener(this);
        ffmpegExecutorService.execute(ffMpegConverterWorker);
        relatedTwitchVideoInfoObject.setRelatedFileOnDisk(destinationVideoFile);
        System.out.printf("ffmpeg worker queue size is %d\n", ffmpegExecutorService.getQueue().size());
    }

    /**
     * Prepares the Download and creates needed files in the destination folder
     *
     * @throws IOException
     */
    private void initializeDownload() throws IOException {

        if(twitchVideoInfoWorkerQueue.isEmpty()) {
            progressFrame.setVisible(false);
            mainPanel.downloadAllBtnSetEnabled(true);
            return;
        }

        mainPanel.downloadAllBtnSetEnabled(false);

        // a SwingWorker can only run once. Create new ones for every Video
        this.twitchDownloadWorkers = new ArrayList<TwitchDownloadWorker>();
        for(int i = 0; i<10; i++) {
            TwitchDownloadWorker tdw = new TwitchDownloadWorker();
            tdw.addPropertyChangeListener(this);
            twitchDownloadWorkers.add(tdw);

        }

        //TwitchVideoInfo tvi = twitchVideoInfoWorkerQueue.pop();
        currentTwitchVideoInfo = twitchVideoInfoWorkerQueue.pop(); //get the next Video
        String quality = currentTwitchVideoInfo.getDownloadInfo().getPreferedQuality(TwitchToolPreferences.getQualityOrder()); //select quality based on TwitchToolsPreferences
        ArrayList<TwitchVideoPart> videoParts = currentTwitchVideoInfo.getDownloadInfo().getTwitchBroadcastParts(quality); //get the Parts of a Video

        //Add Partnumbers for the WorkerThread
        int i = 0;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HHmm");
        String dateTimeStr = sdf.format(currentTwitchVideoInfo.getRecordedAt().getTime());
        String destinationDir = TwitchToolPreferences.getInstance().get(TwitchToolPreferences.DESTINATION_DIR_PREFKEY, OsUtils.getUserHome());
        File destinationFilenameTemplate = new File(destinationDir + "/" + OsUtils.getValidFilename(currentTwitchVideoInfo.getChannelName()) + "/" + OsUtils.getValidFilename(currentTwitchVideoInfo.getTitle()) + "_" + dateTimeStr);
        ArrayList<File> destinationFiles = new ArrayList<File>();

        for(TwitchVideoPart videoPart: videoParts) {
            videoPart.getFileExtension();
            videoPart.setPartNumber(i++);
            File destinationFile = new File(destinationFilenameTemplate.getAbsolutePath()+"_"+String.valueOf(videoPart.getPartNumber()+videoPart.getFileExtension()));
            destinationFiles.add(destinationFile);
        }


        createPlaylistsFolder();
        String playlistsFolderPath = TwitchToolPreferences.getInstance().get(TwitchToolPreferences.DESTINATION_DIR_PREFKEY, OsUtils.getUserHome()) + "/playlists/";
        playlist = new File(playlistsFolderPath + currentTwitchVideoInfo.getId() + ".m3u");
        createM3uPlaylist(playlist, destinationFiles);
        ffmpegFileList = new File(playlistsFolderPath + currentTwitchVideoInfo.getId() + ".ffmpeglist");
        createFFMpegFilelist(ffmpegFileList, destinationFiles);

        workerQueue.resetQueue(videoParts);
        progressFrame.setMaximum(workerQueue.getInitialSize() * 100);
        progressFrame.setValue(0);
        progressFrame.setDescriptionText(String.format("Downloading %s - %s (%d videos remaining)",
                currentTwitchVideoInfo.getChannelDisplaylName(),
                currentTwitchVideoInfo.getTitle(),
                twitchVideoInfoWorkerQueue.size()));

        for(TwitchDownloadWorker twitchDownloadWorker: twitchDownloadWorkers) {
            twitchDownloadWorker.setDownloadWorkerQueue(workerQueue);
            twitchDownloadWorker.setDestinationFilename(destinationFilenameTemplate);
            twitchDownloadWorker.execute();
        }
    }

    private void createPlaylistsFolder() {
        File playlistsFolder = new File(TwitchToolPreferences.getInstance().get(TwitchToolPreferences.DESTINATION_DIR_PREFKEY, OsUtils.getUserHome())+"/playlists");
        if(! playlistsFolder.exists()) {
            playlistsFolder.mkdirs();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if(evt.getSource() instanceof TwitchDownloadWorker) {
            if(twitchDownloadWorkers.contains(evt.getSource()))
                //System.out.print(String.format("DownloadWorker %2d: ", twitchDownloadWorkers.indexOf(evt.getSource()) ) );

            if(evt.getPropertyName().equals("videoPart")) {
                TwitchVideoPart videoPart = (TwitchVideoPart) evt.getNewValue();
                System.out.println(String.format("downloading Nr. %6d %s", videoPart.getPartNumber(), videoPart.getUrl()));
                progressFrame.addOutputText(String.format("Downloading Part %d/%d %s\n",
                        videoPart.getPartNumber()+1,
                        workerQueue.getInitialSize(),
                        videoPart.getUrl()));
            }
            else if(evt.getPropertyName().equals("state")) {
                if(evt.getNewValue().equals(SwingWorker.StateValue.DONE)) {
                    boolean allDone = true;
                    for(TwitchDownloadWorker worker: twitchDownloadWorkers) {
                        if(! worker.getState().equals(SwingWorker.StateValue.DONE ) ) {
                            allDone = false;
                            break;
                        }
                    }
                    if(allDone) {
                        // Change View to
                        progressFrame.setValue(workerQueue.getInitialSize()*100);
                        try {
                            currentTwitchVideoInfo.setRelatedFileOnDisk(playlist);
                            initializeDownload();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else if(evt.getPropertyName().equals("progress")) {
                int oldProgress = (int)evt.getOldValue();
                int newProgress = (int)evt.getNewValue();
                if( newProgress > oldProgress ) { // exclude progress reset for next video file in queue
                    progressFrame.increaseValue(newProgress-oldProgress);
                }
            }
        } else if(evt.getSource() instanceof FFMpegConverterWorker) {
            if(evt.getPropertyName().equals("outputline")) {
                String output = evt.getNewValue().toString();
                progressFrame.addOutputText(output);
            } else if(evt.getPropertyName().equals("state")) {
                if(evt.getNewValue().equals(SwingWorker.StateValue.STARTED)) {
                    progressFrame.setVisible(true);
                    progressFrame.addOutputText("Starting to Convert Video");
                } else if (evt.getNewValue().equals(SwingWorker.StateValue.DONE)) {
                    if(! runningDownloadWorkerExists()) {
                        progressFrame.setVisible(false);
                    }
                }
            }
        }

    }


    private boolean runningDownloadWorkerExists() {
        boolean workerRunning = true;
        for(TwitchDownloadWorker tdw: twitchDownloadWorkers) {
            if(tdw.getState().equals(SwingWorker.StateValue.STARTED)){
                workerRunning = false;
                break;
            }
        }
        return workerRunning;
    }

    private void createM3uPlaylist(File fileName, List<File> files) {
        //return createFileList("", "", fileName, twitchDownloadWorkerQueue, ".m3u", List<File>files);
        try {
            createFileList(fileName, "", "", files);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void createFFMpegFilelist(File fileName, List<File> files) {
        try {
            createFileList(fileName, "file '", "'", files);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private void createFileList(File playlistFile, String prefix, String postfix, List<File> files) throws IOException {
        FileWriter fileWriter = new FileWriter(playlistFile);
        for(File file: files) {
            fileWriter.append(prefix + file.getAbsolutePath() + postfix + System.getProperty("line.separator"));
        }
        fileWriter.close();
    }


    private URI getJarURI()
            throws URISyntaxException
    {
        final ProtectionDomain domain;
        final CodeSource source;
        final URL url;
        final URI              uri;

        domain = TwitchToolsApp.class.getProtectionDomain();
        source = domain.getCodeSource();
        url    = source.getLocation();
        uri    = url.toURI();

        return (uri);
    }

    private String recomendedFFMpegOptions(TwitchVideoInfo tvi) throws MalformedURLException {
        URL twitchUrl = tvi.getUrl();
        if(Pattern.matches("http://www.twitch.tv/\\w+/b/\\d+", twitchUrl.toString())) {
            return "-c copy";
        } else if(Pattern.matches("http://www.twitch.tv/\\w+/c/\\d+", twitchUrl.toString())) {
            return "-c copy";
        } else if(Pattern.matches("http://www.twitch.tv/\\w+/v/\\d+", twitchUrl.toString())) {
            return "-c:v libx264 -c:a copy -bsf:a aac_adtstoasc";
        }
        return null;
    }

    private LinkedList<String> getRecomendedFfmpegParameters(TwitchVideoInfo tvi) throws MalformedURLException {
        return new LinkedList<>(Arrays.asList(recomendedFFMpegOptions(tvi).split(" ")));
    }

    public void progressFrameSetVisible(boolean x) {
        this.progressFrame.setVisible(x);
    }




}
