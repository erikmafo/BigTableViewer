package com.erikmafo.btviewer.ui.queryresult.cell;

import com.erikmafo.btviewer.model.BigtableValue;
import com.erikmafo.btviewer.model.ValueTypeConstants;
import javafx.scene.control.Label;
import javafx.scene.control.TreeView;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import static org.junit.Assert.*;

public class CellContentFactoryTest extends ApplicationTest {

    @Test
    public void getContent_withNull_returnsNull() {
        assertNull(CellContentFactory.getContent(null));
    }

    @Test
    public void getContent_withJson_returnsTreeView() {
        var content = CellContentFactory.getContent(new BigtableValue(TestDataConstants.JSON_TEST_DATA, ValueTypeConstants.JSON));
        assertNotNull(content);
        assertEquals(TreeView.class, content.getClass());
    }

    @Test
    public void getContent_withPrimitiveType_returnsLabel() {
        var content = CellContentFactory.getContent(new BigtableValue(TestDataConstants.JSON_TEST_DATA, ValueTypeConstants.STRING));
        assertNotNull(content);
        assertEquals(Label.class, content.getClass());
    }
}