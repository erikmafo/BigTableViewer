package com.erikmafo.btviewer.ui.queryresult.cell;

import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import static org.junit.Assert.*;

public class JsonTreeViewFactoryTest extends ApplicationTest {

    @Test(expected = IllegalArgumentException.class)
    public void createTreeView_withNull_throwsIllegalArgumentException() {
        JsonTreeViewFactory.createTreeView(null);
    }

    @Test
    public void createTreeView_withValidJson_returnsTreeView() {
        var treeView = JsonTreeViewFactory.createTreeView(TestDataConstants.JSON_TEST_DATA);
        assertNotNull(treeView);
    }
}