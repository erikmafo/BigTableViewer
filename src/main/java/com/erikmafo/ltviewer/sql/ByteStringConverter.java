package com.erikmafo.ltviewer.sql;

import com.google.protobuf.ByteString;

public interface ByteStringConverter {

    /**
     * Takes in a field and a value and converts them into a ByteString,
     * according to the datatype that is used to store the field.
     *
     * @param field representation of the field to be converted.
     * @param value representation of the value to be converted.
     * @return a {@link ByteString} value.
     */
    ByteString toByteString(Field field, Value value);
}
