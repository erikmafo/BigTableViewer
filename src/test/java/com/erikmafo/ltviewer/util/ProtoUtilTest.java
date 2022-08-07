package com.erikmafo.ltviewer.util;

import com.erikmafo.ltviewer.model.ProtoObjectDefinition;
import com.erikmafo.ltviewer.services.internal.testdata.PersonOuterClass;
import com.google.gson.Gson;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonParser;
import com.google.gson.JsonSerializer;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.*;

public class ProtoUtilTest {

    @Test
    public void listProtoFiles_returnsAllProtoFilesInDescriptorSet() throws IOException {
        var protoFiles = ProtoUtil.listProtoFiles(getDescriptorSetFile());
        assertEquals(Arrays.asList("person.proto"), protoFiles);
    }

    @Test
    public void listMessageTypes_returnsAllMessageTypesInProtoFile() throws IOException {
        var messageTypes = ProtoUtil.listMessageTypes(getDescriptorSetFile(), "person.proto");
        assertEquals(Arrays.asList("Person"), messageTypes);
    }

    @Test
    public void toJson_convertsByteStringToJson() throws IOException {
        var person = getPerson(0);

        var json = ProtoUtil.toJson(
                person.toByteString(),
                new ProtoObjectDefinition(
                        getDescriptorSetFile(),
                        "person.proto",
                        "Person"));

        assertEquals("{\n  \"name\": \"Person-0\",\n  \"id\": \"0\"\n}".formatted(), json);
    }

    private String getDescriptorSetFile() {
        return getClass().getResource("/descriptorset.pb").getFile();
    }

    @NotNull
    private static PersonOuterClass.Person getPerson(int i) {
        return PersonOuterClass.Person
                .newBuilder()
                .setName("Person-" + i)
                .setId("" + i)
                .setAge(i % 100)
                .build();
    }
}