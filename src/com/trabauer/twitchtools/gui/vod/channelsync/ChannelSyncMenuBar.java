package com.trabauer.twitchtools.gui.vod.channelsync;

import com.trabauer.twitchtools.controller.channelsync.ChannelSyncController;
import com.trabauer.twitchtools.gui.images.TwitchToolsImages;
import com.trabauer.twitchtools.utils.OsUtils;
import com.trabauer.twitchtools.utils.TwitchToolPreferences;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Vector;
import java.util.prefs.Preferences;

/**
 * Created by Flo on 30.01.2015.
 */
public class ChannelSyncMenuBar extends JMenuBar implements ActionListener {

    //    private final JMenu fileMenu;
    private final JMenu settingsMenu;
    private final JMenuItem destinationFolderMenuItem;
    //    private final JMenuItem qualityPriorityMenuItem;
    private final ChannelSyncController controller;
    private final Preferences prefs;
    private final SettingsFolderDialog settingsFolderDialog;
    private final AboutThisProgramDialog aboutThisProgramDialog;
    private final JFrame mainFrame;
    private final JMenu viewMenu, helpMenu;
    private final JMenuItem showProgressWindowsMenuItem;
    private final JMenuItem showAboutDialogMenuItem;


    private class SettingsFolderDialog extends JDialog {

        private final JList qualitiesJList;

        public SettingsFolderDialog(Frame owner) {
            super(owner, "Select Destination Folder");
            JLabel destinationFolderLbl = new JLabel("Destination Folder:");
            final JTextField destinationFolderTextField = new JTextField();
            destinationFolderTextField.setText(prefs.get(TwitchToolPreferences.DESTINATION_DIR_PREFKEY, ""));

            JButton selectFolderBtn = new JButton("...");
            selectFolderBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String currentDestinationDirectory = prefs.get(TwitchToolPreferences.DESTINATION_DIR_PREFKEY, OsUtils.getUserHome());
                    File destinationDirectory = showDestinationDirChooser(currentDestinationDirectory);
                    destinationFolderTextField.setText(destinationDirectory.getPath());
                }
            });

            JLabel helpTextLbl = new JLabel("<html>You are able to move the qualities up and down. " +
                    "TwitchTools trys to download the video qualities in that order. " +
                    "If the quality at the top isn't available, it will try to download the " +
                    "second, third ... and so on.</html>");


            JButton okBtn = new JButton("OK");
            okBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    prefs.put(TwitchToolPreferences.DESTINATION_DIR_PREFKEY, destinationFolderTextField.getText());
                    settingsFolderDialog.setVisible(false);
                }
            });


            JButton cancelBtn = new JButton("Cancel");
            cancelBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    settingsFolderDialog.setVisible(false);
                }
            });

            JButton upBtn = new JButton("Up");
            upBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int index = qualitiesJList.getSelectedIndex();
                    moveQuality(index, index - 1);
                }
            });


            JButton downBtn = new JButton("Down");
            downBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int index = qualitiesJList.getSelectedIndex();
                    moveQuality(index, index+1);
                }
            });

            qualitiesJList = new JList(TwitchToolPreferences.getQualityOrder());
            qualitiesJList.setSelectedIndex(ListSelectionModel.SINGLE_SELECTION);
            qualitiesJList.setBorder(BorderFactory.createTitledBorder("Quality Priority"));

            GridBagLayout formLayout = new GridBagLayout();

            JPanel formPanel = new JPanel(formLayout);
            JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

            JPanel contentPane = new JPanel(new BorderLayout());
            this.setContentPane(contentPane);

            contentPane.add(formPanel, BorderLayout.CENTER);
            contentPane.add(controlsPanel, BorderLayout.PAGE_END);

            controlsPanel.add(cancelBtn);
            controlsPanel.add(okBtn);


            GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(5, 5, 5, 5);


            c.gridx = 0;
            c.gridy = 0;
            c.anchor = GridBagConstraints.LINE_END;
            formPanel.add(destinationFolderLbl, c);

            c.fill = GridBagConstraints.HORIZONTAL;
            c.weightx = 1.0;
            c.gridx++;
            c.anchor = GridBagConstraints.CENTER;
            formPanel.add(destinationFolderTextField, c);


            c.gridx++;
            c.weightx = 0.0;
            c.anchor = GridBagConstraints.LINE_END;
            c.fill = GridBagConstraints.NONE;
            formPanel.add(selectFolderBtn, c);

            c.gridy++;
            c.gridx = 0;
            c.gridwidth = 3;
            c.anchor = GridBagConstraints.CENTER;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.weightx = 1.0;

            formPanel.add(qualitiesJList, c);


            JPanel upDownPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            upDownPanel.add(downBtn);
            upDownPanel.add(upBtn);

            c.gridy++;
            formPanel.add(upDownPanel, c);

            c.gridy++;


            helpTextLbl.setPreferredSize(new Dimension(400,100));
            formPanel.add(helpTextLbl, c);

            //setSize(400, 500);
            pack();



        }

        private void moveQuality(int oldIndex, int newIndex) {
            Vector<String> qualities = TwitchToolPreferences.getQualityOrder();
            if(newIndex<0 || newIndex>(qualities.size()-1))
                return;
            String quality = qualities.elementAt(oldIndex);
            qualities.remove(oldIndex);
            qualities.add(newIndex, quality);
            TwitchToolPreferences.setQualityOrder(qualities);
            qualitiesJList.setListData(TwitchToolPreferences.getQualityOrder());
            qualitiesJList.updateUI();
            qualitiesJList.setSelectedIndex(newIndex);
        }
    }



    private class AboutThisProgramDialog extends JDialog {
        public AboutThisProgramDialog(Frame owner) {
            super(owner, "About Twitch Downloader");


            ImageIcon logoIcon = new ImageIcon(TwitchToolsImages.getTwitchDownloadToolImage());
            JLabel programNameVersionLabel = new JLabel("TwitchDownloader 0.1", logoIcon, JLabel.CENTER);
            Font origFont = programNameVersionLabel.getFont().deriveFont(Font.BOLD);
            programNameVersionLabel.setFont(origFont.deriveFont(30.0F));
            programNameVersionLabel.setVerticalTextPosition(JLabel.BOTTOM);
            programNameVersionLabel.setHorizontalTextPosition(JLabel.CENTER);
            add(programNameVersionLabel);

            setVisible(true);
            pack();

        }
    }

    private File showDestinationDirChooser(String path) {
        JFileChooser fileChooser = null;
        File file = null;
        fileChooser = new JFileChooser(path);
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (fileChooser != null) {
            int returnVal = fileChooser.showOpenDialog(this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                file = fileChooser.getSelectedFile();
            }
        }
        return file;
    }


    public ChannelSyncMenuBar(ChannelSyncController controller, JFrame mainFrame) {
        this.controller = controller;
        this.prefs = TwitchToolPreferences.getInstance();
        this.mainFrame = mainFrame;

        mainFrame.setJMenuBar(this);

        settingsMenu = new JMenu("Edit");
        this.settingsFolderDialog = new SettingsFolderDialog(mainFrame);
        destinationFolderMenuItem = new JMenuItem("Settings");
        destinationFolderMenuItem.addActionListener(this);
        settingsMenu.add(destinationFolderMenuItem);
        add(settingsMenu);

        viewMenu = new JMenu("View");
        showProgressWindowsMenuItem = new JMenuItem("Log Window");
        showProgressWindowsMenuItem.addActionListener(this);
        viewMenu.add(showProgressWindowsMenuItem);
        add(viewMenu);

        helpMenu = new JMenu("Help");
        showAboutDialogMenuItem = new JMenuItem("About");
        showAboutDialogMenuItem.addActionListener(this);
        helpMenu.add(showAboutDialogMenuItem);
        this.aboutThisProgramDialog = new AboutThisProgramDialog(mainFrame);
        add(helpMenu);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == destinationFolderMenuItem) {
            settingsFolderDialog.setVisible(true);
//            } else if (e.getSource() == qualityPriorityMenuItem) {
//                videoQualityDialog.setVisible(true);
        } else if (e.getSource() == showProgressWindowsMenuItem) {
            controller.progressFrameSetVisible(true);
        } else if (e.getSource() == showAboutDialogMenuItem) {
            aboutThisProgramDialog.setVisible(true);
        }
    }



}
