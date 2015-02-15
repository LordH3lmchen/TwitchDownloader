package com.trabauer.twitchtools.gui.vod.channelsync;

import com.trabauer.twitchtools.controller.channelsync.ChannelSyncControllerInterface;
import com.trabauer.twitchtools.model.twitch.TwitchVideoInfo;
import com.trabauer.twitchtools.utils.OsUtils;
import com.trabauer.twitchtools.utils.TwitchToolPreferences;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.TextAttribute;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Map;

/**
 *
 * Represents one TwitchVideoInfo Object in the Search REsult Area in th GUI.
 *
 *
 *
 *
 * Created by Flo on 22.01.2015.
 */
public class VideoInfoPanel extends JPanel implements ItemListener, ActionListener, PropertyChangeListener {
    private final JLabel titleLbl;
    private final JLabel imageLbl;
    private final JLabel durationLbl;
    private final JLabel viewcountLbl;
    private final TwitchVideoInfo relatedTwitchVideoInfoObject;
    private final ChannelSyncControllerInterface controller;

    private final JPanel btnPanel;
    private final JButton downloadBtn;
    private final JButton convertBtn;
    private final JButton deleteBtn;

    private final JLabel channelDisplayNameLbl;
    private final JCheckBox markForBatchCheckbo;
    private final JLabel linkToTwitchLbl;
    private final JLabel dateLbl;
    private final JLayeredPane previewImageLayeredPane;
    private final Color selectedColor;
    private final Color unselectedColor;
    private final Color downloadedColor;
    private final Border selectedBorder;
    private final Border unselectedBorder;
    private final Border downloadedBorder;
    private boolean isSelected;


    public VideoInfoPanel(final TwitchVideoInfo relatedTwitchVideoInfoObject, final ChannelSyncControllerInterface controller) throws IOException {
        this.relatedTwitchVideoInfoObject = relatedTwitchVideoInfoObject;
        this.relatedTwitchVideoInfoObject.addPropertyChangeListenern(this);
        this.controller = controller;
        setLayout(new GridBagLayout());

        int borderWidth = 5;
        selectedColor = new Color(165, 89, 243, 255);
        selectedBorder = BorderFactory.createMatteBorder(borderWidth,borderWidth,borderWidth,borderWidth,selectedColor);
        unselectedColor = new Color(0, 0, 0, 50);
        unselectedBorder = BorderFactory.createMatteBorder(borderWidth,borderWidth,borderWidth,borderWidth, unselectedColor);
        downloadedColor = Color.green;
        downloadedBorder = BorderFactory.createMatteBorder(borderWidth,borderWidth,borderWidth,borderWidth,downloadedColor);



        setBorder(unselectedBorder);
        this.isSelected=false;

        String title = relatedTwitchVideoInfoObject.getTitle();
        int cutlength = 40;
        if(title.length()>cutlength) {
            title = title.substring(0, cutlength);
            title = title.concat("...");
        }
        titleLbl = new JLabel(title);
        Font original = titleLbl.getFont();
        titleLbl.setFont(original.deriveFont(Font.BOLD, 14.0F));
        viewcountLbl = new JLabel(String.format("%d views", relatedTwitchVideoInfoObject.getViews()));
        viewcountLbl.setForeground(Color.WHITE);
        viewcountLbl.setBackground(new Color(0,0,0,0));
        original = viewcountLbl.getFont();
        viewcountLbl.setFont(original.deriveFont(Font.BOLD));

        int duration = relatedTwitchVideoInfoObject.getLength();
        int seconds = duration%60;
        int minutes = (duration/60)%60;
        int hours = duration/(60*60);
        durationLbl = new JLabel(String.format("%5d:%02d:%02d", hours, minutes, seconds));
        durationLbl.setFont(original.deriveFont(Font.BOLD));
        durationLbl.setForeground(Color.WHITE);

        Image previewImage = relatedTwitchVideoInfoObject.getPreviewImage();
        imageLbl = new JLabel(new ImageIcon(previewImage));
        imageLbl.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if( ! relatedTwitchVideoInfoObject.isDownloaded()) {
                    if (isSelected) {
                        markForBatchCheckbo.setSelected(false);
                        isSelected = false;
                    } else {
                        markForBatchCheckbo.setSelected(true);
                        isSelected = true;
                    }
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if(! relatedTwitchVideoInfoObject.isDownloaded() ) {
                    if (isSelected) setBorder(unselectedBorder);
                    else setBorder(selectedBorder);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if( ! relatedTwitchVideoInfoObject.isDownloaded()) {
                    if (isSelected) setBorder(selectedBorder);
                    else setBorder(unselectedBorder);
                }
            }
        });
        markForBatchCheckbo = new JCheckBox("add to queue");
        markForBatchCheckbo.addItemListener(this);
        channelDisplayNameLbl = new JLabel(relatedTwitchVideoInfoObject.getChannelDisplaylName());
        linkToTwitchLbl = new JLabel("watch on Twitch");
        original = linkToTwitchLbl.getFont();
        Map attributes = original.getAttributes();
        attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        linkToTwitchLbl.setFont(original.deriveFont(attributes));
        linkToTwitchLbl.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                try {
                    controller.openUrlInBrowser(relatedTwitchVideoInfoObject.getUrl());
                } catch (MalformedURLException e1) {
                    e1.printStackTrace();
                }
            }
        });


        linkToTwitchLbl.setForeground(Color.BLUE);
        dateLbl = new JLabel(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(relatedTwitchVideoInfoObject.getRecordedAt().getTime()));

        btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 0));

        downloadBtn = new JButton("Download");
        downloadBtn.addActionListener(this);
        downloadBtn.setVisible(false);// Nothing implemented yet for that

        convertBtn = new JButton("Convert");
        convertBtn.addActionListener(this);
        convertBtn.setVisible(false);

        deleteBtn = new JButton("Delete");
        deleteBtn.addActionListener(this);
        deleteBtn.setVisible(false);

        if(relatedTwitchVideoInfoObject.isDownloaded()) {
            setDownloadedLayout();
        } else {
            downloadBtn.setActionCommand("downloadVideo");
        }


        previewImageLayeredPane = new JLayeredPane();



        layoutComponents();





    }

    private void layoutComponents() {

        imageLbl.setBounds(0,0 ,320,180);
        viewcountLbl.setBounds(5,0,100,25);
        durationLbl.setBounds(255,0,100,25);
        //Adding a dark bar to the previewImage to increase the readability of the viewcount and duration
        JLabel darkBarkLbl = new JLabel();
        darkBarkLbl.setBounds(0,0,320,25);
        darkBarkLbl.setBackground(new Color(0, 0, 0, 80));
        darkBarkLbl.setOpaque(true);
        darkBarkLbl.setVerticalAlignment(JLabel.TOP);
        darkBarkLbl.setHorizontalAlignment(JLabel.CENTER);
        previewImageLayeredPane.setPreferredSize(new Dimension(315, 180));
        previewImageLayeredPane.add(durationLbl, 5);
        previewImageLayeredPane.add(viewcountLbl, 5);
        previewImageLayeredPane.add(darkBarkLbl,7);
        previewImageLayeredPane.add(imageLbl,10);
        previewImageLayeredPane.setBackground(Color.yellow);
        //previewImageLayeredPane.add(viewcountLbl, 1);


        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(0, 0, 0, 0);
        c.ipadx=3;
        c.ipady=3;
        c.gridx=0;
        c.gridy=0;
        c.gridwidth=2;
        c.anchor=GridBagConstraints.LINE_START;
        add(previewImageLayeredPane, c);

        c.insets = new Insets(2, 2, 2, 2);
        c.gridy++;
        c.gridwidth=2;
        // title.           //TODO limit width
        add(titleLbl, c);
        titleLbl.setMinimumSize(new Dimension(300, 20));
        titleLbl.setMaximumSize(new Dimension(300, 100));
        titleLbl.setPreferredSize(new Dimension(300, 20));

        c.gridwidth=1;
        c.gridy++;
        add(channelDisplayNameLbl, c);

        c.gridx=1;
        c.anchor=GridBagConstraints.LINE_END;
        add(dateLbl, c);

        c.gridx=0;
        c.gridy++;
        c.anchor=GridBagConstraints.LINE_START;
        add(linkToTwitchLbl, c);


        c.gridy++;
        add(markForBatchCheckbo, c);

        c.gridx++;
        c.anchor=GridBagConstraints.LINE_END;

        btnPanel.add(deleteBtn);
        btnPanel.add(convertBtn);
        btnPanel.add(downloadBtn);
        add(btnPanel, c);
    }

    private void setDownloadedLayout() {
        downloadBtn.setText("Play");
        downloadBtn.setActionCommand("watchVideo");
        downloadBtn.setVisible(true);
        deleteBtn.setVisible(true);
        markForBatchCheckbo.setEnabled(false);
        markForBatchCheckbo.setVisible(false);
        setBorder(downloadedBorder);

        String fileExtension = OsUtils.getFileExtension(relatedTwitchVideoInfoObject.getRelatedFileOnDisk());
        if(! fileExtension.equals(".mp4")) {
            convertBtn.setVisible(true);
        }
    }



    @Override
    public void actionPerformed(ActionEvent e)  {
        if(e.getSource()==downloadBtn) {
            if (e.getActionCommand().equals("downloadVideo")) {
                try {
                    relatedTwitchVideoInfoObject.getDownloadInfo();
                    /// .... TODO implement single download
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                System.out.println("DownloadBtn of " + relatedTwitchVideoInfoObject.getTitle() + " pressed");
            } else if(e.getActionCommand().equals("watchVideo")) {
                System.out.println("Watch Btn pressed opening " + relatedTwitchVideoInfoObject.getRelatedFileOnDisk().getName());
                try {
                    Desktop.getDesktop().open(relatedTwitchVideoInfoObject.getRelatedFileOnDisk());
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        } else if(e.getSource() == convertBtn) {
            controller.convert2mp4(this.relatedTwitchVideoInfoObject);
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if(e.getSource()==markForBatchCheckbo) {
            if(e.getStateChange() == ItemEvent.SELECTED) {
                //ChangeBorder
                setBorder(selectedBorder);
                isSelected = true;
                relatedTwitchVideoInfoObject.setSelectedForDownload(true);
            } else {
                setBorder(unselectedBorder);
                isSelected = false;
                relatedTwitchVideoInfoObject.setSelectedForDownload(false);
            }
        }
    }


    public boolean isSelected() {
        return isSelected;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if(evt.getSource() == relatedTwitchVideoInfoObject) {
            if (evt.getPropertyName().equals("isSelectedForDownload")) {
                if (relatedTwitchVideoInfoObject.isSelectedForDownload()) {
                    markForBatchCheckbo.setSelected(true);
                } else {
                    markForBatchCheckbo.setSelected(false);
                }
            } else if (evt.getPropertyName().equals("relatedFile")) {
                if (evt.getNewValue() instanceof File) {
                    if (relatedTwitchVideoInfoObject.isDownloaded()) {
                        relatedTwitchVideoInfoObject.setSelectedForDownload(false);
                        setDownloadedLayout();
                    }
                    if(OsUtils.getFileExtension(relatedTwitchVideoInfoObject.getRelatedFileOnDisk()).equals(".mp4")) {
                        convertBtn.setVisible(false);
                    }
                }
            }
        }
    }



}
