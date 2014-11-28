package com.trabauer.twitchtools.gui;

import com.trabauer.twitchtools.controller.Controller;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;

/**
 * Created by Flo on 16.11.2014.
 */
public class DownloadStep1 {
    private JPanel mainPanel;
    private JTextField twitchUrlTextField;
    private JButton nextButton;


    public DownloadStep1() {
        super();

        //NextActionListener nextActionListener = new NextActionListener();
        nextButton.setActionCommand("nextButton1");
        //nextButton.addActionListener(nextActionListener);
        //twitchUrlTextField.addActionListener(nextActionListener);
    }



    public JPanel getMainPanel() {
        return mainPanel;
    }

    public String getTwitchUrl() {
        System.out.println(twitchUrlTextField.getText());
        return twitchUrlTextField.getText();
    }

    public void addActionListener(ActionListener actionListener) {
        nextButton.addActionListener(actionListener);
        twitchUrlTextField.addActionListener(actionListener);
    }
}
