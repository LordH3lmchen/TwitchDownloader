package com.trabauer.twitchtools.utils;

import java.util.Vector;
import java.util.prefs.Preferences;

/**
 * Created by Flo on 18.01.2015.
 */
public class TwitchToolPreferences {

    private static Preferences prefs;
    private static Vector<String> qualities;


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


    public static void setQualityOrder(Vector<String> qualities) {
        String key = "QualityPriority";
        String value = "";
        for(String quality: qualities) {
            value = value + quality + ";";
        }

        prefs.put(key, value);
    }

    public static Vector<String> getQualityOrder() {
        String value = getInstance().get("QualityPriority", "source;high;medium;low;mobile");
        String values[] = value.split(";");
        Vector<String> qualities = new Vector<String>();
        for(String quality: values) {
            qualities.add(quality);
        }
        return qualities;
    }
}
