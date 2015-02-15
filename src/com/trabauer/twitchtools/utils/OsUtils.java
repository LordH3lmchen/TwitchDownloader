package com.trabauer.twitchtools.utils;

import javax.swing.*;
import java.io.File;

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

    public static String getValidFilename(String filename) {
        String validFilename =  filename.replaceAll("[^a-zA-Z0-9\\.\\-\\\\_ ]", "");
        return validFilename.replaceAll(" ", "_");
    }

    public static String getFileExtension(File file) {
        String fileExtension = "";
        String filename = file.getName();
        int i = filename.lastIndexOf('.');
        if(i>0) fileExtension = filename.substring(i);
        fileExtension = fileExtension.replaceAll("\\?.*$", "");
        return fileExtension;
    }
}
