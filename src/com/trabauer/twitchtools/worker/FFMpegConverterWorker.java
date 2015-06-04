package com.trabauer.twitchtools.worker;

import com.trabauer.twitchtools.model.twitch.TwitchVideoInfo;

import javax.swing.*;
import java.io.File;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Flo on 15.01.2015.
 */
public class FFMpegConverterWorker extends SwingWorker<Void, Void> {
    private File destinationVideoFile, fileListForFfmpeg;
    private String ffmpegCommand;
    private LinkedList ffmpegOptions;
    private String outputLine;
    private int videoLength;

    private TwitchVideoInfo relatedTvi;


    public FFMpegConverterWorker(File destinationVideoFile, File fileListForFfmpeg, String ffmpegCommand) {
        this.destinationVideoFile = destinationVideoFile;
        this.fileListForFfmpeg = fileListForFfmpeg;
        this.ffmpegCommand = ffmpegCommand;
        this.ffmpegOptions = new LinkedList();
        ffmpegOptions.add("-c:v");
        ffmpegOptions.add("libx264");
        ffmpegOptions.add("-c:a");
        ffmpegOptions.add("copy");
        ffmpegOptions.add("-bsf:a");
        ffmpegOptions.add("aac_adtstoasc");


    }

    public FFMpegConverterWorker(File destinationVideoFile, File fileListForFfmpeg, String ffmpegCommand, LinkedList ffmpegOptions) {
        this.destinationVideoFile = destinationVideoFile;
        this.fileListForFfmpeg = fileListForFfmpeg;
        this.ffmpegCommand = ffmpegCommand;
        this.ffmpegOptions = ffmpegOptions;
    }

    public void setVideoLength(int videoLength) {
        int oldVideoLength = this.videoLength;
        this.videoLength = videoLength;
        firePropertyChange("videoLength", oldVideoLength, this.videoLength);
    }

    public TwitchVideoInfo getRelatedTwitchVideoInfo() {
        return relatedTvi;
    }

    public void setRelatedTwitchVideoInfo(TwitchVideoInfo relatedTvi) {
        this.relatedTvi = relatedTvi;
    }

    public File getDestinationVideoFile() {
        return destinationVideoFile;
    }

    @Override
    protected Void doInBackground() throws Exception {
        LinkedList<String> command = new LinkedList<String>();
        if(destinationVideoFile.exists()) {
            printToPropertyChangeListeners("Destination file " + destinationVideoFile.getAbsolutePath() + " exists. It will be overwritten.\n");
            System.out.println(outputLine);
            destinationVideoFile.delete();
        }
        command.add(ffmpegCommand);
        command.add("-f");
        command.add("concat");
        command.add("-i");
        command.add(fileListForFfmpeg.getAbsolutePath());
        command.addAll(ffmpegOptions);
        command.add(destinationVideoFile.getAbsolutePath());


        ProcessBuilder pb = new ProcessBuilder(command);
//        System.out.println("Starting to convert Video to " + destinationVideoFile.getAbsolutePath());
//        System.out.println(pb.command());
        firePropertyChange("videoLength", 0, videoLength);
        relatedTvi.setState(TwitchVideoInfo.State.CONVERTING);

        pb.directory(new File(destinationVideoFile.getParent()));
        Process p = pb.start();
        Scanner pSc = new Scanner(p.getErrorStream());
        while (pSc.hasNextLine()) {
            String prefix = "[FFMPEG]";
            String line = pSc.nextLine();
            line = prefix + line;
            line += "\n";
            printToPropertyChangeListeners(line);
            this.outputLine = line;
            Matcher matcher = Pattern.compile("time=\\d{2,}:\\d{2}:\\d{2}").matcher( line );
            if(matcher.find()) {
                //System.out.println(line);
                String timeStr = matcher.group().replace("time=", "");
                String timeStrParts[] = timeStr.split(":");
                try {
                    int progress = Integer.parseInt(timeStrParts[0]) * 3600;
                    progress += Integer.parseInt(timeStrParts[1]) * 60;
                    progress += Integer.parseInt(timeStrParts[2]);
                    if(videoLength>0) {
                        int percent = (progress*100)/videoLength;
                        setProgress(Math.min(100, percent));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    printToPropertyChangeListeners(e.getMessage());
                }
            }

        }


        Scanner fileListSc = new Scanner(fileListForFfmpeg);
        while(fileListSc.hasNextLine()) {
            String line = fileListSc.nextLine();
            line = line.replace("file '", "").replace("'", "");
            File partFile = new File(line);
            System.out.println("deleting " + partFile.getPath());
            printToPropertyChangeListeners("deleting " + partFile.getPath() + "\n");
            partFile.delete();
        }
        fileListSc.close();



        System.out.println("deleting " + fileListForFfmpeg.getName());
        printToPropertyChangeListeners("deleting " + fileListForFfmpeg.getName() + "\n");
        fileListForFfmpeg.delete();

        if(relatedTvi!=null) {
            relatedTvi.setState(TwitchVideoInfo.State.CONVERTED);
            relatedTvi.setMainRelatedFileOnDisk(destinationVideoFile);
        }

        return null;
    }

    protected void printToPropertyChangeListeners(String line) {
        String oldOutputline = this.outputLine;
        this.outputLine = line;
        firePropertyChange("outputline", oldOutputline, line);
    }

}
