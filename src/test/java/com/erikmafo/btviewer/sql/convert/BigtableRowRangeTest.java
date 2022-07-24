package com.erikmafo.btviewer.sql.convert;

import com.google.cloud.bigtable.data.v2.models.Query;
import com.google.cloud.bigtable.data.v2.models.Range;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(MockitoJUnitRunner.class)
public class BigtableRowRangeTest {

    @Mock
    private Query query;

    @Test
    public void applyToQuery_withSingleRowKey_setQueryRowKey() {
        // given
        var singleRowKey = "row-1";
        var rowRange = new BigtableRowRange(singleRowKey, null);

        // when
        rowRange.applyToQuery(query);

        // then
        verify(query).rowKey(singleRowKey);
        verifyNoMoreInteractions(query);
    }

    @Test
    public void applyToQuery_withRowRange_setQueryRange() {
        // given
        var byteRange = Range.ByteStringRange.create("row1", "row-2");
        var rowRange = new BigtableRowRange(null, byteRange);

        // when
        rowRange.applyToQuery(query);

        // then
        verify(query).range(byteRange);
        verifyNoMoreInteractions(query);
    }
}