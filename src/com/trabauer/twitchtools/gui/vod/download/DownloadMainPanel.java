package com.trabauer.twitchtools.gui.vod.download;

import com.trabauer.twitchtools.controller.DownloadControllerGuiInterface;
import com.trabauer.twitchtools.gui.ToolsPanel;
import com.trabauer.twitchtools.gui.images.TwitchToolsImages;
import com.trabauer.twitchtools.model.FilenamePatternsComboBoxModel;
import com.trabauer.twitchtools.model.VideoQualityComboBoxModel;
import com.trabauer.twitchtools.utils.GuiPropertyChangeValue;
import com.trabauer.twitchtools.utils.OsUtils;
import com.trabauer.twitchtools.utils.TwitchToolPreferences;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.prefs.Preferences;

/**
 * Created by Flo on 16.01.2015.
 */
public class DownloadMainPanel extends ToolsPanel implements ActionListener, DownloadControllerGuiInterface {

    public final String DOWNLOAD1CARD = "DownloadStep1Form";
    public final String DOWNLOAD2CARD = "DownloadStep2Form";
    public final String DOWNLOAD3CARD = "DownloadStep3Form";
    public final String DOWNLOAD4CARD = "DownloadStep4Form";

    public final String FIRST_CARD_NAME = DOWNLOAD1CARD;
    public final String LAST_CARD_NAME = DOWNLOAD4CARD;

    private JButton
            nextBtn,
            backBtn,
            exitBtn;

    private DownloadStep1Form downloadStep1Form;
    private DownloadStep2Form downloadStep2Form;
    private DownloadStep3Form downloadStep3Form;
    private DownloadStep4Form downloadStep4Form;

    private LinkedList<ActionListener> listeners;

    private JPanel downloadStepPanel;
    private JFileChooser fileChooser;

    private Preferences prefs;
    private File destinationDirectory;

    private String FilenameTemplate;
    private LinkedHashMap<String, String> fileNameVariables;

    private FilenamePatternsComboBoxModel filenamePatternsComboBoxModel;
    private VideoQualityComboBoxModel videoQualityComboBoxModel;



    //GUI Fields
    private URL twitchUrl;
    private GuiPropertyChangeValue guiPropertyChangeValue, ffmpegGuiPropertyChangeValue;
    private String ffmpegOptions;



    public DownloadMainPanel() {
        super("DownloadTwitchVideoTool");
        BorderLayout layout = new BorderLayout();

        setLayout(layout);
        prefs = TwitchToolPreferences.getInstance();


        JPanel exitControlsPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 5, 5));
        JPanel backNextControlsPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING, 5, 5));
        JPanel controlsPanel = new JPanel(new BorderLayout(0,0));
        CardLayout downloadStepPanelLayout = new CardLayout(5,5);
        downloadStepPanel = new JPanel(downloadStepPanelLayout);

        backBtn = new JButton("Back");
        nextBtn = new JButton("Next");
        exitBtn = new JButton("Exit");
        exitBtn.setActionCommand("exitTool");
        exitBtn.addActionListener(this);


        backNextControlsPanel.add(backBtn);
        backNextControlsPanel.add(nextBtn);
        exitControlsPanel.add(exitBtn);

        controlsPanel.add(exitControlsPanel, BorderLayout.LINE_START);
        controlsPanel.add(backNextControlsPanel, BorderLayout.LINE_END);

        downloadStep1Form = new DownloadStep1Form(TwitchToolsImages.getTwitchDownloadToolImage());
        downloadStep2Form = new DownloadStep2Form();
        downloadStep3Form = new DownloadStep3Form();
        downloadStep4Form = new DownloadStep4Form();


        downloadStep1Form.getMainPanel().setName(DOWNLOAD1CARD);
        downloadStep2Form.getMainPanel().setName(DOWNLOAD2CARD);
        downloadStep3Form.getMainPanel().setName(DOWNLOAD3CARD);
        downloadStep4Form.getMainPanel().setName(DOWNLOAD4CARD);

        downloadStepPanel.add(downloadStep1Form.getMainPanel(), DOWNLOAD1CARD);
        downloadStepPanel.add(downloadStep2Form.getMainPanel(), DOWNLOAD2CARD);
        downloadStepPanel.add(downloadStep3Form.getMainPanel(), DOWNLOAD3CARD);
        downloadStepPanel.add(downloadStep4Form.getMainPanel(), DOWNLOAD4CARD);

        add(downloadStepPanel, BorderLayout.CENTER);
        add(controlsPanel, BorderLayout.PAGE_END);




        backBtn.setVisible(false);
        nextBtn.setVisible(true);

        nextBtn.addActionListener(this);

        backBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CardLayout cardLayout = (CardLayout) downloadStepPanel.getLayout();
                cardLayout.previous(downloadStepPanel);
                updateNavigationControls();
            }
        });


        filenamePatternsComboBoxModel = new FilenamePatternsComboBoxModel();
        updateFilenamePatternsComboBoxModel();
        videoQualityComboBoxModel = new VideoQualityComboBoxModel();
        downloadStep2Form.setFilenamePatternsComboBoxModel(filenamePatternsComboBoxModel);
        downloadStep2Form.setDestFolderTextField(prefs.get(TwitchToolPreferences.DESTINATION_DIR_PREFKEY, OsUtils.getUserHome()));
        downloadStep2Form.addActionListener(this);
        downloadStep4Form.addActionListener(this);


        videoQualityComboBoxModel = new VideoQualityComboBoxModel();
        //downloadStep2Form.setQualityComboBoxModel(videoQualityComboBoxModel);


    }

    @Override
    public void lockNavigationControls() {
        nextBtn.setEnabled(false);
        backBtn.setEnabled(false);
        exitBtn.setEnabled(false);
    }

    @Override
    public void unlockNavigationControls() {
        nextBtn.setEnabled(true);
        backBtn.setEnabled(true);
        exitBtn.setEnabled(true);
    }

    @Override
    public void addOverallProgressPanel(OverallProgressPanel overallProgressPanel) {
        overallProgressPanel.addActionListener(this);
        downloadStep3Form.addOverallProgressPanel(overallProgressPanel);
    }

    @Override
    public void addDownloadProgressPanel(DownloadProgressPanel downloadProgressPanel) {
        downloadStep3Form.addDownloadProgressPanel(downloadProgressPanel);
    }

    @Override
    public void setFFMpegOptions(String ffMpegOptions) {
        downloadStep4Form.setFfmpegOptions(ffMpegOptions);
    }

    private void updateNavigationControls() { //Limits the Navigation to allowed ones.
        if(getCurrentCard().getName().equals(DOWNLOAD2CARD)){
            backBtn.setVisible(true);
        } else if(getCurrentCard().getName().equals(LAST_CARD_NAME)){
            nextBtn.setVisible(false);
            backBtn.setVisible(false);
        } else {
            backBtn.setVisible(false);
            nextBtn.setVisible(true);
        }
    }

    private JPanel getCurrentCard() {
        JPanel currentCard = null;
        for(Component comp: downloadStepPanel.getComponents()) {
            if(comp.isVisible()) {
                return (JPanel)comp;
            }
        }
        return null;
    }


    @Override
    public void actionPerformed(ActionEvent e) {

        if(e.getSource() == nextBtn) { // Generate ActionEvents based on NextBtn and getCurrentCard
            if(getCurrentCard().getName().equals(DOWNLOAD1CARD)) {
//                notifyListeners(new ActionEvent(this, 1, "Twitch URL entered")); // STEP1
                firePropertyChange("Twitch URL entered", twitchUrl, twitchUrl=getTwitchURL());
                downloadStep2Form.setFileNamePreview(parseFilename(fileNameVariables, downloadStep2Form.getFilenameSelection()));
            } else if(getCurrentCard().getName().equals(DOWNLOAD2CARD)) {
                GuiPropertyChangeValue newGuiPropertyChangeValue = new GuiPropertyChangeValue();
                newGuiPropertyChangeValue.putValue("quality", getQuality());
                newGuiPropertyChangeValue.putValue("filename", getFilenamePreviewText());
                newGuiPropertyChangeValue.putValue("destinationDirectory", getDestinationFolder().getAbsolutePath());
                newGuiPropertyChangeValue.putValue("workerCount", new Integer(getWorkerCount()).toString());
                firePropertyChange("download settings entered", guiPropertyChangeValue, newGuiPropertyChangeValue);
                //notifyListeners(new ActionEvent(this, 2, "download settings entered")); //STEP2
            } else if(getCurrentCard().getName().equals(DOWNLOAD3CARD)) {
                // Nothing to do
            } else {
                System.out.println("There shoulndt be a NextButton at step 4 ore later");
            }
            CardLayout cardLayout = (CardLayout) downloadStepPanel.getLayout();
            cardLayout.next(downloadStepPanel);
            updateNavigationControls();



            // other UI Control Stuff
        } else if(e.getActionCommand().equals("selectDestDirBtnPressed")){
            destinationDirectory = showDestinationDirChooser(prefs.get(TwitchToolPreferences.DESTINATION_DIR_PREFKEY, OsUtils.getUserHome()));
            downloadStep2Form.setDestFolderTextField(destinationDirectory.toString());
            prefs.put(TwitchToolPreferences.DESTINATION_DIR_PREFKEY, destinationDirectory.toString());
        } else if (e.getActionCommand().equals("filenameChanged")) {
            downloadStep2Form.setFileNamePreview(parseFilename(fileNameVariables, downloadStep2Form.getFilenameSelection()));
            prefs.put(TwitchToolPreferences.FILENAME_PATTERN_PREFKEY, downloadStep2Form.getFilenameSelection());
        } else if ((e.getActionCommand().equals("comboBoxEdited"))&(e.getSource().getClass().equals(JComboBox.class)) ) {
            JComboBox jcb = (JComboBox)e.getSource();
            downloadStep2Form.setFileNamePreview(parseFilename(fileNameVariables, (String)jcb.getEditor().getItem()));
            prefs.put(TwitchToolPreferences.FILENAME_PATTERN_PREFKEY, (String) jcb.getEditor().getItem());
        } else if (e.getActionCommand().equals("threadCountChanged")) {
            updateBandwidthUsage();
        } else if (e.getActionCommand().equals("qualityChanged")) {
            updateBandwidthUsage();
        } else if (e.getActionCommand().equals("concatButton")) {
//            notifyListeners(new ActionEvent(this, 1001, "concatVideoParts"));
            HashMap<String, String> values = new HashMap<String, String>();
            values.put("ffmpegOptions", downloadStep4Form.getFfmpegOptions());
            GuiPropertyChangeValue newFfmpegGuiPropertyChangeValue = new GuiPropertyChangeValue(values, downloadStep4Form);
            firePropertyChange("concatVideoParts", ffmpegGuiPropertyChangeValue, ffmpegGuiPropertyChangeValue=newFfmpegGuiPropertyChangeValue);
        } else if (e.getActionCommand().equals("downloadFFMPEG")) {
//            notifyListeners(new ActionEvent(this, 1002, "downloadFFMPEG"));
            firePropertyChange("downloadFFMPEG", null, new GuiPropertyChangeValue(downloadStep4Form));
        } else if(e.getSource().getClass().equals(OverallProgressPanel.class)) {
//            System.out.println(e.getActionCommand() + " from " + e.getSource());
            if(e.getActionCommand().equals("STARTED")) {
                lockNavigationControls();
            } else if(e.getActionCommand().equals("DONE")) {
                unlockNavigationControls();
            } else {
                unlockNavigationControls();
            }
        } else if(e.getActionCommand().equals("exitTool")) {
            firePropertyChange(e.getActionCommand(), null, null);
        }
        else {
            System.out.println("Don't know how to handle");
            System.out.println(e.getActionCommand() + " from " + e.getSource());
        }
    }

    private File showDestinationDirChooser(String path) {
        fileChooser = null;
        File file = null;
        fileChooser = new JFileChooser(path);
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if(fileChooser != null) {
            int returnVal = fileChooser.showOpenDialog(this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                file = fileChooser.getSelectedFile();
            }
        }
        return file;
    }

    private void updateBandwidthUsage() {
        downloadStep2Form.getQuality();
        int threadBandwidth;

        // might implement Bandwidth calculation with content length with the first Part of the Twitch Video
        // Speed Values are based on measurements from twitch. Twitch sends the Data at full Speed at the beginning and
        // Slows down after some time. That allows lag free Watching and good Bandwidth allocation for Everyone at home.


        if(downloadStep2Form.getQuality().equals("source")) {
            threadBandwidth = 585 * 1024 * 8; //BitsPerSecond
        } else if(downloadStep2Form.getQuality().equals("high")) {
            threadBandwidth = 585 * 1024 * 8; //BitsPerSecond
        } else if(downloadStep2Form.getQuality().equals("mid")) {
            threadBandwidth = 585 * 1024 * 8; //BitsPerSecond
        } else if(downloadStep2Form.getQuality().equals("low")) {
            threadBandwidth = 585 * 1024 * 8; //BitsPerSecond
        } else if(downloadStep2Form.getQuality().equals("mobile")) {
            threadBandwidth = 585 * 1024 * 8; //BitsPerSecond
        } else {
            threadBandwidth = -1;
        }

        float bandwidth = threadBandwidth * downloadStep2Form.getThreadCount();
        String jLabelText = String.format("%3.2f MBit/s", bandwidth/(1024*1024));
        if(threadBandwidth<0) jLabelText = "unknown";
        downloadStep2Form.setBandWithUsageJLabelText(jLabelText);
    }

    private void notifyListeners(ActionEvent ae) {
        for(ActionListener listener: listeners) {
            listener.actionPerformed(ae);
        }
    }

    private void updateFilenamePatternsComboBoxModel() {
        //filenamePatternsComboBoxModel = new FilenamePatternsComboBoxModel();
        final String[] EXAMPLE_PATTERNS = {
                "$(game)/$(channel)/$(title)",
                "$(game)/$(channel)/$(title)_$(date)_T$(time)",
                "$(channel)-$(title)",
                "$(channel)/$(title)" };
        String lastUsedPattern = prefs.get(TwitchToolPreferences.FILENAME_PATTERN_PREFKEY, null);

        if(lastUsedPattern != null)
            if(!filenamePatternsComboBoxModel.containsElement(lastUsedPattern))
                filenamePatternsComboBoxModel.addElement(lastUsedPattern);
        for(String example: EXAMPLE_PATTERNS)
            if (!filenamePatternsComboBoxModel.containsElement(example))
                filenamePatternsComboBoxModel.addElement(example);
        if(fileNameVariables!=null) {
            for (String key : fileNameVariables.keySet()) {
                key = "$(" + key.toLowerCase() + ")";
                if (!filenamePatternsComboBoxModel.containsElement(key))
                    filenamePatternsComboBoxModel.addElement(key.toLowerCase());
            }
        }
        downloadStep2Form.setFilenamePatternsComboBoxModel(filenamePatternsComboBoxModel);
    }


    private String parseFilename(LinkedHashMap<String, String> streamInformation, String filename) {

        String parsedFilename = new String(filename);
        for(String key: streamInformation.keySet()) {
            String variable = String.format("\\$\\(%s\\)", key).toLowerCase();
            if(streamInformation.get(key)!=null) {
                String streamInfoItem = streamInformation.get(key).replaceAll("[^a-zA-Z0-9\\.\\-\\\\_ ]", "");
                parsedFilename = parsedFilename.replaceAll(variable, streamInfoItem);
            }
            else
                parsedFilename = parsedFilename.replaceAll(variable, "");
        }
        parsedFilename = parsedFilename.replaceAll(" ", "_");
        //parsedFilename = parsedFilename.replaceAll("[^a-zA-Z0-9\\.\\-/\\\\_]", "");
        return parsedFilename;
    }

    @Override
    public void setFileNameVariables(LinkedHashMap<String, String> fileNameVariables) {
        this.fileNameVariables = fileNameVariables;
        updateFilenamePatternsComboBoxModel();
    }

    @Override
    public void setFFMpegEnabled(Boolean bool) {
        downloadStep4Form.getConcatButton().setEnabled(bool);
    }


    @Override
    public void setVideoQualityComboBoxModel(VideoQualityComboBoxModel videoQualityComboBoxModel) {
        this.videoQualityComboBoxModel = videoQualityComboBoxModel;
        downloadStep2Form.setQualityComboBoxModel(videoQualityComboBoxModel);
    }


    private URL getTwitchURL() {
        try {
            return downloadStep1Form.getTwitchUrl();
        } catch (MalformedURLException e) {
//            e.printStackTrace();
            System.err.println("Invalid Twitch URL entered");
        }
        return null;
    }

    @Override
    public File getDestinationFolder() {
        return downloadStep2Form.getDestinationFolder();
    }

    @Override
    public String getFilename() {
        return prefs.get(TwitchToolPreferences.FILENAME_PATTERN_PREFKEY, "");
    }

    @Override
    public String getFilenamePreviewText() {
        return downloadStep2Form.getFilenamePreview();
    }

    @Override
    public String getQuality() {
        return downloadStep2Form.getQuality();
    }

    @Override
    public int getWorkerCount() {
        return downloadStep2Form.getThreadCount();
    }

    @Override
    public void setConcatButtonActionCommand(String actionCommand) {
        downloadStep4Form.getConcatButton().setActionCommand(actionCommand);
    }

    @Override
    public void setConcatButtonText(String buttonText) {
        downloadStep4Form.getConcatButton().setText(buttonText);

    }

    @Override
    public void appendText(String text) {
//        downloadStep4Form.appendText(text);
    }

    //public JButton getFfmpegBtn()
}
