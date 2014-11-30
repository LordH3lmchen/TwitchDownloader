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

    //Some useful examples
    private static String[] EXAMPLE_PATTERNS = {"$(game)/$(channel)/$(title)", "$(channel)-$(title)", "$(channel)/$(title)"};


    public FilenamePatternsComboBoxModel() {
        super();
        this.filenamePatterns = new ArrayList<String>();
        this.listDataListeners = new HashSet<ListDataListener>();
    }

    public FilenamePatternsComboBoxModel(ArrayList<String> filenamePatterns) {
        this();
        this.filenamePatterns = new ArrayList<String>(filenamePatterns);
    }

    public FilenamePatternsComboBoxModel(TwitchVideo twitchVideo) {
        this();
        fillFilenamePatternsWithTwitchVideoKeys(twitchVideo);
    }

    @Override
    public void setSelectedItem(Object anItem) {
        if(filenamePatterns.contains(anItem)) {
            selectedItemIndex = filenamePatterns.indexOf(anItem);
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
        filenamePatterns.add(pattern);
    }

    @Override
    public void update(Observable o, Object arg) {
        if(o.getClass().equals(TwitchVideo.class)) { //Observed TwichVideo changed update Observers
            TwitchVideo twitchVideo = (TwitchVideo)o;
            this.filenamePatterns = new ArrayList<String>();
            fillFilenamePatternsWithTwitchVideoKeys(twitchVideo);

            for(ListDataListener listener: listDataListeners) { // inform listeners about new Patterns
                listener.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, getSize()-1));
            }
        }
    }

    /**
     * füllt die verfügbaren patterns mit den verfügbaren Variablen.
     * Ermöglicht dem User alle verfügbaren Variablen zu sehen und zu testen
     * @param twitchVideo
     */
    private void fillFilenamePatternsWithTwitchVideoKeys(TwitchVideo twitchVideo) {
        LinkedHashMap<String,String> streamInformation =  twitchVideo.getStreamInformation();
        for(String pattern: EXAMPLE_PATTERNS) addElement(pattern);
        for(String key: streamInformation.keySet()) addElement("$(" + key.toLowerCase() + ")");
    }


    @Override
    public void removeElement(Object obj) {
        filenamePatterns.remove(obj);

    }

    @Override
    public void insertElementAt(String item, int index) {
        filenamePatterns.add(index, item);
    }

    @Override
    public void removeElementAt(int index) {
        filenamePatterns.remove(index);
    }
}
