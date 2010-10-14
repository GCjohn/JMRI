// FactoryResetAction.java
package jmri.jmrit.symbolicprog;

import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * Action to create a dialog so that the user can select a factory reset
 * to execute.  The user can cancel this dialog skipping any resets
 *
 * @author    Howard G. Penny    Copyright (C) 2005
 * @version   $Revision: 1.3 $
 */
public class FactoryResetAction extends AbstractAction {

    ResetTableModel rModel;
    JFrame mParent ;

    public FactoryResetAction(String actionName, ResetTableModel rpModel, JFrame pParent) {
        super(actionName);
        rModel = rpModel;
        mParent = pParent ;
    }

    public void actionPerformed(ActionEvent e) {

        log.debug("start to display Factory Reset");
        Object[] options;
        options = new String[rModel.getRowCount()];
        for (int i=0; i<rModel.getRowCount(); i++) {
            options[i]=(rModel.getValueAt(i, 0));
        }
        String s = (String)JOptionPane.showInputDialog(
                            mParent,
                            "Factory Reset"+(options.length>1?"s":""),
                            "Caution: Factory Reset",
                            JOptionPane.WARNING_MESSAGE,
                            null,
                            options,
                            null);

        //If a string was returned, a reset has been requested.
        if ((s != null) && (s.length() > 0)) {
            int i = 0;
            while (!options[i].equals(s)) i++;
            rModel.performReset(i);
            return;
        }

    }
    static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(FactoryResetAction.class.getName());
}
