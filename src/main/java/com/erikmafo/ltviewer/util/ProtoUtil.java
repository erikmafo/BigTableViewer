package com.erikmafo.ltviewer.util;

import com.erikmafo.ltviewer.model.ProtoObjectDefinition;
import com.google.protobuf.ByteString;
import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.NoSuchElementException;

public class ProtoUtil {
    private final static Cache cache = new Cache();

    @NotNull
    public static String toJson(ByteString data, ProtoObjectDefinition proto) throws IOException {
        Check.notNull(data, "data");
        Check.notNull(proto, "protoObjectDefinition");
        return toJson(data, getDescriptor(proto));
    }

    @NotNull
    private static String toJson(@NotNull ByteString data, @NotNull Descriptors.Descriptor descriptor) throws InvalidProtocolBufferException {
        return JsonFormat.printer().print(toDynamicMessage(data, descriptor));
    }

    @NotNull
    @Contract("_, _ -> new")
    private static DynamicMessage toDynamicMessage(@NotNull ByteString data, @NotNull Descriptors.Descriptor descriptor) throws InvalidProtocolBufferException {
        return DynamicMessage.parseFrom(descriptor, data);
    }

    @NotNull
    private static Descriptors.Descriptor getDescriptor(@NotNull ProtoObjectDefinition proto) throws IOException {
        return getDescriptor(getOrReadDescriptorSet(proto.getDescriptorSetFile()), proto.getProtoFile(), proto.getMessageType());
    }

    @NotNull
    private static Descriptors.Descriptor getDescriptor(@NotNull DescriptorProtos.FileDescriptorSet fileDescriptorSet, String protoFile, String messageTypeName) {
        return fileDescriptorSet
                .getFileList()
                .stream()
                .filter(fileDescriptor -> fileDescriptor.getName().equals(protoFile))
                .flatMap(fileDescriptor -> fileDescriptor
                        .getMessageTypeList()
                        .stream()
                        .filter(messageType -> messageType.getName().equals(protoFile)))
                .map(descriptorProto -> descriptorProto.getDescriptorForType())
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(String.format(
                        "Could not find message type %s within proto file %s.", messageTypeName, protoFile)));
    }

    private static DescriptorProtos.FileDescriptorSet getOrReadDescriptorSet(@NotNull String filename) throws IOException {
        return cache.getOrCreate(filename, ProtoUtil::readDescriptorSet);
    }

    @NotNull
    private static DescriptorProtos.FileDescriptorSet readDescriptorSet(@NotNull String filename) throws IOException {
        try (var descriptorSetFile = new FileInputStream(filename)) {
            return DescriptorProtos.FileDescriptorSet.parseFrom(descriptorSetFile);
        }
    }
}
