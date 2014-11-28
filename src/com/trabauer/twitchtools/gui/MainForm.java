package com.trabauer.twitchtools.gui;

import com.trabauer.twitchtools.controller.Controller;
import com.trabauer.twitchtools.utils.OsValidator;
import com.trabauer.twitchtools.utils.UnsupportedOsException;

import javax.swing.*;
import javax.swing.plaf.FileChooserUI;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;

/**
 * Created by Flo on 13.11.2014.
 */
public class MainForm {
    private JPanel mainPanel;
    private JPanel southPanel;
    private JPanel centerPanel;
    private JButton downloadButton;
    private JTextField twitchUrlTextField;
    private JTextField destFolderTextField;
    private JSpinner bandwidthSpinner;
    private JComboBox filenameTextField;
    private JButton getStreamInfoButton;
    private JLabel filenamePreviewLabel;
    private JButton selectDestFolderButton;
    private JPanel bandWidthPanel;
    private JPanel destFolderPanel;
    private JTextArea streamInformationTextArea;

    private JFileChooser fileChooser;

    private ArrayList<ActionListener> actionListeners;
    private ArrayList<MainFormListener> mainFormListeners;

    private Controller controller;

    public MainForm(final Controller controller) {

        this.controller = controller;




        getStreamInfoButton.setActionCommand("getStreamInfoBtnPressed");
        getStreamInfoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String streamInformationString = "";
                LinkedHashMap<String,String> streamInformation = null;
                try {
                    controller.selectVideo(new URL(twitchUrlTextField.getText()));
                    //streamInformation = controller.getStreamInformation();
                } catch (MalformedURLException e1) {
                    e1.printStackTrace();
                }
                for(String key: streamInformation.keySet()) {
                    streamInformationString += key + ": " + streamInformation.get(key) + "\n";
                }
                streamInformationTextArea.setText(streamInformationString);
            }
        });

        selectDestFolderButton.setActionCommand("selectDestDirBtnPressed");
        selectDestFolderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(e.getActionCommand().equals("selectDestDirBtnPressed")) {
                    MainForm.this.fileChooser = null;
                    if(OsValidator.isWindows()) {
                        fileChooser = new JFileChooser(System.getenv().get("USERPROFILE"));
                    }
                    else if(OsValidator.isMac()) {
                        fileChooser = new JFileChooser(System.getenv().get("HOME"));
                    }
                    else if(OsValidator.isUnix()) {
                        fileChooser = new JFileChooser(System.getenv().get("HOME"));
                    }
                    else {
                        try {
                            throw new UnsupportedOsException();
                        } catch (UnsupportedOsException e1) {
                            e1.printStackTrace();
                        }
                    }

//                    System.out.println(fileChooser);
                    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    if(fileChooser != null) {
                        int returnVal = fileChooser.showOpenDialog(MainForm.this.mainPanel);

                        if( returnVal == JFileChooser.APPROVE_OPTION) {
                            File file = fileChooser.getSelectedFile();
                            MainForm.this.destFolderTextField.setText(file.toString());
                        }
                    }
                }
            }
        });




        downloadButton.setActionCommand("DownloadBtnPressed");
        downloadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if(e.getActionCommand().equals("DownloadBtnPressed")) {
                    String twitchUrl = twitchUrlTextField.getText();
                    String destinationFolder = destFolderTextField.getText();
                    String filename = "";
                    if (filenameTextField.getSelectedItem() != null)
                        filename = filenameTextField.getSelectedItem().toString();
                    int bandwidthInMBitPerSecond = (Integer) bandwidthSpinner.getValue();

                    MainFormEvent mainFormEvent = new MainFormEvent(
                            this,
                            twitchUrl,
                            destinationFolder,
                            filename,
                            bandwidthInMBitPerSecond);

                    for (MainFormListener mainFormListener : mainFormListeners) {
                        if (mainFormListener != null) {
                            mainFormListener.mainFormEventOccurred(mainFormEvent);
                        }
                    }
                }
            }
        });
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public void addMainFormListener(MainFormListener mainFormListener) {
        if(this.mainFormListeners == null) {
            this.mainFormListeners = new ArrayList<MainFormListener>();
        }
        mainFormListeners.add(mainFormListener);
    }

    public void addActionListener(ActionListener actionListener) {
        if(actionListener == null) {
            this.actionListeners = new ArrayList<ActionListener>();
        }
        actionListeners.add(actionListener);
    }

    public void setFilenamePreviewLabelText(String filenamePreviewLabelText) {
        filenamePreviewLabel.setText(filenamePreviewLabelText);
    }
}
