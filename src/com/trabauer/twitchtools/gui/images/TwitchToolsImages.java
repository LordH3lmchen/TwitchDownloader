package com.trabauer.twitchtools.gui.images;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;

/**
 * Created by Flo on 18.01.2015.
 */


public class TwitchToolsImages {

    private static Image twitchDownloadToolImage;

    private TwitchToolsImages() {
    }

    public static Image getTwitchDownloadToolImage() {
        if(twitchDownloadToolImage == null) {
            try {
                twitchDownloadToolImage = getImageFromResources("twitchTool.png");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return twitchDownloadToolImage;
    }

    public static Image getDeleteImage() {
        if(twitchDownloadToolImage == null) {
            try {
                twitchDownloadToolImage = getImageFromResources("delete.png");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return twitchDownloadToolImage;
    }

    public static Image getPlayImage() {
        if(twitchDownloadToolImage == null) {
            try {
                twitchDownloadToolImage = getImageFromResources("play.gif");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return twitchDownloadToolImage;
    }


    public static Image getImageFromResources(String imageName) throws IOException {
        return ImageIO.read(TwitchToolsImages.class.getResource(imageName));
    }
}
