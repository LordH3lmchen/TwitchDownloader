package com.trabauer.twitchtools.gui.vod.channelsync;

import com.trabauer.twitchtools.model.twitch.TwitchVideoInfo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by flo on 7/19/15.
 */
public class VideoInfoPreferencesDialog extends JDialog implements ActionListener {

    private TwitchVideoInfo tvi;

    private JTextField startOffsetFld;
    private JSlider startOffsetSlider;
    private JLabel startOffsetTimeLbl;

    private JTextField endOffsetFld;
    private JSlider endOffsetSlider;
    private JLabel endOffsetTimeLbl;

    JButton okBtn;

    public VideoInfoPreferencesDialog() {
        super();

        setTitle("Video Download Settings");
        GridBagLayout layout = new GridBagLayout();
        this.setLayout(layout);
        GridBagConstraints c = new GridBagConstraints();

        startOffsetFld = new JTextField();
        startOffsetFld.setColumns(10);
        startOffsetSlider = new JSlider();
        startOffsetTimeLbl = new JLabel("00:00:00");

        endOffsetFld = new JTextField();
        endOffsetFld.setColumns(10);
        endOffsetSlider = new JSlider();
        endOffsetTimeLbl = new JLabel("00:00:00");

        okBtn = new JButton("OK");
        okBtn.addActionListener(this);
        okBtn.setActionCommand("okBtn");

        // Start Offset
        c.anchor = GridBagConstraints.LINE_START;
        c.gridy = 0;
        c.gridx = 0;
        c.gridwidth = 4;
        add(new JLabel("Start Offset"), c);

        c.gridy++;
        c.gridwidth=1;
        add(new JLabel("Sec"), c);

        c.gridx++;
        add(startOffsetFld, c);

        c.gridx++;
        add(startOffsetSlider, c);

        c.gridx++;
        add(startOffsetTimeLbl, c);

        //END Offset
        c.gridy++;
        c.gridx = 0;
        c.gridwidth = 4;
        add(new JLabel("End Offset"), c);

        c.gridy++;
        c.gridwidth=1;
        add(new JLabel("Sec"), c);

        c.gridx++;
        add(endOffsetFld, c);

        c.gridx++;
        add(endOffsetSlider, c);

        c.gridx++;
        add(endOffsetTimeLbl, c);


        c.gridy++;
        c.gridx=3;
        c.anchor = GridBagConstraints.LINE_END;
        add(okBtn, c);

        pack();


    }

    public TwitchVideoInfo getTvi() {
        return tvi;
    }

    public void setTvi(TwitchVideoInfo tvi) {
        this.tvi = tvi;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals("okBtn")) {
//            tvi.setStartOffset(startOffsetSlider.getValue());
//            tvi.setEndOffset(endOffsetSlider.getValue());
            this.setVisible(false);
        }
    }
}
