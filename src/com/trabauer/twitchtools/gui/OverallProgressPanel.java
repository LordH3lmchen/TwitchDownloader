package com.trabauer.twitchtools.gui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

/**
 * Created by Flo on 28.11.2014.
 */
public class OverallProgressPanel extends JPanel implements PropertyChangeListener {


    private JProgressBar overallProgressBar;
    private int overallProgress, partCountProgress, partCount;
    private JLabel progressLabel;
    private ArrayList<ActionListener> actionListeners;
    private String state = "PENDING";

    public OverallProgressPanel(int partCount) {
        super();
        actionListeners = new ArrayList<ActionListener>();
        overallProgress = 0;
        partCountProgress = 0;
        this.partCount = partCount;
        overallProgressBar = new JProgressBar(0, 100 * partCount);
        progressLabel = new JLabel("".format(" %3d%% (%d/%d)", 0, 0, partCount));
        progressLabel.setToolTipText("( parts done / number of parts )");
        add(new JLabel("Overall Progress "));
        add(overallProgressBar);
        add(progressLabel);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if(evt.getPropertyName().equals("progress")) {
            int oldValue = (Integer)evt.getOldValue();
            int newValue = (Integer)evt.getNewValue();

            if((newValue - oldValue)>0) {
                overallProgress += (newValue - oldValue);
                overallProgressBar.setValue(overallProgress);
                updateProgressLabel();
            }
        } else if(evt.getPropertyName().equals("videoPart")) {
            //System.out.println("OverallProgPanel: "  + evt.getPropertyName() + " changed from " + evt.getOldValue() + " to " + evt.getNewValue());
            if(evt.getOldValue()!=null)
                partCountProgress++;
            updateProgressLabel();
        } else if(evt.getPropertyName().equals("state")) {
            //System.out.println("OverallProgPanel: "  + evt.getPropertyName() + " changed from " + evt.getOldValue() + " to " + evt.getNewValue());
            if(evt.getNewValue().toString().equals("DONE")) {
                partCountProgress++;
                updateProgressLabel();
            } else if(evt.getNewValue().toString().equals("STARTED")) {
                if( ! this.state.equals("STARTED") ) {
                    this.state = "STARTED";
                    notifyState();
                }
            }
        } else {
            //System.out.println("OverallProgPanel: "  + evt.getPropertyName() + " changed from " + evt.getOldValue() + " to " + evt.getNewValue());
        }

    }

    private void updateProgressLabel() {
        int percent = 0;
        if(partCount!=0) {
            percent = (overallProgress / partCount) ;
        }
        if(partCount!=partCountProgress) {
            progressLabel.setText("".format(" %3d %% (%d/%d)", percent, partCountProgress, partCount));

        }
        else {
            progressLabel.setText("".format(" %3d %% (%d/%d)", 100, partCountProgress, partCount));
            this.state="DONE";
            notifyState();
        }
    }

    public void addActionListener(ActionListener actionListener) {
        this.actionListeners.add(actionListener);
    }

    public void notifyActionListeners(ActionEvent actionEvent) {
        for(ActionListener actionListener: actionListeners) {
            actionListener.actionPerformed(actionEvent);
        }
    }

    public void notifyState() {
        notifyActionListeners(new ActionEvent(this, 0, this.state));
    }

    public void reset() {
        this.state = "PENDING";
        notifyState();

    }
}
