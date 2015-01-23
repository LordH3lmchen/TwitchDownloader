package com.trabauer.twitchtools.gui.vod.download;

import com.trabauer.twitchtools.gui.MyGuiFormInterface;
import tests.TestListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Flo on 16.11.2014.
 */
public class DownloadStep1Form implements MyGuiFormInterface {
    private JPanel mainPanel;
    private JTextField twitchUrlTextField;
    private ImageIcon imgIcon;
    private JPanel logoPanel;
    private JLabel logoLabel;


    public DownloadStep1Form() {
        super();
        //nextButton.setActionCommand("nextButton1");
        twitchUrlTextField.setToolTipText("Enter the URL of the past broadcast here. " +
                "Usually looks the URL looks like: \"http://www.twitch.tv/dreamhacksc2/b/593890038\"");

    }

    public DownloadStep1Form(Image img) {
        this();
        logoLabel = new JLabel();
        setLogo(img);
        logoPanel.add(logoLabel);
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public URL getTwitchUrl() throws MalformedURLException {
        return new URL(twitchUrlTextField.getText());
    }

    public void addActionListener(ActionListener actionListener) {
        //nextButton.addPropertyChangeListener(actionListener);
        twitchUrlTextField.addActionListener(actionListener);
    }

    public void setLogo(Image img) {
        this.imgIcon = new ImageIcon(img);
        logoLabel.setIcon(imgIcon);
    }
}
