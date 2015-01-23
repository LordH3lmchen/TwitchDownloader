package com.trabauer.twitchtools.gui.vod.channelsync;

import com.trabauer.twitchtools.controller.channelsync.ChannelSyncControllerInterface;
import com.trabauer.twitchtools.model.twitch.TwitchVideoInfo;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.TextAttribute;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Map;

/**
 *
 * Represents one TwitchVideoInfo Object in the GUI
 *
 *
 * Created by Flo on 22.01.2015.
 */
public class VideoInfoPanel extends JPanel implements ItemListener, ActionListener{
    private final JLabel titleLbl;
    private final JLabel imageLbl;
    private final TwitchVideoInfo relatedTwitchVideoInfoObject;
    private final ChannelSyncControllerInterface controller;
    private final JButton downloadBtn;
    private final JLabel channelDisplayNameLbl;
    private final JCheckBox markForBatchCheckbo;
    private final JLabel linkToTwitchLbl;
    private final JLabel dateLbl;
    private final JLayeredPane previewImageLayeredPane;
    private final Color selectedColor;
    private final Color unselectedColor;
    private final Border selectedBorder;
    private final Border unselectedBorder;
    private boolean isSelected;


    public VideoInfoPanel(final TwitchVideoInfo relatedTwitchVideoInfoObject, final ChannelSyncControllerInterface controller) throws IOException {
        this.relatedTwitchVideoInfoObject = relatedTwitchVideoInfoObject;
        this.controller = controller;
        setLayout(new GridBagLayout());

        int borderWidth = 5;
        selectedColor = new Color(165, 89, 243, 255);
        selectedBorder = BorderFactory.createMatteBorder(borderWidth,borderWidth,borderWidth,borderWidth,selectedColor);
        unselectedColor = new Color(0, 0, 0, 50);
        unselectedBorder = BorderFactory.createMatteBorder(borderWidth,borderWidth,borderWidth,borderWidth, unselectedColor);

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

        previewImageLayeredPane = new JLayeredPane();

        Image previewImage = relatedTwitchVideoInfoObject.getPreviewImage();
        imageLbl = new JLabel(new ImageIcon(previewImage));
        imageLbl.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(isSelected) {
                    markForBatchCheckbo.setSelected(false);
                    isSelected = false;
                } else {
                    markForBatchCheckbo.setSelected(true);
                    isSelected = true;
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if(isSelected) setBorder(unselectedBorder);
                else setBorder(selectedBorder);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if(isSelected) setBorder(selectedBorder);
                else setBorder(unselectedBorder);
            }
        });
        markForBatchCheckbo = new JCheckBox("add to queue");
        markForBatchCheckbo.addItemListener(this);
        channelDisplayNameLbl = new JLabel(relatedTwitchVideoInfoObject.getChanneDisplaylName());
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

        downloadBtn = new JButton("Download");



        layoutComponents();




    }

    private void layoutComponents() {
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(0, 0, 0, 0);
        c.ipadx=3;
        c.ipady=3;
        c.gridx=0;
        c.gridy=0;
        c.gridwidth=2;
        c.anchor=GridBagConstraints.LINE_START;
        add(imageLbl, c);

        c.insets = new Insets(5, 5, 5, 5);
        c.gridy++;
        c.gridwidth=2;
        add(titleLbl, c);

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
        add(downloadBtn, c);
    }


    @Override
    public void actionPerformed(ActionEvent e) {

    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if(e.getSource()==markForBatchCheckbo) {
            if(e.getStateChange() == ItemEvent.SELECTED) {
                //ChangeBorder
                setBorder(selectedBorder);
                isSelected = true;
            } else {
                setBorder(unselectedBorder);
                isSelected = false;
            }
        }

    }


    public boolean isSelected() {
        return isSelected;
    }
}
