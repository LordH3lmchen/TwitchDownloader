package com.trabauer.twitchtools.utils;

import javax.swing.*;

/**
 * Created by Flo on 04.12.2014.
 */
public class OsUtils {
    public static String getUserHome() {
        if(OsValidator.isWindows()) {
            return System.getenv().get("USERPROFILE");
        }
        else if(OsValidator.isMac()) {
            return System.getenv().get("HOME");
        }
        else if(OsValidator.isUnix()) {
            return System.getenv().get("HOME");
        }
        else {
            try {
                throw new UnsupportedOsException();
            } catch (UnsupportedOsException e1) {
                e1.printStackTrace();
            }
        }
        return null;
    }
}
