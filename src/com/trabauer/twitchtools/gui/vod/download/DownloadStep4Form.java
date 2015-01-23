package com.trabauer.twitchtools.gui.vod.download;

import com.trabauer.twitchtools.gui.MyGuiFormInterface;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Created by Flo on 15.01.2015.
 */
public class DownloadStep4Form implements MyGuiFormInterface, PropertyChangeListener {
    private JButton concatButton;
    private JPanel mainPanel;
    private JTextField ffmpegOptionsTextField;
    private JTextArea outputTextArea;


    public DownloadStep4Form() {
        super();
        concatButton.setActionCommand("concatButton");
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public JButton getConcatButton() {
        return concatButton;
    }

    public void addActionListener(ActionListener listener) {
        concatButton.addActionListener(listener);
        ffmpegOptionsTextField.addActionListener(listener);
    }


    public String getFfmpegOptions() {
        return ffmpegOptionsTextField.getText();
    }

    public void setFfmpegOptions(String text) {
        ffmpegOptionsTextField.setText(text);
    }


    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if(evt.getPropertyName().equals("outputline")) {
            if(evt.getNewValue() instanceof String) {
                outputTextArea.append((String)evt.getNewValue());
            }
        }
    }
}
