package com.erikmafo.btviewer.util;

import org.junit.Test;

public class CheckTest {

    @Test(expected = IllegalArgumentException.class)
    public void notAllNull_withAllParamsNull_throws() {
        Check.notAllNull("msg", null, null);
    }

    @Test
    public void notAllNull_withOneParamNotNull_doesNotThrow() {
        Check.notAllNull("msg", null, "not null");
    }

    @Test
    public void notAllNull_withNoParams_doesNotThrow() {
        Check.notAllNull("msg");
    }

    @Test(expected = IllegalArgumentException.class)
    public void notNull_withNull_throws() {
        Check.notNull(null, "paramName");
    }

    @Test
    public void notNull_withNotNull_doesNotThrow() {
        Check.notNull("", "paramName");
    }

    @Test(expected = IllegalArgumentException.class)
    public void notNullOrEmpty_withParamEmpty_throws() {
        Check.notNullOrEmpty("", "paramName");
    }
}