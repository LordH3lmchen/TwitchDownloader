package com.trabauer.twitchtools.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Created by Flo on 17.01.2015.
 *
 *
 */
public class MainFramePanel extends JPanel implements PropertyChangeListener {

    private MainMenuPanel mainMenuPanel;
    private CardLayout layout;
    private ToolsPanel toolPanel;


    public final String MAINMENU_PANEL = "MainMenu";
    public final String TOOL_PANEL = "currentTool";

    public MainFramePanel() {
        super();
        layout = new CardLayout(0,0);
        setLayout(layout);
        mainMenuPanel = new MainMenuPanel();
        mainMenuPanel.setName("MainMenu");

        add(mainMenuPanel, mainMenuPanel.getName());
    }

    public void addToolOpenBtn(JButton toolOpenBtn){
            mainMenuPanel.add(toolOpenBtn);
    }


//    @Override
//    public void actionPerformed(ActionEvent e) {
//        if(e.getActionCommand().equals("exitTool")) {
//            CardLayout cardLayout = (CardLayout) this.getLayout();
//            cardLayout.show(this, MAINMENU_PANEL);
//        }
//    }

    public void openTool(ToolsPanel toolPanel) {
        this.toolPanel = toolPanel;
        toolPanel.addPropertyChangeListener(this);  //required for exitFunction
        add(toolPanel, TOOL_PANEL);
        CardLayout cardLayout = (CardLayout) this.getLayout();
        cardLayout.show(this, TOOL_PANEL);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if(evt.getPropertyName().equals("exitTool")) {
            CardLayout cardLayout = (CardLayout) this.getLayout();
            cardLayout.show(this, MAINMENU_PANEL);
        }

    }

    private class MainMenuPanel extends JPanel {
        private JLabel menuTitle;

        public MainMenuPanel() {
            menuTitle = new JLabel("Main Menu");
            add(menuTitle);
        }

    }



}
