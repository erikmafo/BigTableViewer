package com.erikmafo.btviewer.sql.convert.rowset;

import com.erikmafo.btviewer.sql.convert.util.ByteStringUtil;
import com.google.cloud.bigtable.data.v2.models.Range;
import com.google.protobuf.ByteString;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public record BigtableRowSet(List<Range.ByteStringRange> ranges, List<ByteString> keys) {

    @NotNull
    @Contract(" -> new")
    public static BigtableRowSet unbounded() {
        return new BigtableRowSet(
                Collections.singletonList(Range.ByteStringRange.unbounded()), Collections.emptyList());
    }

    @NotNull
    @Contract("_ -> new")
    public BigtableRowSet withEquals(@NotNull ByteString key) {
        return withEqualsAny(Collections.singletonList(key));
    }

    @NotNull
    @Contract("_ -> new")
    public BigtableRowSet withEqualsAny(@NotNull Collection<ByteString> keysCollection) {
        return new BigtableRowSet(
                Collections.emptyList(),
                keysCollection.stream().filter(key -> contains(key)).collect(Collectors.toList()));
    }

    @NotNull
    public BigtableRowSet withGreaterThan(ByteString key) {
        return updatedRowSet(k -> ByteStringUtil.greaterThan(k, key), range -> range.startOpen(key));
    }

    @NotNull
    public BigtableRowSet withGreaterThanOrEqual(ByteString key) {
        return updatedRowSet(k -> ByteStringUtil.greaterThanOrEqual(k, key), range -> range.startClosed(key));
    }

    @NotNull
    public BigtableRowSet withLessThan(ByteString key) {
        return updatedRowSet(k -> ByteStringUtil.lessThan(k, key), range -> range.endOpen(key));
    }

    @NotNull
    public BigtableRowSet withLessThanOrEqual(ByteString key) {
        return updatedRowSet(k -> ByteStringUtil.lessThanOrEqual(k, key), range -> range.endClosed(key));
    }

    public boolean isEmpty() {
        return ranges().isEmpty() && keys.isEmpty();
    }

    public boolean contains(ByteString key) {
        return keys().contains(key) || ranges().stream().anyMatch(range -> ByteStringUtil.isContainedIn(range, key));
    }

    @NotNull
    private BigtableRowSet updatedRowSet(
            Predicate<ByteString> keyPredicate,
            Function<Range.ByteStringRange, Range.ByteStringRange> updateRange) {
        return new BigtableRowSet(updatedRanges(updateRange), updatedKeys(keyPredicate));
    }

    @NotNull
    private List<Range.ByteStringRange> updatedRanges(Function<Range.ByteStringRange, Range.ByteStringRange> updateRange) {
        return ranges()
                .stream()
                .map(r -> updateRange.apply(r))
                .filter(r -> ByteStringUtil.isValid(r))
                .collect(Collectors.toList());
    }

    @NotNull
    private List<ByteString> updatedKeys(Predicate<ByteString> keyPredicate) {
        return keys()
                .stream()
                .filter(k -> keyPredicate.test(k))
                .collect(Collectors.toList());
    }
}
