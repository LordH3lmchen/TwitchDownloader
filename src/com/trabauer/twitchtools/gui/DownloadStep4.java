package com.trabauer.twitchtools.gui;

import com.trabauer.twitchtools.tasks.HttpFileDownloadWorker;
import com.trabauer.twitchtools.utils.Consumer;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Created by Flo on 15.01.2015.
 */
public class DownloadStep4 implements myGuiForm, Consumer, PropertyChangeListener {
    private JButton finishButton;
    private JButton concatButton;
    private JPanel mainPanel;
    private JTextArea ffmpegOutTextArea;




    public DownloadStep4() {
        super();
        finishButton.setActionCommand("finishButton");
        concatButton.setActionCommand("concatButton");
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public JButton getConcatButton() {
        return concatButton;
    }

    public void addActionListener(ActionListener listener) {
        finishButton.addActionListener(listener);
        concatButton.addActionListener(listener);
    }


    @Override
    public void appendText(final String text) {
        if (SwingUtilities.isEventDispatchThread()) {
            ffmpegOutTextArea.append(text);
            ffmpegOutTextArea.setCaretPosition(ffmpegOutTextArea.getText().length());
        } else {

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    appendText(text);
                }
            });

        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if(evt.getPropertyName().equals("outputline")) {
            ffmpegOutTextArea.append(evt.getNewValue().toString());
        } else if(evt.getPropertyName().equals("progress") && evt.getSource().getClass().equals(HttpFileDownloadWorker.class)) {
            HttpFileDownloadWorker httpFileDownloadWorker = (HttpFileDownloadWorker) evt.getSource();
            int beginningPos = ffmpegOutTextArea.getCaretPosition();
            int endPos = ffmpegOutTextArea.getText().length()-1;
            String line = "Downloading " +
                    httpFileDownloadWorker.getDestinationFile().getAbsolutePath() + " from " +
                    httpFileDownloadWorker.getSourceUrl().getPath() + evt.getNewValue().toString() + "% done";

            //ffmpegOutTextArea.replaceRange(line, beginningPos, endPos);
            //ffmpegOutTextArea.setCaretPosition(beginningPos);
        } else if(evt.getPropertyName().equals("state") && evt.getSource().getClass().equals(HttpFileDownloadWorker.class)) {
            //System.out.println(evt.getPropertyName() + " changed to " + evt.getNewValue().toString() );
            if(evt.getNewValue().toString().equals("DONE")) {
                System.out.println("Download finished");
                concatButton.setActionCommand("concatButton");
                concatButton.setText("Convert to MP4 Video");
                concatButton.setEnabled(true);
                finishButton.setEnabled(true);
            } else if(evt.getNewValue().toString().equals("STARTED")) {
                System.out.println("Downloading FFMPEG executable");
                concatButton.setEnabled(false);
                finishButton.setEnabled(false);
            }

        }
    }
}
