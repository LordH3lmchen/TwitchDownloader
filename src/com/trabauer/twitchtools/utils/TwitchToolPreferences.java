package com.trabauer.twitchtools.utils;

import java.util.prefs.Preferences;

/**
 * Created by Flo on 18.01.2015.
 */
public class TwitchToolPreferences {

    private static Preferences prefs;

    public static final String
            DESTINATION_DIR_PREFKEY = "destinationDir",
            FILENAME_PATTERN_PREFKEY = "filenamePattern";


    private TwitchToolPreferences() {
    }

    public static Preferences getInstance() {
        if(prefs == null) {
            prefs = Preferences.userRoot().node("/com/trabauer/twitchtools");
        }
        return prefs;
    }
}
