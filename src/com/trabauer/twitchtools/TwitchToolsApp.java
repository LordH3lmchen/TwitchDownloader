package com.trabauer.twitchtools;

import com.trabauer.twitchtools.controller.Controller;
import com.trabauer.twitchtools.gui.MainFormEvent;
import com.trabauer.twitchtools.gui.MainFormListener;
import com.trabauer.twitchtools.gui.MainFrame;

import javax.swing.*;

/**
 * Created by Flo on 12.11.2014.
 */
public class TwitchToolsApp{
    public static void main (String[] args) {


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
