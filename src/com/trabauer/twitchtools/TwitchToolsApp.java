package com.trabauer.twitchtools;

import com.trabauer.twitchtools.controller.Controller;
import com.trabauer.twitchtools.utils.OsValidator;

import javax.swing.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;

/**
 * Created by Flo on 12.11.2014.
 */
public class TwitchToolsApp{


    public static void main (String[] args) throws URISyntaxException, MalformedURLException {

        // URL onlineRessourcesURL = new URL("http://trabauer.com/downloads/project_ressources/TwitchTools/");



        // Set look & feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Controller controller = new Controller();
            }
        });


    }




}
