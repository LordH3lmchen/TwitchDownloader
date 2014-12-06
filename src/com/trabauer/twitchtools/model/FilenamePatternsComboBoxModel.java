package com.trabauer.twitchtools.model;

import com.trabauer.twitchtools.model.twitch.TwitchVideo;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.util.*;

/**
 * Created by Flo on 19.11.2014.
 */
public class FilenamePatternsComboBoxModel implements MutableComboBoxModel<String>, Observer {

    private HashSet<ListDataListener> listDataListeners;
    private ArrayList<String> filenamePatterns;
    private int selectedItemIndex = 0;



    public FilenamePatternsComboBoxModel() {
        super();
        this.filenamePatterns = new ArrayList<String>();
        this.listDataListeners = new HashSet<ListDataListener>();
    }

    public FilenamePatternsComboBoxModel(ArrayList<String> filenamePatterns) {
        this();
        this.filenamePatterns = new ArrayList<String>(filenamePatterns);
    }

    public FilenamePatternsComboBoxModel(String[] keys) {
        this();
    }

    @Override
    public void setSelectedItem(Object anItem) {
        if(filenamePatterns.contains(anItem)) {
            selectedItemIndex = filenamePatterns.indexOf(anItem);
            notifyListDataListeners();
        }
    }

    @Override
    public String getSelectedItem() {
        return filenamePatterns.get(selectedItemIndex);
    }

    @Override
    public int getSize() {
        return filenamePatterns.size();
    }

    @Override
    public String getElementAt(int index) {
        return filenamePatterns.get(index);
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
    public void addElement(String pattern) {
        if(pattern != null) {
            filenamePatterns.add(pattern);
            notifyListDataListeners();
        }
    }

    public void addElement(String pattern, int index) {
        if(pattern != null) {
            filenamePatterns.add(index, pattern);
            notifyListDataListeners();
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        //STUB
        if(o.getClass().equals(TwitchVideo.class)) { //Observed TwichVideo changed update Observers
            //TwitchVideo twitchVideo = (TwitchVideo)o;
            //this.filenamePatterns = new ArrayList<String>();
        }
    }



    @Override
    public void removeElement(Object obj) {
        filenamePatterns.remove(obj);
        notifyListDataListeners();
    }

    @Override
    public void insertElementAt(String item, int index) {
        filenamePatterns.add(index, item);
        notifyListDataListeners();
    }

    @Override
    public void removeElementAt(int index) {
        filenamePatterns.remove(index);
        notifyListDataListeners();
    }

    public boolean containsElement(String item) {
        return filenamePatterns.contains(item);
    }

    private void notifyListDataListeners() {
        for(ListDataListener listener: listDataListeners) {
            listener.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, getSize()-1));
        }
    }
}
