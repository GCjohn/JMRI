package jmri.jmrit.beantable;

import apps.gui.GuiLafPreferencesManager;
import java.awt.GraphicsEnvironment;
import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import jmri.Block;
import jmri.InstanceManager;
import jmri.jmrit.display.layoutEditor.LayoutBlock;
import jmri.jmrit.display.layoutEditor.LayoutBlockManager;
import jmri.util.JUnitUtil;
import jmri.util.swing.JemmyUtil;
import org.junit.*;
import org.netbeans.jemmy.operators.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tests for the jmri.jmrit.beantable.BlockTableAction class
 *
 * @author Bob Jacobsen Copyright 2004, 2007, 2008
 */
public class BlockTableActionTest extends AbstractTableActionBase<Block> {

    @Test
    public void testCreate() {
        Assert.assertNotNull(a);
        Assert.assertNull(a.f); // frame should be null until action invoked
    }

    @Override
    public String getTableFrameName() {
        return Bundle.getMessage("TitleBlockTable");
    }

    @Override
    @Test
    public void testGetClassDescription() {
        Assert.assertEquals("Block Table Action class description", "Block Table", a.getClassDescription());
    }

    /**
     * Check the return value of includeAddButton. The table generated by this
     * action includes an Add Button.
     */
    @Override
    @Test
    public void testIncludeAddButton() {
        Assert.assertTrue("Default include add button", a.includeAddButton());
    }

    @Test
    public void testInvoke() {
        Assume.assumeFalse(GraphicsEnvironment.isHeadless());
        a.actionPerformed(null);

        JFrame f = JFrameOperator.waitJFrame(Bundle.getMessage("TitleBlockTable"), true, true);
        Assert.assertNotNull(f);
        // create a couple of blocks, and see if they show
        InstanceManager.getDefault(jmri.BlockManager.class).createNewBlock("IB1", "block 1");

        Block b2 = InstanceManager.getDefault(jmri.BlockManager.class).createNewBlock("IB2", "block 2");
        Assert.assertNotNull(b2);
        b2.setDirection(jmri.Path.EAST);

        // set graphic state column display preference to false, read by createModel()
        InstanceManager.getDefault(GuiLafPreferencesManager.class).setGraphicTableState(false);

        BlockTableAction _bTable;
        _bTable = new BlockTableAction();
        Assert.assertNotNull("found BlockTable frame", _bTable);

        // assert blocks show in table
        //Assert.assertEquals("Block1 getValue","(no name)",_bTable.getValue(null)); // taken out for now, returns null on CI?
        //Assert.assertEquals("Block1 getValue","(no Block)",_bTable.getValue("nonsenseBlock"));
        //Assert.assertEquals("Block1 getValue","IB1",_bTable.getValue("block 1"));
        // set to true, use icons
        InstanceManager.getDefault(GuiLafPreferencesManager.class).setGraphicTableState(true);
        BlockTableAction _b1Table;
        _b1Table = new BlockTableAction();
        Assert.assertNotNull("found BlockTable1 frame", _b1Table);

        _b1Table.addPressed(null);
        JFrame af = JFrameOperator.waitJFrame(Bundle.getMessage("TitleAddBlock"), true, true);
        Assert.assertNotNull("found Add frame", af);
        // Cancel & close AddPane
        _b1Table.cancelPressed(null);

        // clean up
        (new JFrameOperator(af)).requestClose();
        JUnitUtil.dispose(af);
        _bTable.dispose();
        _b1Table.dispose();
        (new JFrameOperator(f)).requestClose();
        JUnitUtil.dispose(f);
    }

    @Test
    public void testAddBlock() {
        Assume.assumeFalse(GraphicsEnvironment.isHeadless());
        a.actionPerformed(null); // show table
        JFrame f = JFrameOperator.waitJFrame(Bundle.getMessage("TitleBlockTable"), true, true);
        Assert.assertNotNull(f);

        a.addPressed(null);
        JFrameOperator addFrame = new JFrameOperator(Bundle.getMessage("TitleAddBlock"));  // NOI18N
        Assert.assertNotNull("Found Add Block Frame", addFrame);  // NOI18N

        new JTextFieldOperator(addFrame, 0).setText("105");  // NOI18N
        new JTextFieldOperator(addFrame, 2).setText("Block 105");  // NOI18N
        new JButtonOperator(addFrame, Bundle.getMessage("ButtonCreate")).push();  // NOI18N
        new JButtonOperator(addFrame, Bundle.getMessage("ButtonCancel")).push();  // NOI18N

        Block chk105 = jmri.InstanceManager.getDefault(jmri.BlockManager.class).getBlock("Block 105");  // NOI18N
        Assert.assertNotNull("Verify IB105 Added", chk105);  // NOI18N
        Assert.assertEquals("Verify system name prefix", "IB105", chk105.getSystemName());  // NOI18N

        (new JFrameOperator(f)).requestClose();
        JUnitUtil.dispose(f);
    }

    @Test
    public void testRenameBlock() {
        Assume.assumeFalse(GraphicsEnvironment.isHeadless());

        // Create a Layout Block which will create the Block entry
        LayoutBlockManager lbm = jmri.InstanceManager.getDefault(LayoutBlockManager.class);
        LayoutBlock layoutBlock = lbm.createNewLayoutBlock("ILB999", "Block Name");  // NOI18N
        layoutBlock.initializeLayoutBlock();
        Assert.assertNotNull(layoutBlock);
        Assert.assertEquals("Block Name", layoutBlock.getUserName());  // NOI18N

        // Get the referenced block
        jmri.Block block = jmri.InstanceManager.getDefault(jmri.BlockManager.class).getByUserName("Block Name");  // NOI18N
        Assert.assertNotNull(block);

        // Open the block table
        a.actionPerformed(null); // show table
        JFrameOperator jfo = new JFrameOperator(Bundle.getMessage("TitleBlockTable"));  // NOI18N
        Assert.assertNotNull(jfo);

        JTableOperator tbo = new JTableOperator(jfo);
        Assert.assertNotNull(tbo);

        // Click on the edit button, set the user name to empty for remove
        tbo.clickOnCell(0, 5);
        JFrameOperator jfoEdit = new JFrameOperator(Bundle.getMessage("TitleEditBlock"));  // NOI18N
        JTextFieldOperator jtxt = new JTextFieldOperator(jfoEdit, 0);
        jtxt.clickMouse();
        jtxt.setText("");

        // Preprare the dialog thread and click on OK
        Thread remove = JemmyUtil.createModalDialogOperatorThread(Bundle.getMessage("WarningTitle"), Bundle.getMessage("ButtonOK"));  // NOI18N
        new JButtonOperator(jfoEdit, "OK").doClick();  // NOI18N
        JUnitUtil.waitFor(() -> {
            return !(remove.isAlive());
        }, "remove finished");  // NOI18N
        tbo.clickOnCell(0, 0);  // deselect the edit button

        // Click on the edit button, set the user name to a new value
        tbo.clickOnCell(0, 5);
        jfoEdit = new JFrameOperator(Bundle.getMessage("TitleEditBlock"));  // NOI18N
        jtxt = new JTextFieldOperator(jfoEdit, 0);
        jtxt.clickMouse();
        jtxt.setText("New Block Name");  // NOI18N

        // Preprare the dialog thread and click on OK
        Thread rename = JemmyUtil.createModalDialogOperatorThread(Bundle.getMessage("QuestionTitle"), Bundle.getMessage("ButtonYes"));  // NOI18N
        new JButtonOperator(jfoEdit, "OK").doClick();  // NOI18N
        JUnitUtil.waitFor(() -> {
            return !(rename.isAlive());
        }, "rename finished");  // NOI18N
        tbo.clickOnCell(0, 0);  // deselect the edit button

        // Confirm the layout block user name change
        Assert.assertEquals("New Block Name", layoutBlock.getUserName());

        jmri.util.JUnitAppender.assertWarnMessage("Cannot remove user name for block Block Name");  // NOI18N
        jfo.requestClose();
    }

    @Override
    public String getAddFrameName() {
        return Bundle.getMessage("TitleAddBlock");
    }

    @Test
    @Override
    public void testAddThroughDialog() {
        Assume.assumeFalse(GraphicsEnvironment.isHeadless());
        Assume.assumeTrue(a.includeAddButton());
        a.actionPerformed(null);
        JFrame f = JFrameOperator.waitJFrame(getTableFrameName(), true, true);

        // find the "Add... " button and press it.
        jmri.util.swing.JemmyUtil.pressButton(new JFrameOperator(f), Bundle.getMessage("ButtonAdd"));
        new org.netbeans.jemmy.QueueTool().waitEmpty();
        JFrame f1 = JFrameOperator.waitJFrame(getAddFrameName(), true, true);
        JFrameOperator jf = new JFrameOperator(f1);
        //Enter 1 in the text field labeled "System Name:"
        JLabelOperator jlo = new JLabelOperator(jf, Bundle.getMessage("LabelSystemName"));
        ((JTextField) jlo.getLabelFor()).setText("1");
        //and press create
        jmri.util.swing.JemmyUtil.pressButton(jf, Bundle.getMessage("ButtonCreate"));
        jf.requestClose();
        JUnitUtil.dispose(f1);
        (new JFrameOperator(f)).requestClose();
        JUnitUtil.dispose(f);
    }

    @Test
    public void testSetDefaultSpeed() {
        Assume.assumeFalse(GraphicsEnvironment.isHeadless());
        Assume.assumeTrue(a.includeAddButton());
        a.actionPerformed(null);
        JFrame f = JFrameOperator.waitJFrame(getTableFrameName(), true, true);

        // find the "Add... " button and press it.
        jmri.util.swing.JemmyUtil.pressButton(new JFrameOperator(f), Bundle.getMessage("ButtonAdd"));
        JFrame f1 = JFrameOperator.waitJFrame(getAddFrameName(), true, true);
        JFrameOperator jf = new JFrameOperator(f1);
        //Enter 1 in the text field labeled "System Name:"
        JLabelOperator jlo = new JLabelOperator(jf, Bundle.getMessage("LabelSystemName"));
        ((JTextField) jlo.getLabelFor()).setText("1");
        //and press create
        jmri.util.swing.JemmyUtil.pressButton(jf, Bundle.getMessage("ButtonCreate"));
        jf.requestClose();

        // Open Speed pane to test Speed menu, which displays a JOptionPane
        JFrameOperator main = new JFrameOperator(getTableFrameName());
        // Use GUI menu to open Speeds pane:
        //This is a modal JOptionPane, so create a thread to dismiss it.
        Thread t = new Thread(() -> {
            try {
                jmri.util.swing.JemmyUtil.confirmJOptionPane(main, Bundle.getMessage("BlockSpeedLabel"), "", "OK");
            }
            catch (org.netbeans.jemmy.TimeoutExpiredException tee) {
                // we're waiting for this thread to finish in the main method,
                // so any exception here means we failed.
                log.error("caught timeout exception while waiting for modal dialog", tee);
            }
        });
        t.setName("Default Speeds Dialog Close Thread");
        t.start();
        // pushMenuNoBlock is used, because dialog is modal
        JMenuBarOperator mainbar = new JMenuBarOperator(main);
        mainbar.pushMenu(Bundle.getMessage("SpeedsMenu")); // stops at top level
        JMenuOperator jmo = new JMenuOperator(mainbar, Bundle.getMessage("SpeedsMenu"));
        JPopupMenu jpm = jmo.getPopupMenu();
        JMenuItemOperator jmio = new JMenuItemOperator(new JPopupMenuOperator(jpm), Bundle.getMessage("SpeedsMenuItemDefaults"));
        jmio.pushNoBlock();

        // wait for the dismiss thread to finish
        JUnitUtil.waitFor(() -> {
            return !t.isAlive();
        }, "Dismiss Default Speeds Thread finished");

        // clean up
        JUnitUtil.dispose(f1);
        (new JFrameOperator(f)).requestClose();
        JUnitUtil.dispose(f);
    }

    @Test
    @Override
    public void testEditButton() {
        Assume.assumeFalse(GraphicsEnvironment.isHeadless());
        Assume.assumeTrue(a.includeAddButton());
        a.actionPerformed(null);
        JFrame f = JFrameOperator.waitJFrame(getTableFrameName(), true, true);
        JFrameOperator jfo = new JFrameOperator(f);

        // find the "Add... " button and press it.
        jmri.util.swing.JemmyUtil.pressButton(jfo, Bundle.getMessage("ButtonAdd"));
        JFrame f1 = JFrameOperator.waitJFrame(getAddFrameName(), true, true);
        JFrameOperator jf = new JFrameOperator(f1);
        //Enter 1 in the text field labeled "System Name:"

        JLabelOperator jlo = new JLabelOperator(jf, Bundle.getMessage("LabelSystemName"));
        ((JTextField) jlo.getLabelFor()).setText("1");
        //and press create
        jmri.util.swing.JemmyUtil.pressButton(jf, Bundle.getMessage("ButtonCreate"));
        jf.requestClose();
        new org.netbeans.jemmy.QueueTool().waitEmpty();
        JTableOperator tbl = new JTableOperator(jfo, 0);
        // find the "Edit" button and press it.  This is in the table body.
        tbl.clickOnCell(0, tbl.findColumn(Bundle.getMessage("ButtonEdit")));
        JFrame f2 = JFrameOperator.waitJFrame(getEditFrameName(), true, true);
        jmri.util.swing.JemmyUtil.pressButton(new JFrameOperator(f2), Bundle.getMessage("ButtonCancel"));
        JUnitUtil.dispose(f2);
        JUnitUtil.dispose(f1);
        (new JFrameOperator(f)).requestClose();
        JUnitUtil.dispose(f);
    }

    @Override
    public String getEditFrameName() {
        return Bundle.getMessage("TitleEditBlock") + " IB1";
    }

    @Before
    @Override
    public void setUp() {
        JUnitUtil.setUp();
        JUnitUtil.resetInstanceManager();
        jmri.util.JUnitUtil.resetProfileManager();
        JUnitUtil.initDefaultUserMessagePreferences();
        JUnitUtil.initInternalTurnoutManager();
        JUnitUtil.initInternalLightManager();
        JUnitUtil.initInternalSensorManager();
        JUnitUtil.initInternalSignalHeadManager();
        InstanceManager.setDefault(jmri.BlockManager.class, new jmri.BlockManager());
        helpTarget = "package.jmri.jmrit.beantable.BlockTable";
        a = new BlockTableAction();
    }

    @After
    @Override
    public void tearDown() {
        a = null;
        JUnitUtil.resetWindows(false, false);
        JUnitUtil.deregisterBlockManagerShutdownTask();
        JUnitUtil.tearDown();
    }

    private final static Logger log = LoggerFactory.getLogger(BlockTableActionTest.class);
}
