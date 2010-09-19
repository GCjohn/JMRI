// NodeConfigToolPane.java

package jmri.jmrix.can.cbus.swing.nodeconfig;

import jmri.InstanceManager;
import jmri.jmrix.can.*;
import jmri.util.StringUtil;

import java.util.ResourceBundle;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.*;

import javax.swing.*;

/**
 * Pane to for setting node configuration
 *
 * @author			Bob Jacobsen   Copyright (C) 2008
 * @version			$Revision: 1.1 $
 * @since 2.3.1
 */
public class NodeConfigToolPane extends JPanel implements CanListener {

    static ResourceBundle rb = ResourceBundle.getBundle("jmri.jmrix.can.cbus.swing.nodeconfig.NodeConfigToolBundle");
    
    JTextField number;
    JButton setNN;
    JTextField varnumber;
    JTextField varvalue;
    JButton read;
    JButton write;
    
    public NodeConfigToolPane() {
        super();

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        JPanel p1;
        
        // get event number
        p1 = new JPanel();
        p1.setLayout(new BoxLayout(p1, BoxLayout.X_AXIS));
        p1.add(new JLabel(rb.getString("LabelNodeNumber")));
        number = new JTextField(5);
        p1.add(number);
        
        p1.setToolTipText(rb.getString("ToolTipNodeNumber"));
        setNN = new JButton(rb.getString("ButtonSetNodeNumber"));
        setNN.setToolTipText(rb.getString("ToolTipSetNodeNumber"));
        p1.add(setNN);
        
        p1.setBorder(BorderFactory.createTitledBorder(rb.getString("BorderNodeNumber")));
        add(p1);
        
        // set node variables
        p1 = new JPanel();
        p1.setLayout(new java.awt.GridLayout(3,2));
        
        p1.add(new JLabel(rb.getString("LabelVariableNumber")));
        varnumber = new JTextField(5);
        varnumber.setToolTipText(rb.getString("ToolTipVariableNumber"));
        p1.add(varnumber);
        
        p1.add(new JLabel(rb.getString("LabelVariableValue")));
        varvalue = new JTextField(5);
        varvalue.setToolTipText(rb.getString("ToolTipVariableValue"));
        p1.add(varvalue);

        read = new JButton(rb.getString("ButtonRead"));
        read.setToolTipText(rb.getString("ToolTipRead"));
        p1.add(read);
        write = new JButton(rb.getString("ButtonWrite"));
        write.setToolTipText(rb.getString("ToolTipWrite"));
        p1.add(write);
        
        p1.setBorder(BorderFactory.createTitledBorder(rb.getString("BorderNodeVariables")));
        add(p1);
        
        // connect
        TrafficController.instance().addCanListener(this);

    }

    public void reply(jmri.jmrix.can.CanReply m) {
    }

    public void message(jmri.jmrix.can.CanMessage m) {
    }

    public void dispose() {
        // disconnect from the CBUS
        TrafficController.instance().removeCanListener(this);
    }

    static org.apache.log4j.Category log = org.apache.log4j.Logger.getLogger(NodeConfigToolPane.class.getName());
}
