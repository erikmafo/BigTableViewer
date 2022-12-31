package com.erikmafo.btviewer.sql.convert;

import com.erikmafo.btviewer.sql.convert.util.DateTimeFormatUtil;
import org.junit.Test;

import static org.junit.Assert.*;

public class DateTimeFormatUtilTest {

    @Test
    public void toMicros_withDateTimeString_returnsNumberOfMicrosSince1970() {
        // given
        var dateTime = "1970-01-01";

        // when
        var micros = DateTimeFormatUtil.toMicros(dateTime);

        // then
        assertEquals(0, micros);
    }
}