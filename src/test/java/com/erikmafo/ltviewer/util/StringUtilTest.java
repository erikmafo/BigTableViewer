package com.erikmafo.ltviewer.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class StringUtilTest {

    @Test
    public void isNullOrEmpty_withEmptyString_returnsTrue() {
        assertTrue(StringUtil.isNullOrEmpty(null));
    }

    @Test
    public void isNullOrEmpty_withNull_returnsTrue() {
        assertTrue(StringUtil.isNullOrEmpty(""));
    }

    @Test
    public void isNullOrEmpty_withNonEmptyString_returnsFalse() {
        assertFalse(StringUtil.isNullOrEmpty("foo"));
    }
}