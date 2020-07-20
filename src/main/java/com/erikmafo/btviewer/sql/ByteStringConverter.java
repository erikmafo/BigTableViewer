package com.erikmafo.btviewer.sql;

import com.google.protobuf.ByteString;

public interface ByteStringConverter {
    ByteString toByteString(Field field, Value value);
}
