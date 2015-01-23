package com.trabauer.twitchtools.gui.vod.channelsync;

import com.trabauer.twitchtools.controller.channelsync.ChannelSyncController;
import com.trabauer.twitchtools.controller.channelsync.ChannelSyncControllerInterface;
import com.trabauer.twitchtools.gui.ToolsPanel;
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
import java.util.ArrayList;

/**
 * Created by Flo on 16.01.2015.
 */
public class SyncChannelMainPanel extends ToolsPanel implements PropertyChangeListener, ActionListener {



    private final JLabel channelInputLabel;
    private final JPanel channelInputPanel;
    private final JTextField channelInputFld;
    private final JButton channelInputBtn;
    private final JScrollPane searchResultScrollPane;
    private final JPanel searchResultPanel;

    private final ChannelSyncControllerInterface controller;
    private final TwitchVideoInfoList twitchVideoInfoList;
    private final JButton loadMoreBtn;

    private ArrayList<JPanel> searchResultItemPanels;

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

                JFrame mainFrame = new JFrame("SyncChannelTest");
                TwitchVideoInfoList twitchVideoInfoList = new TwitchVideoInfoList(); //model
                ChannelSyncController controller = new ChannelSyncController(twitchVideoInfoList); //Controller

                mainFrame.setContentPane(controller.getMainPanel()); //get the view from controller

                mainFrame.pack();
                mainFrame.setSize(1280, 1024);
                mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                mainFrame.setVisible(true);
            }
        });


    }


    public SyncChannelMainPanel(ChannelSyncController controller, TwitchVideoInfoList twitchVideoInfoList) {
        super("SyncChannel");
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

        loadMoreBtn = new JButton("load more ... ");
        loadMoreBtn.addActionListener(this);

        searchResultPanel = new JPanel(new WrapLayout(FlowLayout.LEFT, 10, 10));

        searchResultScrollPane = new JScrollPane(searchResultPanel);
        searchResultScrollPane.getVerticalScrollBar().setUnitIncrement(30);

        layoutComponents();

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


//        searchResultPanel.setBackground(Color.yellow);
//        add(searchResultPanel, BorderLayout.CENTER);


    }


    public void addVodItemComponent(Component comp) {
        add(comp);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if(evt.getPropertyName().equals("twitchVideoInfos"))
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
            VideoInfoPanel videoInfoPanel = null; //
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
        if( e.getSource()==channelInputBtn || e.getSource()==channelInputFld) {
            controller.searchFldText(channelInputFld.getText());
        } else if(e.getSource()==loadMoreBtn) {
            controller.loadMoreSearchResults();
        }
    }
}
