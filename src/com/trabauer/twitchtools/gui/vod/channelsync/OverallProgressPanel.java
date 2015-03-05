package com.trabauer.twitchtools.gui.vod.channelsync;

import com.trabauer.twitchtools.worker.FFMpegConverterWorker;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Flo on 17.02.2015.
 */
public class OverallProgressPanel extends JPanel implements PropertyChangeListener {


    private final JLabel titleLbl;
    private final JProgressBar progressBar;
    private final JLabel progressLabel;
    private int max;
    private int progress;
    private String operationName;
    private String title;
    private LinkedBlockingQueue queue;
    private boolean increasingProgressEvts;


    public OverallProgressPanel(String operationName) {
        setLayout(new GridBagLayout());
        this.operationName = operationName;
        this.title = "FFMPEG";

        titleLbl = new JLabel(operationName + " " + title);


        this.max = 100;

        progressBar = new JProgressBar(0, max);

        progressLabel = new JLabel("100% (5 videos remaining)");
        increasingProgressEvts = false;


        layoutComponents();


    }
    private void layoutComponents() {


        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(0,2,0,2);

        c.gridy=0;
        c.gridx=0;
        c.gridwidth=1;
        Font original = titleLbl.getFont();
        titleLbl.setFont(original.deriveFont(Font.BOLD, 11.0F));
        Dimension d = titleLbl.getPreferredSize();
        titleLbl.setPreferredSize(new Dimension(400, d.height));
        add(titleLbl, c);


        c.gridwidth=1;

        c.gridx++;
        c.weightx=1.0;
        c.fill=GridBagConstraints.HORIZONTAL;

        add(progressBar, c);

        c.fill=GridBagConstraints.NONE;
        c.weightx=0.0;
        c.gridx++;
        add(progressLabel, c);

    }

    public void setMaximum(int maximum) {
        progressBar.setMaximum(maximum);
        this.max = maximum;
        setProgress(this.progress);
    }


    public void setValue(int value) {
        progressBar.setValue(value);
        progress = value;
        increaseProgress(0);
    }

    public void setTitle(String title) {
        this.title = title;
        titleLbl.setText(operationName + " " + title);
    }

    public void setQueue(LinkedBlockingQueue<Object> queue) {
        this.queue = queue;
        int percent = ((progress)*100)/(max);
        updateGuiComponents();
    }

    /**
     * If set to true. Progress Events increase the progress. So multiple propertyChange Events are able to increase
     * the progressBar. This is usefull if multiple tasks are used to do some work.
     * @param x
     */
    public void setIncreasingProgressEvts(boolean x) {
        this.increasingProgressEvts = x;
    }




    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if(evt.getPropertyName().equals("state")) {
            if(evt.getNewValue().equals(SwingWorker.StateValue.STARTED)) {
                if(! increasingProgressEvts) {
                    setVisible(true);
                    setProgress(0);
                }
                if(evt.getSource() instanceof FFMpegConverterWorker) {
                    String filename = ((FFMpegConverterWorker) evt.getSource()).getDestinationVideoFile().getName();
                    setTitle(filename);
                }
            } else if(evt.getNewValue().equals(SwingWorker.StateValue.DONE) ) {
                if(! increasingProgressEvts) {
                    if (queue == null) {
                        setVisible(false);
                    } else if (queue.size() == 0) {
                        setVisible(false);
                    }
                }
            }
        } else if(evt.getPropertyName().equals("progress")) {
            if(increasingProgressEvts) {
                int newValue = (Integer)evt.getNewValue();
                int oldValue = (Integer)evt.getOldValue();
                int increase = newValue - oldValue;
                increaseProgress(increase);
            } else {
                setProgress((Integer)evt.getNewValue());
            }
        }
    }

    public void setProgress(Integer newValue) {
        progress = newValue;
        updateGuiComponents();
    }

    private void increaseProgress(int increase) {
        progress += increase;
        updateGuiComponents();
    }

    private void updateGuiComponents() {
        progressBar.setValue(progress);
        int percent = (progress*100)/max;
        String progressText = "";
        if(queue==null) progressText = String.format("%3d %%", percent);
        else progressText = String.format("%3d %% (%d videos remaining)", percent, queue.size());
        progressLabel.setText(progressText);
    }



}
