package com.trabauer.twitchtools.gui.vod.channelsync;

import com.trabauer.twitchtools.controller.channelsync.ChannelSyncController;
import com.trabauer.twitchtools.controller.channelsync.ChannelSyncControllerInterface;
import com.trabauer.twitchtools.model.twitch.TwitchVideoInfo;
import com.trabauer.twitchtools.model.twitch.TwitchVideoInfoList;
import com.trabauer.twitchtools.utils.WrapLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

/**
 * Created by Flo on 16.01.2015.
 */
public class SyncChannelMainPanel extends JPanel implements PropertyChangeListener, ActionListener {


    // Controller
    private final ChannelSyncControllerInterface controller;

    // Models
    private final TwitchVideoInfoList twitchVideoInfoList;

    // localStuff
    private ArrayList<JPanel> searchResultItemPanels; //List to manage ResultPanelItems

    // GUI Components
    private final JLabel channelInputLabel;
    private final JPanel channelInputPanel;
    private final JTextField channelInputFld;
    private final JButton channelInputBtn;
    private final JScrollPane searchResultScrollPane;
    private final JPanel searchResultPanel;
    private final JButton loadMoreBtn;
    private final JPanel bottomPanel;
    private final JButton downloadAllBtn;
    private final JButton selectMostRecentBtn;
    private final JSpinner recentDaysSpinner;
    private final JLabel daysLabel;
    private OverallProgressPanel downloadProgressPanel;
    private OverallProgressPanel convertProgressPanel;



    public static void main(String[] args) {
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
//              DownloadController downloadController = new DownloadController();
//                MainController mainController = MainController.getInstance();

                final JFrame mainFrame = new JFrame("SyncChannelTest");
                TwitchVideoInfoList twitchVideoInfoList = new TwitchVideoInfoList(); //model
                ChannelSyncController controller = new ChannelSyncController(); //Controller

                mainFrame.setContentPane(controller.getMainPanel()); //get the view from controller

                mainFrame.pack();
                mainFrame.setSize(1080, 700);
//                mainFrame.addComponentListener(new ComponentAdapter() {
//                    @Override
//                    public void componentResized(ComponentEvent e) {
//                        System.out.println("MainFrameSiz=" + mainFrame.getSize());
//
//                    }
//                });
                mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                mainFrame.setVisible(true);
            }
        });


    }


    public SyncChannelMainPanel(ChannelSyncController controller, TwitchVideoInfoList twitchVideoInfoList) {
        setName("SyncChannel");
        this.controller = controller;
        this.twitchVideoInfoList = twitchVideoInfoList;
        twitchVideoInfoList.addPropertyChangeListener(this);
        this.searchResultItemPanels = new ArrayList<JPanel>();
        BorderLayout layout = new BorderLayout();
        setLayout(layout);

        channelInputPanel = new JPanel();
        channelInputLabel = new JLabel("Channel: ");
        channelInputFld = new JTextField();
        channelInputBtn = new JButton("Get VOD's");
        channelInputBtn.addActionListener(this);
        channelInputFld.addActionListener(this);
        bottomPanel = new JPanel();
        downloadAllBtn = new JButton("Download All");
        downloadAllBtn.addActionListener(this);
        loadMoreBtn = new JButton("load more ... ");
        loadMoreBtn.addActionListener(this);
        selectMostRecentBtn = new JButton("Select most Recent");
//        selectMostRecentBtn.setEnabled(false);
        selectMostRecentBtn.addActionListener(this);

        recentDaysSpinner = new JSpinner(new SpinnerNumberModel(30, 1, 365, 1));
        daysLabel = new JLabel("day's");




        searchResultPanel = new JPanel(new WrapLayout(FlowLayout.LEFT, 10, 10));

        searchResultScrollPane = new JScrollPane(searchResultPanel);
        searchResultScrollPane.getVerticalScrollBar().setUnitIncrement(30);

        downloadProgressPanel = new OverallProgressPanel("Downloading");
        downloadProgressPanel.setIncreasingProgressEvts(true);
        convertProgressPanel = new OverallProgressPanel("Converting");
        convertProgressPanel.setIncreasingProgressEvts(false);

        layoutComponents();

        downloadProgressPanel.setVisible(false);
        convertProgressPanel.setVisible(false);

    }



    private void layoutComponents() {

        channelInputPanel.setLayout(new GridBagLayout());
        add(channelInputPanel, BorderLayout.PAGE_START);


        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5,5,5,5);
        c.gridx = 0;
        c.gridy = 0;
        channelInputPanel.add(channelInputLabel, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        c.gridx = 1;
        channelInputPanel.add(channelInputFld, c);

        c.weightx = 0.0;
        c.gridx = 2;
        channelInputPanel.add(channelInputBtn, c);


        searchResultScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        searchResultScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        add(searchResultScrollPane, BorderLayout.CENTER);

        bottomPanel.setLayout(new GridBagLayout());
        add(bottomPanel, BorderLayout.PAGE_END);


        c.gridy=0;
        c.gridx=0;
        c.gridwidth=4;
        c.fill=GridBagConstraints.HORIZONTAL;
        c.weightx=1.0;
        bottomPanel.add(downloadProgressPanel, c);

        c.gridy++;
        bottomPanel.add(convertProgressPanel, c);




        c.gridx=0;
        c.gridy++;
        c.weightx=0.0;
        c.weighty=0.0;
        c.gridwidth=1;
        c.anchor=GridBagConstraints.LINE_START;
        bottomPanel.add(selectMostRecentBtn, c);

        c.gridx=1;
        bottomPanel.add(recentDaysSpinner, c);

        c.anchor=GridBagConstraints.LINE_END;
        c.gridx=3;
        bottomPanel.add(downloadAllBtn, c);

        c.gridx=2;
        c.weightx=1.0;
        c.fill=GridBagConstraints.HORIZONTAL;
        bottomPanel.add(daysLabel, c);



//        searchResultPanel.setBackground(Color.yellow);
//        add(searchResultPanel, BorderLayout.CENTER);


    }


    public void addVodItemComponent(Component comp) {
        add(comp);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if(evt.getPropertyName().equals("contentUpdate"))
            updateResultContentPanel();
        else if(evt.getPropertyName().equals("twitchVideoInfoAdded"))
            addResultPanel((TwitchVideoInfo)evt.getNewValue());

    }

    private void updateResultContentPanel() {
        //removeOldResults
        if(!searchResultItemPanels.isEmpty()) {
            for(JPanel searchResultItemPanel: searchResultItemPanels) {
                searchResultPanel.remove(searchResultItemPanel);
            }
            this.searchResultItemPanels = new ArrayList<JPanel>();
        }
        for(TwitchVideoInfo twitchVideoInfo: twitchVideoInfoList.getTwitchVideoInfos()) {
            VideoInfoPanel videoInfoPanel = null;
            try {
                videoInfoPanel = new VideoInfoPanel(twitchVideoInfo, controller);
            } catch (IOException e) {
                e.printStackTrace();
            }
            searchResultPanel.add(videoInfoPanel);
            searchResultItemPanels.add(videoInfoPanel);
            searchResultPanel.validate();
            searchResultPanel.repaint();
            validate();
            repaint();
        }
        searchResultPanel.add(loadMoreBtn);
        searchResultPanel.validate();
        searchResultPanel.repaint();
        validate();
        repaint();
    }

    private void addResultPanel(TwitchVideoInfo twitchVideoInfo) {
        VideoInfoPanel videoInfoPanel = null;
        try {
            videoInfoPanel = new VideoInfoPanel(twitchVideoInfo, controller);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Unable to load PreviewImage for " + twitchVideoInfo.getTitle(), "IO Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        searchResultPanel.add(videoInfoPanel);
        searchResultItemPanels.add(videoInfoPanel);
        searchResultPanel.add(loadMoreBtn);
        searchResultPanel.validate();
        searchResultPanel.repaint();
        validate();
        repaint();
    }


    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == channelInputBtn || e.getSource() == channelInputFld) {
            try {
                controller.searchFldText(channelInputFld.getText());
            } catch (MalformedURLException e1) {
                JOptionPane.showMessageDialog(this, "Weird Channel Input " + channelInputFld.getText() + " isn't a valid channel name", "Invalid channel namen", JOptionPane.ERROR_MESSAGE);
                e1.printStackTrace();
            } catch (IOException ioe) {
                JOptionPane.showMessageDialog(this, "Channel not found!", "Channel not found", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == loadMoreBtn) {
            controller.loadMoreSearchResults();
        } else if (e.getSource() == selectMostRecentBtn) {
            controller.selectMostRecent((Integer)recentDaysSpinner.getValue());
        } else if (e.getSource() == downloadAllBtn) {
            controller.downloadAllSelectedTwitchVideos();
        }

    }

    public OverallProgressPanel getDownloadProgressPanel() {
        return downloadProgressPanel;
    }

    public OverallProgressPanel getConvertProgressPanel() {
        return convertProgressPanel;
    }

    public void downloadAllBtnSetEnabled(boolean x) {
        downloadAllBtn.setEnabled(x);
    }
}
