package com.trabauer.twitchtools.worker;

import javax.swing.*;
import java.io.File;
import java.util.Scanner;

/**
 * Created by Flo on 15.01.2015.
 */
public class FFMpegConverterWorker extends SwingWorker<Void, Void> {
    private File destinationVideoFile, fileListForFfmpeg, ffmpegExecutable;
    private String outputLine;
    public FFMpegConverterWorker(File destinationVideoFile, File fileListForFfmpeg, File ffmpegExecutable) {
        this.destinationVideoFile = destinationVideoFile;
        this.fileListForFfmpeg = fileListForFfmpeg;
        this.ffmpegExecutable = ffmpegExecutable;




    }

    @Override
    protected Void doInBackground() throws Exception {
        ProcessBuilder pb = new ProcessBuilder(ffmpegExecutable.getAbsolutePath(),
                "-f", "concat",
                "-i", fileListForFfmpeg.getAbsolutePath(),
                "-c:v", "libx264",
                "-c:a", "copy",
                "-bsf:a", "aac_adtstoasc",
                destinationVideoFile.getAbsolutePath()
        );
        pb.directory(new File(destinationVideoFile.getParent()));
        Process p = pb.start();
        Scanner pSc = new Scanner(p.getErrorStream());
        while (pSc.hasNextLine()) {
            String prefix = "[FFMPEG]";
            String line = pSc.nextLine();
            line = prefix + line;
            line += "\n";
            firePropertyChange("outputline", this.outputLine, line);
            this.outputLine = line;
        }


        Scanner fileListSc = new Scanner(fileListForFfmpeg);
        while(fileListSc.hasNextLine()) {
            String line = fileListSc.nextLine();
            line = line.replace("file '", "").replace("'", "");
            File partFile = new File(fileListForFfmpeg.getParent() + "/" + line);
            System.out.println("deleting " + partFile.getPath());
            partFile.delete();
        }
        fileListSc.close();
        System.out.println("deleting " + fileListForFfmpeg.getName());
        fileListForFfmpeg.delete();
        File playlist = new File(fileListForFfmpeg.getParent() + "/" +  destinationVideoFile.getName().replaceFirst(".mp4$", ".m3u"));
        System.out.println("deleting" + playlist.getName());
        playlist.delete();

        return null;
    }
}
