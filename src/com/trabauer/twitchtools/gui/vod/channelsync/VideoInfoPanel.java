package com.trabauer.twitchtools.gui.vod.channelsync;

import com.trabauer.twitchtools.controller.channelsync.ChannelSyncControllerInterface;
import com.trabauer.twitchtools.model.twitch.TwitchVideoInfo;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.TextAttribute;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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

    private final boolean debugColors = true;

    private final JLabel titleLbl;
    private final JLabel imageLbl;
    private final JLabel durationLbl;
    private final JLabel viewcountLbl;
    private final TwitchVideoInfo relatedTwitchVideoInfoObject;
    private final ChannelSyncControllerInterface controller;

    private final JPanel btnPanel;
    private final JButton downloadBtn;
    private final JButton playBtn;
    private final JButton convertBtn;
    private final JButton deleteBtn;

    private final JLabel channelDisplayNameLbl;
    private final JCheckBox markForBatchCheckbo;
    private final JLabel linkToTwitchLbl;
    private final JLabel dateLbl;
    private final JLayeredPane previewImageLayeredPane;


    // Borders
    private static int borderWidth = 5;
    private static final Color selectedColor = new Color(165, 89, 243, 255);
    private static final Color unselectedColor =  new Color(0, 0, 0, 50);
    private static final Color downloadedColor = Color.GREEN;
    private static final Color downloadingColor = Color.YELLOW;
    private static final Color convertingColor = Color.ORANGE;
    private static final Border selectedBorder = BorderFactory.createMatteBorder(borderWidth,borderWidth,borderWidth,borderWidth,selectedColor);
    private static final Border unselectedBorder = BorderFactory.createMatteBorder(borderWidth,borderWidth,borderWidth,borderWidth, unselectedColor);
    private static final Border downloadedBorder = BorderFactory.createMatteBorder(borderWidth,borderWidth,borderWidth,borderWidth,downloadedColor);
    private static final Border downloadingBorder = BorderFactory.createMatteBorder(borderWidth, borderWidth, borderWidth, borderWidth, downloadingColor);
    private static final Border convertingBorder = BorderFactory.createMatteBorder(borderWidth, borderWidth, borderWidth, borderWidth, convertingColor);

    public VideoInfoPanel(final TwitchVideoInfo relatedTwitchVideoInfoObject, final ChannelSyncControllerInterface controller) throws IOException {
        this.relatedTwitchVideoInfoObject = relatedTwitchVideoInfoObject;
        this.relatedTwitchVideoInfoObject.addPropertyChangeListenern(this);
        this.controller = controller;
        setLayout(new GridBagLayout());


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

        imageLbl = new JLabel();

        if(relatedTwitchVideoInfoObject.getPreviewImage()!=null) {
            addImageToThis();
        } else { //loadImage in background (improves performance with small connections)
            Runnable getPreviewRunnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        relatedTwitchVideoInfoObject.loadPreviewImage();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            Thread t = new Thread(getPreviewRunnable);
            t.start();
        }



        imageLbl.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if( ! relatedTwitchVideoInfoObject.isDownloaded()) {
                    if (isSelected()) {
                        markForBatchCheckbo.setSelected(false);
                        relatedTwitchVideoInfoObject.setState(TwitchVideoInfo.State.INITIAL);
                    } else {
                        markForBatchCheckbo.setSelected(true);
                        relatedTwitchVideoInfoObject.setState(TwitchVideoInfo.State.SELECTED_FOR_DOWNLOAD);
                    }
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if(relatedTwitchVideoInfoObject.getState().equals(TwitchVideoInfo.State.INITIAL)) {
                    if (isSelected()) setBorder(unselectedBorder);
                    else setBorder(selectedBorder);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if(relatedTwitchVideoInfoObject.getState().equals(TwitchVideoInfo.State.INITIAL)) {
                    if (isSelected()) setBorder(selectedBorder);
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

        playBtn = new JButton("Play");
        playBtn.setActionCommand("watchVideo");
        playBtn.addActionListener(this);


        convertBtn = new JButton("Convert");
        convertBtn.addActionListener(this);

        deleteBtn = new JButton("Delete");
        deleteBtn.addActionListener(this);

//        if(relatedTwitchVideoInfoObject.isDownloaded()) {
//            setDownloadedLayout();
//        }


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
        btnPanel.add(playBtn);
        add(btnPanel, c);

        setInitialLayout();
        changeLookAndFeelBasedOnState(relatedTwitchVideoInfoObject.getState());

    }

    private void setInitialLayout() {
        downloadBtn.setVisible(true);
        playBtn.setVisible(false);
        deleteBtn.setVisible(false);
        convertBtn.setVisible(false);
        setBorder(unselectedBorder);
    }

    private void setDownloadingLayout() {
        setInitialLayout();
        setQueuedLayout();
        setBorder(downloadingBorder);
    }


    private void setDownloadedLayout() {
        downloadBtn.setVisible(false);
        deleteBtn.setVisible(true);
        deleteBtn.setEnabled(true);
        playBtn.setVisible(true);
        convertBtn.setVisible(true);
        convertBtn.setEnabled(true);
        markForBatchCheckbo.setEnabled(false);
        markForBatchCheckbo.setVisible(false);
        setBorder(downloadedBorder);
    }

    private void setConvertedLayout() {
        setDownloadedLayout();
        convertBtn.setVisible(false);
        convertBtn.setEnabled(false);
    }

    private void setConvertingLayout() {
        setDownloadedLayout();
        convertBtn.setEnabled(false);
        setBorder(convertingBorder);
    }

    private void setQueuedLayout() {
        convertBtn.setEnabled(false);
        deleteBtn.setEnabled(false);
        downloadBtn.setEnabled(false);
        markForBatchCheckbo.setEnabled(false);
    }





    @Override
    public void actionPerformed(ActionEvent e)  {
        if(e.getSource()== downloadBtn) {
            System.out.println("DownloadBtn of " + relatedTwitchVideoInfoObject.getTitle() + " pressed");
                controller.downloadTwitchVideo(relatedTwitchVideoInfoObject);
        } else if(e.getSource() == convertBtn) {
            controller.convert2mp4(this.relatedTwitchVideoInfoObject);
        } else if(e.getSource() == playBtn) {
            System.out.println("Watch Btn pressed opening " + relatedTwitchVideoInfoObject.getRelatedFileOnDisk().getName());
            try {
                Desktop.getDesktop().open(relatedTwitchVideoInfoObject.getRelatedFileOnDisk());
            } catch (IOException e1) {
                e1.printStackTrace();
            }

        } else if(e.getSource() == deleteBtn) {

        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if(e.getSource()==markForBatchCheckbo) {
            if(e.getStateChange() == ItemEvent.SELECTED) {
                if(relatedTwitchVideoInfoObject.getState().equals(TwitchVideoInfo.State.INITIAL)) {
                    relatedTwitchVideoInfoObject.setState(TwitchVideoInfo.State.SELECTED_FOR_DOWNLOAD);
                }
            } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                if(relatedTwitchVideoInfoObject.getState().equals(TwitchVideoInfo.State.SELECTED_FOR_DOWNLOAD)) {
                    relatedTwitchVideoInfoObject.setState(TwitchVideoInfo.State.INITIAL);
                }
            }
        }
    }


    public boolean isSelected() {
        if(relatedTwitchVideoInfoObject.getState().equals(TwitchVideoInfo.State.SELECTED_FOR_DOWNLOAD)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if(evt.getSource() == relatedTwitchVideoInfoObject)
            if (evt.getPropertyName().equals("previewImage")) {  // Performance imnprovement
                // Loading the Image is done in a background Thread. This adds the Image Proeview when its done.
                try {
                    addImageToThis();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if(evt.getPropertyName().equals("state")) {
                TwitchVideoInfo.State currentState = (TwitchVideoInfo.State) evt.getNewValue();
                changeLookAndFeelBasedOnState(currentState);
            }
    }


    private void changeLookAndFeelBasedOnState(TwitchVideoInfo.State currentState) {
        if(currentState.equals(TwitchVideoInfo.State.SELECTED_FOR_DOWNLOAD)) {
            setBorder(selectedBorder);
            markForBatchCheckbo.setSelected(true);
        } else {
            markForBatchCheckbo.setSelected(false);
        }
        if(currentState.equals(TwitchVideoInfo.State.QUEUED_FOR_DOWNLOAD)) { //Video is in a Queue
            downloadBtn.setEnabled(false);
            convertBtn.setEnabled(false);
            markForBatchCheckbo.setEnabled(false);
            if(debugColors) {
                setBorder(BorderFactory.createMatteBorder(borderWidth, borderWidth, borderWidth, borderWidth, Color.CYAN));
            } else {
                setBorder(selectedBorder);
            }
        }
        if(currentState.equals(TwitchVideoInfo.State.DOWNLOADING)) {
            setDownloadingLayout();
        }
        if(currentState.equals(TwitchVideoInfo.State.DOWNLOADED)) {
            setDownloadedLayout();
        }
        if(currentState.equals(TwitchVideoInfo.State.QUEUED_FOR_CONVERT)) {
            setConvertingLayout();
            if(debugColors) {
                setBorder(BorderFactory.createMatteBorder(borderWidth, borderWidth, borderWidth, borderWidth, Color.blue));
            }
        }
        if(currentState.equals(TwitchVideoInfo.State.CONVERTING)) {
            setConvertingLayout();
        }
        if(currentState.equals(TwitchVideoInfo.State.CONVERTED)) {
            setConvertedLayout();
        }
        if(currentState.equals(TwitchVideoInfo.State.INITIAL)) {
            setInitialLayout();
        }



    }

    public void addImageToThis() throws IOException {
        imageLbl.setIcon(new ImageIcon(relatedTwitchVideoInfoObject.getPreviewImage()));
        imageLbl.repaint();
    }


}
