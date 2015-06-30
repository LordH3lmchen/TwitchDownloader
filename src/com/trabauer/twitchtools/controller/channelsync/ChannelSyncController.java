package com.trabauer.twitchtools.controller.channelsync;

import com.trabauer.twitchtools.TwitchVodLoader;
import com.trabauer.twitchtools.gui.images.TwitchToolsImages;
import com.trabauer.twitchtools.gui.vod.channelsync.ChannelSyncLogFrame;
import com.trabauer.twitchtools.gui.vod.channelsync.ChannelSyncMenuBar;
import com.trabauer.twitchtools.gui.vod.channelsync.SyncChannelMainPanel;
import com.trabauer.twitchtools.model.twitch.*;
import com.trabauer.twitchtools.utils.OsUtils;
import com.trabauer.twitchtools.utils.OsValidator;
import com.trabauer.twitchtools.utils.TwitchToolPreferences;
import com.trabauer.twitchtools.worker.FFMpegConverterWorker;
import com.trabauer.twitchtools.worker.HttpFileDownloadWorker;
import com.trabauer.twitchtools.worker.TwitchDownloadWorker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.regex.Pattern;

/**
 * This is the main controller for this application. That handls operations between the models and the views.
 *
 * Created by Flo on 20.01.2015.
 */
public class ChannelSyncController implements ChannelSyncControllerInterface {

    public static final String FFMPEG_EXE_URL_STR = "http://trabauer.com/downloads/project_ressources/TwitchTools/ffmpeg.exe";
    public static final String VERSION_INFO_URL_STR = "http://trabauer.com/downloads/TwitchVodLoaderInfo.txt";
    public static final String PROGRAM_DOWNLOAD_URL_STR = "http://trabauer.com/downloads/TwitchVodLoader.jar";
    public static final String PROJECT_PAGE_URL_STR = "http://lordh3lmchen.github.io/TwitchDownloader/";
    public static final String PROGRAM_VERSION = "TwitchVodDownloader 0.2";

    private final JFrame mainFrame;
    private final SyncChannelMainPanel mainPanel;
    private final JMenuBar mainMenuBar;
    private ChannelSyncLogFrame progressFrame;

    private final TwitchVideoInfoList twitchVideoInfoList;
    private final LinkedBlockingQueue<TwitchVideoInfo> twitchVideoInfoWorkerQueue;
    private TwitchVideoInfo currentTwitchVideoInfo, currentConvertingTwitchVideoInfo;
    private File playlist;
    private File ffmpegFileListFile;
    String playlistFolderPath;

    private final ThreadPoolExecutor ffmpegExecutorService;
    private final ThreadPoolExecutor downloadExecutorService;
    private File ffmpegExecutable;
    private String ffmpegCommand;
    private ArrayList<TwitchVideoPart> videoParts;




    public ChannelSyncController() {
        this.twitchVideoInfoList = new TwitchVideoInfoList();

        mainFrame = new JFrame("Twitch VOD Downloader");
        this.mainPanel = new SyncChannelMainPanel(this, twitchVideoInfoList);
        mainFrame.getContentPane().add(this.getMainPanel());
        mainFrame.setSize(750, 550);
        mainFrame.setMinimumSize(new Dimension(550, 450));
        mainFrame.setVisible(true);
        mainFrame.setVisible(true);
        mainFrame.setIconImage(TwitchToolsImages.getTwitchDownloadToolImage());

        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainMenuBar = new ChannelSyncMenuBar(this, mainFrame);

        this.progressFrame = new ChannelSyncLogFrame();
        this.playlistFolderPath = TwitchToolPreferences.getInstance().get(TwitchToolPreferences.DESTINATION_DIR_PREFKEY, OsUtils.getUserHome()) + "/playlists/";
        this.ffmpegExecutorService = new ThreadPoolExecutor(1, 1, 5000L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        this.downloadExecutorService = new ThreadPoolExecutor(15, 15, 5000L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());

        twitchVideoInfoWorkerQueue = new LinkedBlockingQueue<>();

        try { // look for ffmpeg
            if(OsValidator.isWindows()) {
                ffmpegExecutable = new File(new File(getJarURI().getPath()).getParent().concat("/ffmpeg.exe"));
                if (!ffmpegExecutable.exists()) {
                    int choice = JOptionPane.showConfirmDialog(mainFrame, "FFMPEG not found! Do you want to download it? FFMPEG is required to convert videos", "FFMPEG not found! Download it?", JOptionPane.YES_NO_OPTION);
                    if (choice == 0) { //YES
                        downloadFFMPEG();
                    } //else if(choice == 0) { //NO
                    // Nothing right now
                    //}
                }
                ffmpegCommand = ffmpegExecutable.getAbsolutePath();
            } else if(OsValidator.isUnix() || OsValidator.isMac()) {
                System.out.println("Running on a Unix System");
                ffmpegCommand = "ffmpeg";
            } else {
                System.out.println("unknown OS assuming ffmpeg is installed and can be accessed via path-variable");
                ffmpegCommand = "ffmpeg";
            }
        } catch (URISyntaxException e) {
            JOptionPane.showMessageDialog(mainPanel, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        checkForUpdates();


    }

    private void checkForUpdates() {
        try {
            URL VersionInfoUrl = new URL(VERSION_INFO_URL_STR);
            InputStream is = VersionInfoUrl.openStream();
            Scanner sc = new Scanner(is);
            String line = null;
            if(sc.hasNextLine()) line = sc.nextLine();
            if (line != null) {
                if(! line.equals(PROGRAM_VERSION)) {
                    int choice = JOptionPane.showConfirmDialog(mainFrame, "Update Available! Download latest Version?", "Update Available!", JOptionPane.YES_NO_OPTION);
                    if (choice == 0) { //YES
                        openUrlInBrowser(new URL(PROJECT_PAGE_URL_STR));
                    } //else if(choice == 0) { //NO
                    // Nothing right now
                    //}
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public JPanel getMainPanel() {
        return mainPanel;
    }

    @Override
    public void searchFldText(String text, boolean pastBroadcasts) throws IOException {

        ArrayList<TwitchVideoInfo> cachedTVIs = new ArrayList<>();


        //Getting all Quued and Processed VideoInfoObjects
        if(currentTwitchVideoInfo != null)
            cachedTVIs.add(currentTwitchVideoInfo);
        if(currentConvertingTwitchVideoInfo != null)
            cachedTVIs.add(currentConvertingTwitchVideoInfo);
        for(TwitchVideoInfo queued: twitchVideoInfoWorkerQueue.toArray(new TwitchVideoInfo[0])) {
            cachedTVIs.add(queued);
        }

        for(Runnable runnable:ffmpegExecutorService.getQueue()) {
            if(runnable instanceof FFMpegConverterWorker) {
                FFMpegConverterWorker ffMpegConverterWorker = (FFMpegConverterWorker) runnable;
                cachedTVIs.add(ffMpegConverterWorker.getRelatedTwitchVideoInfo());
            }
        }

        twitchVideoInfoList.update(text, pastBroadcasts, 40, 0, cachedTVIs); //Updates Videos Except new
        for(TwitchVideoInfo tvi: twitchVideoInfoList.getTwitchVideoInfos()) { //Search related file on disk
            searchLocalFiles(tvi);
        }

        if(twitchVideoInfoList.get(0).getChannel().getStream().isOnline() && pastBroadcasts) {
            twitchVideoInfoList.get(0).setState(TwitchVideoInfo.State.LIVE);
        }


    }


    /**
     * Updates the State of a new TwitchVideoInfoObjects based on the Stored Videos
     *
     *
     * @param tvi the Twitch Video Info Object that should be modified
     */
    private void searchLocalFiles(TwitchVideoInfo tvi) {
        if (tvi.getState().equals(TwitchVideoInfo.State.INITIAL)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HHmm");
            String dateTimeStr = sdf.format(tvi.getRecordedAt().getTime());
            String destinationDir = TwitchToolPreferences.getInstance().get(TwitchToolPreferences.DESTINATION_DIR_PREFKEY, OsUtils.getUserHome());

            File playlist = new File(destinationDir +"/playlists/" + tvi.getId() + ".m3u");
            if(playlist.exists() && playlist.isFile() && playlist.canRead()) {
                tvi.setMainRelatedFileOnDisk(playlist);
                tvi.putRelatedFile("playlist", playlist);

                try {
                    InputStream is = new FileInputStream(playlist);
                    Scanner sc = new Scanner(is);
                    int i = 0;
                    while (sc.hasNextLine()) {
                        String line = sc.nextLine();
                        File file = new File(line);
                        if(file.exists()) {
                            i++;
                            String key = String.format("playlist_item_%04d", i);
                            tvi.putRelatedFile(key, file);
                        }
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                tvi.setState(TwitchVideoInfo.State.DOWNLOADED);
            }

            playlistFolderPath = destinationDir + "/playlists/";
            ffmpegFileListFile = new File(playlistFolderPath + tvi.getId() + ".ffmpeglist");
            if(ffmpegFileListFile.exists()) {
                tvi.putRelatedFile("ffmpegFileListFile", ffmpegFileListFile);
            }

            File mp4Video = new File(destinationDir + "/" + OsUtils.getValidFilename(tvi.getChannelName()) + "/" + OsUtils.getValidFilename(tvi.getTitle()) + "_" + dateTimeStr + ".mp4");
            if(mp4Video.exists() && mp4Video.isFile() && mp4Video.canRead()) {
                tvi.setMainRelatedFileOnDisk(mp4Video);
                tvi.putRelatedFile("mp4Video", mp4Video);
                tvi.setState(TwitchVideoInfo.State.CONVERTED);
            }
        }
    }

    @Override
    public void openUrlInBrowser(URL url) {
        try {
            Desktop.getDesktop().browse(url.toURI());
        } catch (URISyntaxException e) {
            JOptionPane.showMessageDialog(mainPanel, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(mainPanel, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

    }

    @Override
    public void loadMoreSearchResults() {
        twitchVideoInfoList.loadMore(null);
    }

    @Override
    public void downloadTwitchVideo(TwitchVideoInfo videoInfo) {
        videoInfo.setState(TwitchVideoInfo.State.QUEUED_FOR_DOWNLOAD);
        if(twitchVideoInfoWorkerQueue.isEmpty() && (downloadExecutorService.getActiveCount()==0)) { //Queue is Empty and no running workers
            twitchVideoInfoWorkerQueue.add(videoInfo);
            initializeDownload();
        } else {
            twitchVideoInfoWorkerQueue.add(videoInfo);
        }
    }

    @Override
    public void selectMostRecent(Integer age) {
        twitchVideoInfoList.selectMostRecentForDownload(age);
    }

    @Override
    public void downloadAllSelectedTwitchVideos() {
//        twitchVideoInfoWorkerQueue.resetQueue(twitchVideoInfoList.getAllSelected());

        for(TwitchVideoInfo tvi: twitchVideoInfoList.getAllSelected()) {
            try {
                twitchVideoInfoWorkerQueue.put(tvi);
                tvi.setState(TwitchVideoInfo.State.QUEUED_FOR_DOWNLOAD);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        //progressFrame.setVisible(true); //Is displayed in the main window now


        initializeDownload();


    }

    @Override
    public void convert2mp4(TwitchVideoInfo relatedTwitchVideoInfoObject) {
        String destinationDirPath = TwitchToolPreferences.getInstance().get(TwitchToolPreferences.DESTINATION_DIR_PREFKEY, OsUtils.getUserHome());
        playlistFolderPath = destinationDirPath + "/playlists/";
        ffmpegFileListFile = new File(playlistFolderPath + relatedTwitchVideoInfoObject.getId() + ".ffmpeglist");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HHmm");
        String dateTimeStr = sdf.format(relatedTwitchVideoInfoObject.getRecordedAt().getTime());
        File destinationVideoFile = new File(destinationDirPath + "/" + OsUtils.getValidFilename(relatedTwitchVideoInfoObject.getChannelName()) + "/" + OsUtils.getValidFilename(relatedTwitchVideoInfoObject.getTitle()) + "_" + dateTimeStr + ".mp4");

        LinkedList<String> ffmpegOptions = null;
        try {
            ffmpegOptions = getRecomendedFfmpegParameters(relatedTwitchVideoInfoObject);
        } catch (MalformedURLException e) {
            JOptionPane.showMessageDialog(mainPanel, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }


        FFMpegConverterWorker ffMpegConverterWorker;
        ffMpegConverterWorker = new FFMpegConverterWorker(destinationVideoFile, ffmpegFileListFile, ffmpegCommand, ffmpegOptions);
        ffMpegConverterWorker.setVideoLength(relatedTwitchVideoInfoObject.getLength());
        ffMpegConverterWorker.setRelatedTwitchVideoInfo(relatedTwitchVideoInfoObject);
        ffMpegConverterWorker.addPropertyChangeListener(this);
        ffMpegConverterWorker.addPropertyChangeListener(mainPanel.getConvertProgressPanel());
        LinkedBlockingQueue queue = (LinkedBlockingQueue)ffmpegExecutorService.getQueue();
        mainPanel.getConvertProgressPanel().setQueue(queue);
        relatedTwitchVideoInfoObject.setState(TwitchVideoInfo.State.QUEUED_FOR_CONVERT);
        ffmpegExecutorService.execute(ffMpegConverterWorker);
    }

    @Override
    public void delete(TwitchVideoInfo relatedTwitchVideoInfoObject) {
        relatedTwitchVideoInfoObject.deleteAllRelatedFiles();
        relatedTwitchVideoInfoObject.setState(TwitchVideoInfo.State.INITIAL);
    }

    /**
     * Prepares the Download and creates needed files in the destination folder
     *
     */
    private void initializeDownload()  {

        if(twitchVideoInfoWorkerQueue.isEmpty()) {
            return;
        }

        mainPanel.getDownloadProgressPanel().setQueue((LinkedBlockingQueue)twitchVideoInfoWorkerQueue);
        mainPanel.getDownloadProgressPanel().setVisible(true);

        //TwitchVideoInfo tvi = twitchVideoInfoWorkerQueue.pop();

        currentTwitchVideoInfo = twitchVideoInfoWorkerQueue.poll(); //get the next Video
        currentTwitchVideoInfo.setState(TwitchVideoInfo.State.DOWNLOADING);
        try {
            String quality = currentTwitchVideoInfo.getDownloadInfo().getPreferedQuality(TwitchToolPreferences.getQualityOrder()); //select quality based on TwitchToolsPreferences
            videoParts = currentTwitchVideoInfo.getDownloadInfo().getTwitchBroadcastParts(quality); //get the Parts of a Video
        } catch (IOException e) {
            JOptionPane.showMessageDialog(mainPanel, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        mainPanel.getDownloadProgressPanel().setMaximum(videoParts.size() * 100);
        mainPanel.getDownloadProgressPanel().setValue(0);
        mainPanel.getDownloadProgressPanel().setTitle(currentTwitchVideoInfo.getTitle());


        //Add Partnumbers for the WorkerThread
        int i = 0;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HHmm");
        String dateTimeStr = sdf.format(currentTwitchVideoInfo.getRecordedAt().getTime());
        String destinationDir = TwitchToolPreferences.getInstance().get(TwitchToolPreferences.DESTINATION_DIR_PREFKEY, OsUtils.getUserHome());
        File destinationFilenameTemplate = new File(destinationDir + "/" + OsUtils.getValidFilename(currentTwitchVideoInfo.getChannelName()) + "/" + OsUtils.getValidFilename(currentTwitchVideoInfo.getTitle()) + "_" + dateTimeStr);
        ArrayList<File> destinationFiles = new ArrayList<>();

        for(TwitchVideoPart videoPart: videoParts) {
            videoPart.getFileExtension();
            videoPart.setPartNumber(i++);
            File destinationFile = new File(destinationFilenameTemplate.getAbsolutePath()+"_"+String.valueOf(videoPart.getPartNumber()+videoPart.getFileExtension()));
            destinationFiles.add(destinationFile);
            TwitchDownloadWorker tdw = new TwitchDownloadWorker(destinationFile, videoPart);
            tdw.addPropertyChangeListener(this);
            tdw.addPropertyChangeListener(mainPanel.getDownloadProgressPanel());
            downloadExecutorService.execute(tdw);
        }


        createPlaylistsFolder();
        String playlistsFolderPath = TwitchToolPreferences.getInstance().get(TwitchToolPreferences.DESTINATION_DIR_PREFKEY, OsUtils.getUserHome()) + "/playlists/";
        playlist = new File(playlistsFolderPath + currentTwitchVideoInfo.getId() + ".m3u");
        createM3uPlaylist(playlist, destinationFiles);
        ffmpegFileListFile = new File(playlistsFolderPath + currentTwitchVideoInfo.getId() + ".ffmpeglist");
        createFFMpegFilelist(ffmpegFileListFile, destinationFiles);
    }

    private void createPlaylistsFolder() {
        File playlistsFolder = new File(TwitchToolPreferences.getInstance().get(TwitchToolPreferences.DESTINATION_DIR_PREFKEY, OsUtils.getUserHome())+"/playlists");
        if(! playlistsFolder.exists()) {
            boolean mkdirs = playlistsFolder.mkdirs();
            if(mkdirs == false) {
                JOptionPane.showConfirmDialog(mainFrame, "Unable to create folder for playlists in " + playlistsFolder.getParent() + " make shure you have write access to that directory.", "Unable to create playist folder!", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if(evt.getSource() instanceof TwitchDownloadWorker) {

            if(evt.getPropertyName().equals("state")) {
                if(evt.getNewValue().equals(SwingWorker.StateValue.DONE)) {
                    if(downloadExecutorService.getActiveCount()==0 ) { // if (Download of video is done)
                        currentTwitchVideoInfo.setMainRelatedFileOnDisk(playlist);
                        currentTwitchVideoInfo.setState(TwitchVideoInfo.State.DOWNLOADED);
                        mainPanel.getDownloadProgressPanel().setVisible(false);
                        initializeDownload(); //try init next Download
                    }
                } else if (evt.getNewValue().equals(SwingWorker.StateValue.STARTED)) {
                    TwitchDownloadWorker source = (TwitchDownloadWorker) evt.getSource();
                    TwitchVideoPart videoPart = source.getVideoPart();
                    System.out.println(String.format("downloading Nr. %6d %s", videoPart.getPartNumber(), videoPart.getUrl()));
                    progressFrame.addOutputText(String.format("Downloading Part %d/%d %s\n",
                            videoPart.getPartNumber() + 1,
                            videoParts.size(),
                            videoPart.getUrl()));
                }
            }

        } else if(evt.getSource() instanceof FFMpegConverterWorker) {
            FFMpegConverterWorker runningFFmpegWorker = (FFMpegConverterWorker)evt.getSource();
            if(evt.getPropertyName().equals("outputline")) {
                String output = evt.getNewValue().toString();
                progressFrame.addOutputText(output);
            } else if(evt.getPropertyName().equals("state")) {
                if(evt.getNewValue().equals(SwingWorker.StateValue.STARTED)) {
                    progressFrame.addOutputText("Starting to Convert Video");
                    currentConvertingTwitchVideoInfo = runningFFmpegWorker.getRelatedTwitchVideoInfo();
                } else if(evt.getNewValue().equals(SwingWorker.StateValue.DONE)) {
                    currentConvertingTwitchVideoInfo = null;
                }
            }
        }
    }

    private void createM3uPlaylist(File fileName, List<File> files) {
        //return createFileList("", "", fileName, twitchDownloadWorkerQueue, ".m3u", List<File>files);
        try {
            createFileList(fileName, "", "", files);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(mainPanel, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void createFFMpegFilelist(File fileName, List<File> files) {
        try {
            createFileList(fileName, "file '", "'", files);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(mainPanel, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

        domain = TwitchVodLoader.class.getProtectionDomain();
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

    private void downloadFFMPEG() {
        URL ffmpegExeUrl = null;
        try {
            ffmpegExeUrl = new URL(FFMPEG_EXE_URL_STR);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpFileDownloadWorker httpFileDownloadWorker = new HttpFileDownloadWorker(ffmpegExeUrl, ffmpegExecutable);
        httpFileDownloadWorker.addPropertyChangeListener(mainPanel.getDownloadProgressPanel());
        mainPanel.getConvertProgressPanel().setTitle("FFMPEG");
        httpFileDownloadWorker.execute();
    }




}
