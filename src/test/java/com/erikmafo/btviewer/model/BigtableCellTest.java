package com.erikmafo.btviewer.model;

import com.google.cloud.bigtable.data.v2.models.RowCell;
import com.google.protobuf.ByteString;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.*;

public class BigtableCellTest {

    @Test
    public void shouldCreateFromRowCell() {
        //given
        var family = "myFamily";
        var qualifier = "myQualifier";
        long timestamp = 1000;
        var stringValue = "myStringValue";
        var rowCell = RowCell.create(
                family, ByteString.copyFromUtf8(qualifier), timestamp, Collections.emptyList(), ByteString.copyFromUtf8(stringValue));

        //when
        var bigtableCell = BigtableCell.from(rowCell);

        //then
        assertEquals(family, bigtableCell.getFamily());
        assertEquals(qualifier, bigtableCell.getQualifier());
        assertEquals(timestamp, bigtableCell.getTimestamp());
        assertEquals(stringValue, bigtableCell.getValueAsString());
    }
}