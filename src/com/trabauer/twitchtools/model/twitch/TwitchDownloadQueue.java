package com.trabauer.twitchtools.model.twitch;

import com.trabauer.twitchtools.model.VideoPart;

import java.util.ArrayList;
import java.util.Observable;

/**
 * Created by Flo on 25.11.2014.
 */
public class TwitchDownloadQueue extends Observable {
    private ArrayList<TwitchVideoPart> videoParts;
    private int initialSize;



    public TwitchDownloadQueue(ArrayList<TwitchVideoPart> videoParts) {
        this.videoParts = new ArrayList<TwitchVideoPart>(videoParts);
        this.initialSize = videoParts.size();
    }

    public TwitchVideoPart popNextVideoPart() {
        if(! videoParts.isEmpty()) {
            TwitchVideoPart videoPart = videoParts.get(0);
            videoParts.remove(0);
            return videoPart;
        }
        return null;
    }

    public TwitchVideoPart peekNextVideoPart() {
        if(! videoParts.isEmpty()) {
            TwitchVideoPart videoPart = videoParts.get(0);
            return videoPart;
        }
        return null;
    }

    public void appendVideoPart(TwitchVideoPart videoPart) {
        videoParts.add(videoPart);
        initialSize++;
    }

    public int getInitialSize() {
        return initialSize;
    }

    public int size() {
        return videoParts.size();
    }


    public boolean isEmpty() {
        return videoParts.isEmpty();
    }




}
