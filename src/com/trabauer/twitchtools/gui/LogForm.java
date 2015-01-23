package com.trabauer.twitchtools.gui;

import com.trabauer.twitchtools.utils.Consumer;

import javax.swing.*;

/**
 * Created by Flo on 18.01.2015.
 */
public class LogForm implements Consumer {
    private JPanel mainPanel;
    private JScrollPane scrollPane;
    private JTextArea textArea;

    private static LogForm instance;

    private LogForm() {
        super();
    }

    public static LogForm getInstance(){
        if(instance == null) {
            instance = new LogForm();
        }
        return instance;
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    @Override
    public void appendText(final String text) {
        if (SwingUtilities.isEventDispatchThread()) {
            textArea.append(text);
            textArea.setCaretPosition(textArea.getText().length());
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    appendText(text);
                }
            });

        }
    }

    public JTextArea getLogJTextArea() {
        return textArea;
    }
}
