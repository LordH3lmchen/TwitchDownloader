package tests;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Created by Flo on 20.01.2015.
 */
public class TestListener implements PropertyChangeListener, ActionListener {

    private static TestListener instance;

    private TestListener() {
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        System.err.println("PropertyChangeTester\n====================" +
                        "\n" + evt.toString() +
                        "PropertyName: " + evt.getPropertyName() +
                        "\nOldValue: " + evt.getOldValue() +
                        "\nNewValue:" + evt.getNewValue() +
                        "\nPropagationId: " + evt.getPropagationId() +
                        "\nSource: " + evt.getSource()
        );


    }

    public static TestListener getInstance() {
        if(instance == null) {
            instance = new TestListener();
        }
        return instance;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.err.println("ActionPerformedTester\n===================== " +
                        "\n" + e.toString() +
                        "\nActionCommand: " + e.getActionCommand() +
                        "\nSource: " + e.getSource() +
                        "\nParamString: " + e.paramString()
        );
    }


}
