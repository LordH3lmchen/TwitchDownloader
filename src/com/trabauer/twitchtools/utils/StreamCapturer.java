package com.trabauer.twitchtools.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Created by Flo on 15.01.2015.
 */
public class StreamCapturer extends OutputStream {

    private StringBuilder buffer;
    private String prefix;
    private Consumer consumer;
    private PrintStream old;

    public StreamCapturer(String prefix, Consumer consumer, PrintStream old) {
        this.prefix = prefix;
        buffer = new StringBuilder(128);
        buffer.append("[").append(prefix).append("] ");
        this.old = old;
        this.consumer = consumer;
    }

    @Override
    public void write(int b) throws IOException {
        char c = (char) b;
        String value = Character.toString(c);
        buffer.append(value);
        if (value.equals("\n")) {
            consumer.appendText(buffer.toString());
            buffer.delete(0, buffer.length());
            buffer.append("[").append(prefix).append("] ");
        }
        old.print(c);
    }
}
