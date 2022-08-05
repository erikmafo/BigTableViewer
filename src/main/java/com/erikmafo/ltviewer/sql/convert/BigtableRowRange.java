package com.erikmafo.ltviewer.sql.convert;

import com.erikmafo.ltviewer.util.Check;
import com.google.cloud.bigtable.data.v2.models.Query;
import com.google.cloud.bigtable.data.v2.models.Range;
import org.jetbrains.annotations.NotNull;

public class BigtableRowRange {

    private final String singleRowKey;
    private final Range.ByteStringRange rowRange;

    public BigtableRowRange(String singleRowKey, Range.ByteStringRange rowRange) {
        Check.notAllNull(
                "One of the constructor arguments for BigtableRowRange must have a value", singleRowKey, rowRange);
        this.singleRowKey = singleRowKey;
        this.rowRange = rowRange;
    }

    public void applyToQuery(@NotNull Query query) {
        if (singleRowKey != null) {
            query.rowKey(singleRowKey);
        } else {
            query.range(rowRange);
        }
    }
}
