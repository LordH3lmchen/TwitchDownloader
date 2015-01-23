package com.trabauer.twitchtools.gui;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;

/**
 * Created by Flo on 17.01.2015.
 *
 * Every Toolspanel hast to provide an Method to exit the Tool itself. It hast to send an action with "exitTool" as ActionCommand to all of its Action Listeners.
 */
public abstract class ToolsPanel extends JPanel {
    public ToolsPanel(String toolsPanelName) {
        super();
        setName(toolsPanelName);
    }
}
