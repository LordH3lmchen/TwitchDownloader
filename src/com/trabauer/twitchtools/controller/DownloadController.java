package com.trabauer.twitchtools.controller;

import com.trabauer.twitchtools.TwitchToolsApp;
import com.trabauer.twitchtools.gui.vod.download.*;
import com.trabauer.twitchtools.model.FilenamePatternsComboBoxModel;
import com.trabauer.twitchtools.model.VideoQualityComboBoxModel;
import com.trabauer.twitchtools.model.twitch.TwitchDownloadQueue;
import com.trabauer.twitchtools.model.twitch.TwitchVideo;
import com.trabauer.twitchtools.utils.GuiPropertyChangeValue;
import com.trabauer.twitchtools.utils.OsValidator;
import com.trabauer.twitchtools.utils.TwitchToolPreferences;
import com.trabauer.twitchtools.worker.FFMpegConverterWorker;
import com.trabauer.twitchtools.worker.HttpFileDownloadWorker;
import com.trabauer.twitchtools.worker.TwitchDownloadWorker;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.*;
import java.util.prefs.*;
import java.util.regex.Pattern;

/**
 * Created by Flo on 13.11.2014.
 */
public class DownloadController implements ActionListener , Observer, NodeChangeListener, PreferenceChangeListener, PropertyChangeListener {

    public static final String FFMPEG_EXE_URL_STR = "http://trabauer.com/downloads/project_ressources/TwitchTools/ffmpeg.exe";

    private TwitchVideo twitchVideo = null;
    private FilenamePatternsComboBoxModel filenamePatternsComboBoxModel;
    private VideoQualityComboBoxModel videoQualityComboBoxModel;


    private Preferences prefs;

    private File ffmpegExecutable, m3uPlaylist, ffmpegFilelist;

    private DownloadControllerGuiInterface linkedGUI;


    public DownloadController(DownloadControllerGuiInterface linkedGUI) {
        super();
        this.twitchVideo = new TwitchVideo();
        this.linkedGUI = linkedGUI;

        ffmpegExecutable = null;
        try {
            ffmpegExecutable = new File(new File(getJarURI().getPath()).getParent().concat("/ffmpeg.exe"));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        prefs = TwitchToolPreferences.getInstance();
        prefs.addNodeChangeListener(this);
        prefs.addPreferenceChangeListener(this);


        if( !ffmpegExecutable.exists() ) {
            linkedGUI.setConcatButtonActionCommand("downloadFFMPEG");
            linkedGUI.setConcatButtonText("Download FFMPEG to enable video converting");

            if(! OsValidator.isWindows()) {
                linkedGUI.setConcatButtonText("Download FFMPEG to enable video converting (only Windows is suppoerted right now)"); //TODO make it available for OSX and Linux
                linkedGUI.setFFMpegEnabled(false);
            }

        }


        videoQualityComboBoxModel = new VideoQualityComboBoxModel(twitchVideo);
        twitchVideo.addObserver(this);
        twitchVideo.addObserver(videoQualityComboBoxModel);
        linkedGUI.setVideoQualityComboBoxModel(videoQualityComboBoxModel);






    }

    public void selectVideo(URL twitchUrl) {
        this.twitchVideo.updateTwitchVideoByUrl(twitchUrl);
    }

    public TwitchVideo getSelectedTwitchVideo() {
        return twitchVideo;
    }





    @Override
    public void actionPerformed(ActionEvent e) {
        //System.out.println("DownloadController ActionPerformed: " + e.getActionCommand().toString() + " from " + e.getSource());

        if (e.getActionCommand().equals("Twitch URL entered")) {
//            twitchVideo.updateTwitchVideoByUrl(linkedGUI.getTwitchURL());
//            linkedGUI.setFileNameVariables(twitchVideo.getStreamInformation());
        } else if(e.getActionCommand().equals("download settings entered")) {
//            String quality = linkedGUI.getQuality();
//            String filename = linkedGUI.getFilenamePreviewText();
//            File destinationDirectory = linkedGUI.getDestinationFolder();
//            int workerCount = linkedGUI.getWorkerCount();
//            downloadPastBroadcast(filename, destinationDirectory, quality, workerCount, twitchVideo);
        } else if (e.getActionCommand().equals("concatVideoParts")) {
            //convertVideoPartsToMP4((String) e.getNewValue());
        } else if (e.getActionCommand().equals("downloadFFMPEG")) {
            //downloadFFMPEG();
        } else {
            //System.err.println("don't know how to handle" + e.getActionCommand().toString() + " from " + e.getSource());
        }
    }




    private void downloadFFMPEG(PropertyChangeListener propertyChangeListener) {
        URL ffmpegExeUrl = null;
        try {
            ffmpegExeUrl = new URL(FFMPEG_EXE_URL_STR);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpFileDownloadWorker httpFileDownloadWorker = new HttpFileDownloadWorker(ffmpegExeUrl, ffmpegExecutable);
        httpFileDownloadWorker.addPropertyChangeListener(propertyChangeListener);
        httpFileDownloadWorker.addPropertyChangeListener(this);
        httpFileDownloadWorker.execute();
    }

    private void convertVideoPartsToMP4(String ffmpegOptions, PropertyChangeListener propertyChangeListener) {

        File destinationVideoFile = new File(linkedGUI.getDestinationFolder() + "/" + linkedGUI.getFilenamePreviewText() + ".mp4");
        LinkedList ffmpegOptionsList = new LinkedList(Arrays.asList(ffmpegOptions.split(" ")));


        FFMpegConverterWorker ffMpegConverterWorker = new FFMpegConverterWorker(destinationVideoFile, this.ffmpegFilelist, this.ffmpegExecutable, ffmpegOptionsList);

        ffMpegConverterWorker.addPropertyChangeListener(propertyChangeListener);
        ffMpegConverterWorker.addPropertyChangeListener(this);
        ffMpegConverterWorker.execute();
    }



    private void downloadPastBroadcast(String filename, File destinationDirectory, String quality, int threadcount, TwitchVideo twitchVideo) {
        String filePath = destinationDirectory.getAbsolutePath() + "/" + filename;
        File destinationFileTemplate = new File(filePath);
        TwitchDownloadQueue twitchDownloadQueue = new TwitchDownloadQueue(twitchVideo.getTwitchVideoParts(quality));
        String prefixLabelText = new String().format("%2d / %2d", twitchDownloadQueue.peekNextVideoPart().getPartNumber()+1, twitchDownloadQueue.getInitialSize());
        OverallProgressPanel overallProgressPanel = new OverallProgressPanel(twitchDownloadQueue.getInitialSize());
        overallProgressPanel.addActionListener(this);
        linkedGUI.addOverallProgressPanel(overallProgressPanel);

        File destinationFolder = destinationFileTemplate.getParentFile();
        if(! destinationFolder.exists()) {
            destinationFolder.mkdirs();
        }

        this.m3uPlaylist = createM3uPlaylist(destinationFileTemplate, twitchDownloadQueue);
        this.ffmpegFilelist = createFFMpegFilelist(destinationFileTemplate, twitchDownloadQueue);

        for(int i=0; i<threadcount; i++){
            TwitchDownloadWorker twitchDownloadWorker = new TwitchDownloadWorker(destinationFileTemplate, twitchDownloadQueue);
            DownloadProgressPanel downloadProgressPanel = new DownloadProgressPanel(prefixLabelText, "  0 %");
            downloadProgressPanel.setPartsToDownloadCount(twitchDownloadQueue.getInitialSize());
            twitchDownloadWorker.addPropertyChangeListener(downloadProgressPanel);
            twitchDownloadWorker.addPropertyChangeListener(overallProgressPanel);
            linkedGUI.addDownloadProgressPanel(downloadProgressPanel);
            twitchDownloadWorker.execute();
        }

    }

    private File createM3uPlaylist(File fileTemplate, TwitchDownloadQueue twitchDownloadQueue) {
        return createFileList("", "", fileTemplate, twitchDownloadQueue, ".m3u");
    }

    private File createFFMpegFilelist(File fileTemplate, TwitchDownloadQueue twitchDownloadQueue) {
        return createFileList("file '", "'", fileTemplate, twitchDownloadQueue, ".ffmpegfilelist");
    }

    private File createFileList(String prefix, String postfix, File fileTemplate, TwitchDownloadQueue twitchDownloadQueue, String listFileExtension) {
        File filelistFile = new File(fileTemplate + listFileExtension);
        int partCount = twitchDownloadQueue.getInitialSize();
        String fileExtension = "";
        int i = twitchDownloadQueue.peekNextVideoPart().getUrl().getFile().lastIndexOf('.');
        if(i>0) fileExtension = twitchDownloadQueue.peekNextVideoPart().getUrl().getFile().substring(i);
        fileExtension = fileExtension.replaceAll("\\?.*$", "");



        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(filelistFile);
            for(int j=0; j<partCount; j++) {
                String filename = fileTemplate + "_" + String.valueOf(j) + fileExtension;
                File partFile = new File(filename);
                fileWriter.append(prefix + partFile.getName() + postfix + System.getProperty("line.separator"));
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return filelistFile;
    }


    @Override
    public void update(Observable o, Object arg) {
        if(o.equals(twitchVideo)) {
            //System.out.println("Twitch Video updated!");
            linkedGUI.setFileNameVariables(twitchVideo.getStreamInformation());
        }
    }



    @Override
    public void childAdded(NodeChangeEvent evt) {
        Preferences parent = evt.getParent(), child = evt.getChild();
        //System.out.println( parent.name() + " hat neuen Knoten " + child.name());
    }

    @Override
    public void childRemoved(NodeChangeEvent evt) {
        Preferences parent = evt.getParent(), child = evt.getChild();
        //System.out.println( parent.name() + " verliert Knoten " + child.name());
    }

    @Override
    public void preferenceChange(PreferenceChangeEvent evt) {
        String key = evt.getKey(), value = evt.getNewValue();
        Preferences node = evt.getNode();
        //System.out.println( node.name() + "hat einen neuen Wert für " + value + " für " + key);
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

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if(evt.getPropertyName().equals("state") && evt.getSource().getClass().equals(HttpFileDownloadWorker.class)) {
            //System.out.println(evt.getPropertyName() + " changed to " + evt.getNewValue().toString() );
            if(evt.getNewValue().toString().equals("DONE")) {
                System.out.println("Download finished");
                linkedGUI.setConcatButtonActionCommand("concatButton");
                linkedGUI.setConcatButtonText("Convert to MP4 Video");
                linkedGUI.setFFMpegEnabled(true);
                linkedGUI.unlockNavigationControls();
            } else if(evt.getNewValue().toString().equals("STARTED")) {
                System.out.println("Downloading FFMPEG executable");
                linkedGUI.setFFMpegEnabled(false);
                linkedGUI.lockNavigationControls();
            }
        } else if(evt.getPropertyName().equals("state")) {
            if(evt.getNewValue().toString().equals("STARTED")) {
                linkedGUI.lockNavigationControls();
                linkedGUI.setFFMpegEnabled(false);
            } else if(evt.getNewValue().toString().equals("DONE")) {
                linkedGUI.unlockNavigationControls();
                linkedGUI.setFFMpegEnabled(true);
            }
        } else if(evt.getPropertyName().equals("Twitch URL entered")) {
            if(evt.getNewValue() instanceof URL) twitchVideo.updateTwitchVideoByUrl((URL)evt.getNewValue());
            linkedGUI.setFileNameVariables(twitchVideo.getStreamInformation());
            linkedGUI.setFFMpegOptions(recomendedFFMpegOptions());

        } else if(evt.getPropertyName().equals("download settings entered")) {
            String quality = linkedGUI.getQuality();
            String filename = linkedGUI.getFilenamePreviewText();
            File destinationDirectory = linkedGUI.getDestinationFolder();
            int workerCount = linkedGUI.getWorkerCount();
            downloadPastBroadcast(filename, destinationDirectory, quality, workerCount, twitchVideo);
        } else if (evt.getPropertyName().equals("concatVideoParts")) {
            if(evt.getNewValue() instanceof GuiPropertyChangeValue) {
                GuiPropertyChangeValue newValue = (GuiPropertyChangeValue) evt.getNewValue();
                convertVideoPartsToMP4(newValue.get("ffmpegOptions"), newValue.getPropertyChangeListener());
            }
        } else if(evt.getPropertyName().equals("downloadFFMPEG")) {
            if(evt.getNewValue() instanceof  GuiPropertyChangeValue) {
                GuiPropertyChangeValue newValue = (GuiPropertyChangeValue) evt.getNewValue();
                downloadFFMPEG(newValue.getPropertyChangeListener());
            }
        }
    }

    private String recomendedFFMpegOptions() {
        URL twitchUrl = twitchVideo.getUrl();
        if(Pattern.matches("http://www.twitch.tv/\\w+/b/\\d+", twitchUrl.toString())) {
            return "-c copy";
        } else if(Pattern.matches("http://www.twitch.tv/\\w+/c/\\d+", twitchUrl.toString())) {
            return "-c copy";
        } else if(Pattern.matches("http://www.twitch.tv/\\w+/v/\\d+", twitchUrl.toString())) {
            return "-c:v libx264 -c:a copy -bsf:a aac_adtstoasc";
        }
        return null;
    }



}
