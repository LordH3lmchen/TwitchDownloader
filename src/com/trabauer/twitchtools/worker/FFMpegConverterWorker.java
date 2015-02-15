package com.trabauer.twitchtools.worker;

import javax.swing.*;
import java.io.File;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * Created by Flo on 15.01.2015.
 */
public class FFMpegConverterWorker extends SwingWorker<Void, Void> {
    private File destinationVideoFile, fileListForFfmpeg, ffmpegExecutable;
    private LinkedList ffmpegOptions;
    private String outputLine;


    public FFMpegConverterWorker(File destinationVideoFile, File fileListForFfmpeg, File ffmpegExecutable) {
        this.destinationVideoFile = destinationVideoFile;
        this.fileListForFfmpeg = fileListForFfmpeg;
        this.ffmpegExecutable = ffmpegExecutable;
        this.ffmpegOptions = new LinkedList();
        ffmpegOptions.add("-c:v");
        ffmpegOptions.add("libx264");
        ffmpegOptions.add("-c:a");
        ffmpegOptions.add("copy");
        ffmpegOptions.add("-bsf:a");
        ffmpegOptions.add("aac_adtstoasc");


    }

    public FFMpegConverterWorker(File destinationVideoFile, File fileListForFfmpeg, File ffmpegExecutable, LinkedList ffmpegOptions) {
        this.destinationVideoFile = destinationVideoFile;
        this.fileListForFfmpeg = fileListForFfmpeg;
        this.ffmpegExecutable = ffmpegExecutable;
        this.ffmpegOptions = ffmpegOptions;
    }

    @Override
    protected Void doInBackground() throws Exception {
        LinkedList<String> command = new LinkedList<String>();
        if(destinationVideoFile.exists()) {
            printToPropertyChangeListeners("Destination file " + destinationVideoFile.getAbsolutePath() + " exists. It will be overwritten.\n");
            System.out.println(outputLine);
            destinationVideoFile.delete();
        }
        command.add(ffmpegExecutable.getAbsolutePath());
        command.add("-f");
        command.add("concat");
        command.add("-i");
        command.add(fileListForFfmpeg.getAbsolutePath());
        command.addAll(ffmpegOptions);
        command.add(destinationVideoFile.getAbsolutePath());


        ProcessBuilder pb = new ProcessBuilder(command);
        System.out.println("Starting to convert Video to " + destinationVideoFile.getAbsolutePath());
        System.out.println(pb.command());

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
        }


        Scanner fileListSc = new Scanner(fileListForFfmpeg);
        while(fileListSc.hasNextLine()) {
            String line = fileListSc.nextLine();
            line = line.replace("file '", "").replace("'", "");
            File partFile = new File(line);
            System.out.println("deleting " + partFile.getPath());
            partFile.delete();
        }
        fileListSc.close();



        System.out.println("deleting " + fileListForFfmpeg.getName());
        printToPropertyChangeListeners("deleting " + fileListForFfmpeg.getName() + "\n");
        fileListForFfmpeg.delete();

        return null;
    }

    protected void printToPropertyChangeListeners(String line) {
        String oldOutputline = this.outputLine;
        this.outputLine = line;
        firePropertyChange("outputline", oldOutputline, line);

    }

}
