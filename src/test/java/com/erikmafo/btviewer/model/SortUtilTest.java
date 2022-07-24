package com.erikmafo.btviewer.model;

import org.junit.Test;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class SortUtilTest {

    @Test
    public void byFamilyThenQualifier_ordersByFamilyFirstThenQualifier() {
        // given
        var columns = Arrays.asList(
                new BigtableColumn("f2", "q1"),
                new BigtableColumn("f2", "q2"),
                new BigtableColumn("f1", "q2"),
                new BigtableColumn("f1", "q1")
        );

        // when
        var columnsSorted = columns
                .stream().sorted(SortUtil::byFamilyThenQualifier).collect(Collectors.toList());

        // then
        var expected = Arrays.asList(
                new BigtableColumn("f1", "q1"),
                new BigtableColumn("f1", "q2"),
                new BigtableColumn("f2", "q1"),
                new BigtableColumn("f2", "q2"));

        assertEquals(expected, columnsSorted);
    }

    @Test
    public void byFamilyThenQualifier_withCellDefinitions_ordersByFamilyFirstThenQualifier() {
        // given
        var columns = Arrays.asList(
                new CellDefinition(ValueTypeConstants.STRING, "f2", "q1"),
                new CellDefinition(ValueTypeConstants.STRING, "f2", "q2"),
                new CellDefinition(ValueTypeConstants.STRING, "f1", "q2"),
                new CellDefinition(ValueTypeConstants.STRING, "f1", "q1")
        );

        // when
        var columnsSorted = columns
                .stream().sorted(SortUtil::byFamilyThenQualifier).collect(Collectors.toList());

        // then
        var expected = Arrays.asList(
                new CellDefinition(ValueTypeConstants.STRING, "f1", "q1"),
                new CellDefinition(ValueTypeConstants.STRING, "f1", "q2"),
                new CellDefinition(ValueTypeConstants.STRING, "f2", "q1"),
                new CellDefinition(ValueTypeConstants.STRING, "f2", "q2"));

        assertEquals(expected, columnsSorted);
    }
}