package com.trabauer.twitchtools.utils;

/**
 * Created by Flo on 13.11.2014.
 */
public class UnsupportedOsException extends Exception {

    public UnsupportedOsException() {
        super(System.getProperty("os.name") + " isn't supported yet!");
    }

    public UnsupportedOsException(String message) {
        super(message);
    }
}
