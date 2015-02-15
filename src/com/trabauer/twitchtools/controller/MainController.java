package com.trabauer.twitchtools.controller;

import com.trabauer.twitchtools.gui.*;
import com.trabauer.twitchtools.gui.vod.download.DownloadMainPanelDelete;
import com.trabauer.twitchtools.utils.StreamCapturer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintStream;

/**
 * Created by Flo on 16.01.2015.
 */
public class MainController implements ActionListener {

    private MainFrame mainFrame;
    private MainFramePanel mainFramePanel;

    private final String OPEN_VOD_DOWNLOAD_TOOL = "openVodDownload";
    private final String OPEN_CHANNEL_VOD_SYNC_TOOL = "openChannelVodSync";
    private final String OPEN_LOCAL_VOD_BROWSER_TOOL = "openLocalVodBrowser";

    private static MainController instance;

    private MainController() {
        super();

        mainFrame = new MainFrame();
        mainFramePanel = new MainFramePanel();
        mainFrame.setContentPane(mainFramePanel);

        JFrame logFrame = new JFrame();
        LogForm logForm = LogForm.getInstance();
        logFrame.getContentPane().add(logForm.getMainPanel());
        logFrame.setVisible(true);
        logFrame.setSize(755, 550);

        JButton openVodDownloadBtn = new JButton("Download Twitch PastBroadcast");
        openVodDownloadBtn.setActionCommand(OPEN_VOD_DOWNLOAD_TOOL);
        openVodDownloadBtn.addActionListener(this);
        mainFramePanel.addToolOpenBtn(openVodDownloadBtn);

        JButton openChannelVodSyncBtn = new JButton("Synchronise channel's past broadcasts");
        openChannelVodSyncBtn.setActionCommand(OPEN_CHANNEL_VOD_SYNC_TOOL);
        openChannelVodSyncBtn.addActionListener(this);
        mainFramePanel.addToolOpenBtn(openChannelVodSyncBtn);

        JButton openLocalVodBrowserBtn = new JButton("Downloaded VODs");
        openLocalVodBrowserBtn.setActionCommand(OPEN_LOCAL_VOD_BROWSER_TOOL);
        openLocalVodBrowserBtn.addActionListener(this);
        mainFramePanel.addToolOpenBtn(openLocalVodBrowserBtn);


        PrintStream ps = System.out;
        System.setOut(new PrintStream(new StreamCapturer("STDOUT", logForm, ps))); //Redirects STDOUT to TextArea and Console(old STDOUT)

    }

    public static MainController getInstance() {
        if(instance == null) {
            instance = new MainController();
        }
        return instance;
    }




    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals(OPEN_VOD_DOWNLOAD_TOOL)) {
            DownloadMainPanelDelete downloadMainPanel = new DownloadMainPanelDelete();
            DownloadController downloadController = null;
            try {
                downloadController = new DownloadController(downloadMainPanel);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            downloadMainPanel.addPropertyChangeListener(downloadController);
            mainFramePanel.openTool(downloadMainPanel);
        } else if(e.getActionCommand().equals(OPEN_CHANNEL_VOD_SYNC_TOOL)) {
            mainFramePanel.openTool(new TestTool(OPEN_CHANNEL_VOD_SYNC_TOOL));
        } else if(e.getActionCommand().equals(OPEN_LOCAL_VOD_BROWSER_TOOL)) {
            mainFramePanel.openTool(new TestTool(OPEN_LOCAL_VOD_BROWSER_TOOL));
        }
    }




    class TestTool extends ToolsPanel implements ActionListener {

        private JButton exitBtn;

        public TestTool(String toolsPanelName) {
            super(toolsPanelName);
            exitBtn = new JButton("Exit");
            exitBtn.setActionCommand("exitTool");
            exitBtn.addActionListener(this);



            add(new JLabel(this.getName()));
            add(new JLabel("Sorry this is just a dummy"));

            add(exitBtn);

        }


        @Override
        public void actionPerformed(ActionEvent e) {
            if(e.getSource() == exitBtn) {
                firePropertyChange(e.getActionCommand(), null, null);
            }
        }
    }

}
