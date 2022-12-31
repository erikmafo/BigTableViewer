package com.erikmafo.btviewer.sql.convert.util;

import com.google.cloud.bigtable.data.v2.internal.ByteStringComparator;
import com.google.cloud.bigtable.data.v2.models.Range;
import com.google.protobuf.ByteString;
import org.jetbrains.annotations.NotNull;

public class ByteStringUtil {

    public static boolean equals(@NotNull ByteString first, @NotNull ByteString second) {
        return ByteStringComparator.INSTANCE.compare(first, second) == 0;
    }

    /**
     * Checks if the first argument is greater than the second argument.
     * @param first - the first byte string to compare.
     * @param second - the second byte string to compare.
     * @return true if the first byte string is greater than the second byte string.
     */
    public static boolean greaterThan(@NotNull ByteString first, @NotNull ByteString second) {
        return ByteStringComparator.INSTANCE.compare(first, second) > 0;
    }

    /**
     * Checks if the first argument is greater than or equal to the second argument.
     * @param first - the first byte string to compare.
     * @param second - the second byte string to compare.
     * @return true if the first byte string is greater than or equal to the second byte string.
     */
    public static boolean greaterThanOrEqual(@NotNull ByteString first, @NotNull ByteString second) {
        return ByteStringComparator.INSTANCE.compare(first, second) >= 0;
    }

    /**
     * Checks if the first argument is less than the second argument.
     * @param first - the first byte string to compare.
     * @param second - the second byte string to compare.
     * @return true if the first byte string is less than the second byte string.
     */
    public static boolean lessThan(@NotNull ByteString first, @NotNull ByteString second) {
        return ByteStringComparator.INSTANCE.compare(first, second) < 0;
    }

    /**
     * Checks if the first argument is less than or equal to the second argument.
     * @param first - the first byte string to compare.
     * @param second - the second byte string to compare.
     * @return true if the first byte string is less than or equal to the second byte string.
     */
    public static boolean lessThanOrEqual(@NotNull ByteString first, @NotNull ByteString second) {
        return ByteStringComparator.INSTANCE.compare(first, second) <= 0;
    }

    /**
     * Checks if the specified range contains the given byte string.
     * @param range - a byte string range.
     * @param byteString - a byte string.
     * @return true if the range contains the byte string, false otherwise.
     */
    public static boolean isContainedIn(@NotNull Range.ByteStringRange range, ByteString byteString) {
        return isIncludedByStartBound(range, byteString) && isIncludedByEndBound(range, byteString);
    }

    public static boolean isValid(@NotNull Range.ByteStringRange range) {
        if (range.getEndBound() == Range.BoundType.UNBOUNDED || range.getStartBound() == Range.BoundType.UNBOUNDED) {
            return false;
        }

        if (greaterThan(range.getStart(), range.getEnd())) {
            return false;
        }

        if (equals(range.getStart(), range.getEnd()) &&
                (range.getStartBound() == Range.BoundType.OPEN || range.getEndBound() == Range.BoundType.OPEN)) {
            return false;
        }

        return true;
    }

    private static boolean isIncludedByStartBound(@NotNull Range.ByteStringRange range, ByteString byteString) {
        return switch (range.getStartBound()) {
            case UNBOUNDED -> true;
            case CLOSED -> greaterThanOrEqual(byteString, range.getStart());
            case OPEN -> greaterThan(byteString, range.getStart());
        };
    }

    private static boolean isIncludedByEndBound(@NotNull Range.ByteStringRange range, ByteString byteString) {
        return switch (range.getEndBound()) {
            case UNBOUNDED -> true;
            case CLOSED -> lessThanOrEqual(byteString, range.getEnd());
            case OPEN -> lessThan(byteString, range.getEnd());
        };
    }
}
