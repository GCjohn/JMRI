package jmri.jmrix.grapevine.serialdriver.configurexml;

import jmri.util.JUnitUtil;
import org.junit.*;
import jmri.jmrix.grapevine.serialdriver.ConnectionConfig;

/**
 * Tests for the ConnectionConfigXml class.
 *
 * @author Paul Bender Copyright (C) 2016
 */
public class ConnectionConfigXmlTest extends jmri.jmrix.configurexml.AbstractSerialConnectionConfigXmlTestBase {

    @Before
    @Override
    public void setUp() {
        JUnitUtil.setUp();
        xmlAdapter = new ConnectionConfigXml();
        cc = new ConnectionConfig();
    }

    @After
    @Override
    public void tearDown() {
        JUnitUtil.clearShutDownManager(); // put in place because AbstractMRTrafficController implementing subclass was not terminated properly
        JUnitUtil.tearDown();

        xmlAdapter = null;
        cc = null;
    }
}
