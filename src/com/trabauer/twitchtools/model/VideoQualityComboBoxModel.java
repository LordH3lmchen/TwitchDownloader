package com.trabauer.twitchtools.model;

import com.trabauer.twitchtools.model.twitch.TwitchVideo;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Flo on 20.11.2014.
 */
public class VideoQualityComboBoxModel implements ComboBoxModel<String>, Observer {

    private ArrayList<ListDataListener> listDataListeners;
    private ArrayList<String> qualities;
    private int selectedItem = 0;



    public VideoQualityComboBoxModel(TwitchVideo twitchVideo) {
        this();
        fillQualities(twitchVideo);
    }

    private void fillQualities(TwitchVideo twitchVideo) {
        if(twitchVideo.getAvailableQualities().isEmpty()) qualities.add("None");
        for(String quality: twitchVideo.getAvailableQualities()) {
                    qualities.add(quality);
        }
        //this.selectedItem = qualities.indexOf(twitchVideo.getBestAvailableQuality());
    }

    public VideoQualityComboBoxModel() {
        this.listDataListeners = new ArrayList<ListDataListener>();
        this.qualities = new ArrayList<String>();
    }

    @Override
    public void setSelectedItem(Object anItem) {
        selectedItem = qualities.indexOf(anItem);
    }

    @Override
    public Object getSelectedItem() {
        return qualities.get(selectedItem);
    }

    @Override
    public int getSize() {
        return qualities.size();
    }

    @Override
    public String getElementAt(int index) {
        return qualities.get(index);
    }

    @Override
    public void addListDataListener(ListDataListener l) {
        listDataListeners.add(l);

    }

    @Override
    public void removeListDataListener(ListDataListener l) {
        listDataListeners.remove(l);
    }

    @Override
    public void update(Observable o, Object arg) {
        if(o.getClass().equals(TwitchVideo.class)) {
            TwitchVideo twitchVideo = (TwitchVideo)o;
            this.qualities = new ArrayList<String>();
            fillQualities(twitchVideo);

            for(ListDataListener listener: listDataListeners) {
                listener.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, getSize()-1));
            }
        }
    }
}



