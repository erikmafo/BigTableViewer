package com.erikmafo.btviewer.sql.convert.filter;

import com.erikmafo.btviewer.sql.convert.util.ByteStringUtil;
import com.google.cloud.bigtable.data.v2.models.Range;
import com.google.protobuf.ByteString;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.function.Predicate;

public record BigtableFilter(ByteString exact, Range.ByteStringRange range) {

    @NotNull
    @Contract("_ -> new")
    public BigtableFilter withExact(@NotNull ByteString value) {
        return new BigtableFilter(value, null);
    }

    @NotNull
    public BigtableFilter withGreaterThan(ByteString key) {
        return updatedFilter(k -> ByteStringUtil.greaterThan(k, key), range -> range.startOpen(key));
    }

    @NotNull
    public BigtableFilter withGreaterThanOrEqual(ByteString key) {
        return updatedFilter(k -> ByteStringUtil.greaterThanOrEqual(k, key), range -> range.startClosed(key));
    }

    @NotNull
    public BigtableFilter withLessThan(ByteString key) {
        return updatedFilter(k -> ByteStringUtil.lessThan(k, key), range -> range.endOpen(key));
    }

    @NotNull
    public BigtableFilter withLessThanOrEqual(ByteString key) {
        return updatedFilter(k -> ByteStringUtil.lessThanOrEqual(k, key), range -> range.endClosed(key));
    }

    @NotNull
    private BigtableFilter updatedFilter(
            Predicate<ByteString> keyPredicate,
            Function<Range.ByteStringRange, Range.ByteStringRange> updateRange) {
        return new BigtableFilter(updatedExact(keyPredicate), updatedRange(updateRange));
    }

    @NotNull
    private Range.ByteStringRange updatedRange(@NotNull Function<Range.ByteStringRange, Range.ByteStringRange> updateRange) {
        var updatedRange = updateRange.apply(range());
        if (ByteStringUtil.isValid(updatedRange)) {
            return updatedRange;
        }
        return null;
    }

    @NotNull
    private ByteString updatedExact(@NotNull Predicate<ByteString> keyPredicate) {
        if (keyPredicate.test(exact)) {
            return exact;
        }

        return null;
    }
}
