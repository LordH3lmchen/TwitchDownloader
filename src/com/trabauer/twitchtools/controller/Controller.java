package com.trabauer.twitchtools.controller;

import com.trabauer.twitchtools.TwitchToolsApp;
import com.trabauer.twitchtools.gui.*;
import com.trabauer.twitchtools.model.FilenamePatternsComboBoxModel;
import com.trabauer.twitchtools.model.VideoQualityComboBoxModel;
import com.trabauer.twitchtools.model.twitch.TwitchDownloadQueue;
import com.trabauer.twitchtools.model.twitch.TwitchVideo;
import com.trabauer.twitchtools.tasks.FFMpegConverterWorker;
import com.trabauer.twitchtools.tasks.HttpFileDownloadWorker;
import com.trabauer.twitchtools.tasks.TwitchDownloadWorker;
import com.trabauer.twitchtools.utils.OsUtils;
import com.trabauer.twitchtools.utils.StreamCapturer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.*;
import java.util.prefs.*;

/**
 * Created by Flo on 13.11.2014.
 */
public class Controller implements ActionListener , Observer, NodeChangeListener, PreferenceChangeListener {

    private static final String FFMPEG_EXE_URL_STR = "http://trabauer.com/downloads/project_ressources/TwitchTools/ffmpeg.exe";

    private TwitchVideo twitchVideo = null;
    private File destinationDirectory;
    private MainFrame mainFrame;
    private FilenamePatternsComboBoxModel filenamePatternsComboBoxModel;
    private VideoQualityComboBoxModel videoQualityComboBoxModel;
    private DownloadStep1 downloadStep1;
    private DownloadStep2 downloadStep2;
    private DownloadStep3 downloadStep3;
    private DownloadStep4 downloadStep4;

    private Preferences prefs;

    private static String DESTINATION_DIR_PREFKEY = "destinationDir",
                          FILENAME_PATTERN_PREFKEY = "filenamePattern";

    private File ffmpegExecutable, m3uPlaylist, ffmpegFilelist;


    public Controller() {
        super();
        this.destinationDirectory = null;
        this.mainFrame = new MainFrame();
        mainFrame.setSize(700, 550);
        this.twitchVideo = new TwitchVideo();
        ffmpegExecutable = null;
        try {
            ffmpegExecutable = new File(new File(getJarURI().getPath()).getParent().concat("/ffmpeg.exe"));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }




        prefs = Preferences.userRoot().node("/com/trabauer/twitchtools");
        prefs.addNodeChangeListener(this);
        prefs.addPreferenceChangeListener(this);

        downloadStep1 = mainFrame.getDownloadStep1Form();
        downloadStep2 = mainFrame.getDownloadStep2Form();
        downloadStep3 = mainFrame.getDownloadStep3Form();
        downloadStep4 = mainFrame.getDownloadStep4Form();

        downloadStep1.addActionListener(this);
        downloadStep2.addActionListener(this);
        downloadStep3.addActionListener(this);
        downloadStep4.addActionListener(this);

        if( !ffmpegExecutable.exists() ) {
            downloadStep4.getConcatButton().setActionCommand("downloadFFMPEG");
            downloadStep4.getConcatButton().setText("Download FFMPEG");
        }

        filenamePatternsComboBoxModel = new FilenamePatternsComboBoxModel();
        updateFilenamePatternsComboBoxModel();
        downloadStep2.setFilenamePatternsComboBoxModel(filenamePatternsComboBoxModel);
        downloadStep2.setDestFolderTextField(prefs.get(DESTINATION_DIR_PREFKEY, OsUtils.getUserHome()));

        videoQualityComboBoxModel = new VideoQualityComboBoxModel(twitchVideo);
        downloadStep2.setQualityComboBoxModel(videoQualityComboBoxModel);


        twitchVideo.addObserver(this);
        twitchVideo.addObserver(filenamePatternsComboBoxModel);
        twitchVideo.addObserver(videoQualityComboBoxModel);
        twitchVideo.addObserver(downloadStep2);

        PrintStream ps = System.out;
        System.setOut(new PrintStream(new StreamCapturer("STDOUT", downloadStep4, ps))); //Redirects STDOUT to TextArea and Console(old STDOUT)

        //System.out.println("Das ist ein TEst ob der OutPutRedirectFunktioniert");



    }

    public void selectVideo(URL twitchUrl) {
        this.twitchVideo.updateTwitchVideoByUrl(twitchUrl);
    }

    public TwitchVideo getSelectedTwitchVideo() {
        return twitchVideo;
    }





    @Override
    public void actionPerformed(ActionEvent e) {
        //System.out.println(e.getActionCommand());
        if(e.getActionCommand().equals("selectDestDirBtnPressed")){
            destinationDirectory = mainFrame.showDestinationDirChooser(prefs.get(DESTINATION_DIR_PREFKEY, OsUtils.getUserHome()));
            downloadStep2.setDestFolderTextField(destinationDirectory.toString());
            prefs.put(DESTINATION_DIR_PREFKEY, destinationDirectory.toString());
        } else if (e.getActionCommand().equals("backButton2")) {
            mainFrame.showPreviousCard();
        } else if (e.getActionCommand().equals("filenameChanged")) {
            downloadStep2.setFileNamePreview(parseFilename(twitchVideo.getStreamInformation(), downloadStep2.getFilenameSelection()));
            prefs.put(FILENAME_PATTERN_PREFKEY, downloadStep2.getFilenameSelection());
        } else if ((e.getActionCommand().equals("comboBoxEdited"))&(e.getSource().getClass().equals(JComboBox.class)) ) {
            JComboBox jcb = (JComboBox)e.getSource();
            downloadStep2.setFileNamePreview(parseFilename(twitchVideo.getStreamInformation(), (String)jcb.getEditor().getItem()));
            prefs.put(FILENAME_PATTERN_PREFKEY, (String)jcb.getEditor().getItem());
        } else if (e.getActionCommand().equals("threadCountChanged")) {
            updateBandwidthUsage();
        } else if (e.getActionCommand().equals("qualityChanged")) {
            updateBandwidthUsage();
        } else if (e.getActionCommand().equals("nextButton1")) {
            try {
                twitchVideo.updateTwitchVideoByUrl(new URL(downloadStep1.getTwitchUrl()));
                downloadStep2.setFileNamePreview(parseFilename(twitchVideo.getStreamInformation(), downloadStep2.getFilenameSelection()));
                mainFrame.showNextCard();
            } catch (MalformedURLException e1) {
                e1.printStackTrace();
            }
        } else if (e.getActionCommand().equals("nextButton2")) {
            String quality = downloadStep2.getQuality();
            String filename = downloadStep2.getFilenamePreview();
            String destinationDirectory = downloadStep2.getDestinationFolder();
            int threadcount = downloadStep2.getThreadCount();
            downloadPastBroadcast(filename, destinationDirectory, quality, threadcount, twitchVideo);
            mainFrame.showNextCard();
        } else if (e.getActionCommand().equals("finishButton")) {
            //mainFrame.showFirstStep();
            System.exit(0);
        } else if (e.getActionCommand().equals("nextButton3")) {
            mainFrame.showNextCard();
        } else if (e.getActionCommand().equals("concatButton")) {
            convertVideoPartsToMP4();
        } else if (e.getActionCommand().equals("downloadFFMPEG")) {
            downloadFFMPEG();
        }
    }




    private void downloadFFMPEG() {
        URL ffmpegExeUrl = null;
        try {
            ffmpegExeUrl = new URL(FFMPEG_EXE_URL_STR);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpFileDownloadWorker httpFileDownloadWorker = new HttpFileDownloadWorker(ffmpegExeUrl, ffmpegExecutable);
        httpFileDownloadWorker.addPropertyChangeListener(downloadStep4);
        httpFileDownloadWorker.execute();
    }

    private void convertVideoPartsToMP4() {
        File destinationVideoFile = new File(downloadStep2.getDestinationFolder() + "/" + downloadStep2.getFilenamePreview() + ".mp4");
        FFMpegConverterWorker ffMpegConverterWorker = new FFMpegConverterWorker(destinationVideoFile, this.ffmpegFilelist, this.ffmpegExecutable);
        ffMpegConverterWorker.addPropertyChangeListener(downloadStep4);
        ffMpegConverterWorker.execute();
    }



    private void downloadPastBroadcast(String filename, String destinationDirectory, String quality, int threadcount, TwitchVideo twitchVideo) {
        String filePath = destinationDirectory + "/" + filename;
        File destinationFileTemplate = new File(filePath);
        TwitchDownloadQueue twitchDownloadQueue = new TwitchDownloadQueue(twitchVideo.getTwitchVideoParts(quality));
        String prefixLabelText = new String().format("%2d / %2d", twitchDownloadQueue.peekNextVideoPart().getPartNumber()+1, twitchDownloadQueue.getInitialSize());
        OverallProgressPanel overallProgressPanel = new OverallProgressPanel(twitchDownloadQueue.getInitialSize());
        downloadStep3.addOverallProgressPanel(overallProgressPanel);

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
            downloadStep3.addDownloadProgressPanel(downloadProgressPanel);
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


    /*
    @Override
    public void stepEventOccurred(StepEvent stepEvent) {
        System.err.println(stepEvent);
                if(stepEvent.getStepName().equals("DownloadStepOneNext")) {
                    try {
                        selectVideo(new URL(stepEvent.getUserInput().get("twitchURL")));
                        updateBandwidthUsage();
                        mainFrame.showNextCard();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    downloadStep2.setFileNamePreview(parseFilename(twitchVideo.getStreamInformation(), downloadStep2.getFilenameSelection()));

                } else if(stepEvent.getStepName().equals("DownloadStepTwoNext")) {
                    System.out.println("DownloadStepTwoNext");
                } else if(stepEvent.getStepName().equals("DownloadStepTwoBack")) {
                    System.out.println("DownloadStepTwoBack");
                }private void downloadPastBroadcast(String , String , String , int , TwitchVideo ) {
    }

    }
    */

    private String parseFilename(LinkedHashMap<String, String> streamInformation, String filename) {

        String parsedFilename = new String(filename);
        for(String key: streamInformation.keySet()) {
            String variable = String.format("\\$\\(%s\\)", key).toLowerCase();
            if(streamInformation.get(key)!=null)
                parsedFilename = parsedFilename.replaceAll(variable, streamInformation.get(key));
            else
                parsedFilename = parsedFilename.replaceAll(variable, "");
        }
        parsedFilename = parsedFilename.replaceAll(" ", "_");
        parsedFilename = parsedFilename.replaceAll("[^a-zA-Z0-9\\.\\-/\\\\_]", "");
        return parsedFilename;
    }

    private void updateBandwidthUsage() {
        downloadStep2.getQuality();
        int threadBandwidth;

        // might implement Bandwidth calculation with content length with the first Part of the Twitch Video
        // Speed Values are based on measurements from twitch. Twitch sends the Data at full Speed at the beginning and
        // Slows down after some time. That allows lag free Watching and good Bandwidth allocation for Everyone at home.


        if(downloadStep2.getQuality().equals("source")) {
            threadBandwidth = 585 * 1024 * 8; //BitsPerSecond
        } else if(downloadStep2.getQuality().equals("high")) {
            threadBandwidth = 585 * 1024 * 8; //BitsPerSecond
        } else if(downloadStep2.getQuality().equals("mid")) {
            threadBandwidth = 585 * 1024 * 8; //BitsPerSecond
        } else if(downloadStep2.getQuality().equals("low")) {
            threadBandwidth = 585 * 1024 * 8; //BitsPerSecond
        } else if(downloadStep2.getQuality().equals("mobile")) {
            threadBandwidth = 585 * 1024 * 8; //BitsPerSecond
        } else {
            threadBandwidth = -1;
        }

        float bandwidth = threadBandwidth * downloadStep2.getThreadCount();
        String jLabelText = String.format("%3.2f MBit/s", bandwidth/(1024*1024));
        if(threadBandwidth<0) jLabelText = "unknown";
        downloadStep2.setBandWithUsageJLabelText(jLabelText);
    }

    @Override
    public void update(Observable o, Object arg) {
        if(o.equals(twitchVideo)) {
            //System.out.println("Twitch Video updated!");
            updateFilenamePatternsComboBoxModel();
        }

    }

    private void updateFilenamePatternsComboBoxModel() {
        final String[] EXAMPLE_PATTERNS = {
                "$(game)/$(channel)/$(title)",
                "$(game)/$(channel)/$(title)_$(date)_T$(time)",
                "$(channel)-$(title)",
                "$(channel)/$(title)" };
        String lastUsedPattern = prefs.get(FILENAME_PATTERN_PREFKEY, null);

        if(lastUsedPattern != null)
            if(!filenamePatternsComboBoxModel.containsElement(lastUsedPattern))
                filenamePatternsComboBoxModel.addElement(lastUsedPattern);
        for(String example: EXAMPLE_PATTERNS)
            if (!filenamePatternsComboBoxModel.containsElement(example))
                filenamePatternsComboBoxModel.addElement(example);
        for(String key: twitchVideo.getStreamInformation().keySet()) {
            key = "$("+ key.toLowerCase() + ")";
            if(!filenamePatternsComboBoxModel.containsElement(key))
                filenamePatternsComboBoxModel.addElement(key.toLowerCase());
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
}
