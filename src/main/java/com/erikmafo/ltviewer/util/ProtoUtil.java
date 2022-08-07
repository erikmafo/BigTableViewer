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
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProtoUtil {
    private final static Cache descriptorSetCache = new Cache();

    @NotNull
    public static List<String> listProtoFiles(String descriptorSetFile) throws IOException {
        return getFileDescriptorProtoStream(descriptorSetFile)
                .map(file -> file.getName())
                .collect(Collectors.toList());
    }

    @NotNull
    public static List<String> listMessageTypes(String descriptorSetFile, String fileName) throws IOException {
        return getFileDescriptorProtoStream(descriptorSetFile)
                .filter(fileDescriptorProto -> fileDescriptorProto.getName().equals(fileName))
                .flatMap(fileDescriptorProto -> fileDescriptorProto.getMessageTypeList().stream())
                .map(descriptorProto -> descriptorProto.getName())
                .collect(Collectors.toList());
    }

    @NotNull
    public static String toJson(ByteString data, ProtoObjectDefinition proto) throws IOException {
        Check.notNull(data, "data");
        Check.notNull(proto, "protoObjectDefinition");
        return toJson(data, getDescriptor(proto));
    }

    @NotNull
    private static Stream<DescriptorProtos.FileDescriptorProto> getFileDescriptorProtoStream(String descriptorSetFile) throws IOException {
        Check.notNullOrEmpty(descriptorSetFile, "descriptorSetFile");
        return getOrReadDescriptorSet(descriptorSetFile)
                .getFileList()
                .stream();
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
                .map(fileDescriptorProto -> buildFileDescriptor(fileDescriptorProto, fileDescriptorSet))
                .filter(fileDescriptor -> fileDescriptor.getName().equals(protoFile))
                .map(fileDescriptor -> fileDescriptor.findMessageTypeByName(messageTypeName))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(String.format(
                        "Could not find message type %s within proto file %s.", messageTypeName, protoFile)));
    }

    private static Descriptors.FileDescriptor buildFileDescriptor(DescriptorProtos.FileDescriptorProto fileDescriptorProto, DescriptorProtos.FileDescriptorSet descriptorSet) {
        try {
            return Descriptors.FileDescriptor.buildFrom(fileDescriptorProto, getDependencies(fileDescriptorProto, descriptorSet));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    private static DescriptorProtos.FileDescriptorSet getOrReadDescriptorSet(@NotNull String filename) throws IOException {
        return descriptorSetCache.getOrCreate(filename, ProtoUtil::readDescriptorSet);
    }

    @NotNull
    private static DescriptorProtos.FileDescriptorSet readDescriptorSet(@NotNull String filename) throws IOException {
        try (var descriptorSetFile = new FileInputStream(filename)) {
            return DescriptorProtos.FileDescriptorSet.parseFrom(descriptorSetFile);
        }
    }

    @NotNull
    private static Descriptors.FileDescriptor[] getDependencies(@NotNull DescriptorProtos.FileDescriptorProto fileDescProto, DescriptorProtos.FileDescriptorSet fileDescriptorSet) {
        return fileDescProto
                .getDependencyList()
                .stream()
                .map(dependency -> buildDependencyFileDescriptor(dependency, fileDescriptorSet))
                .toArray(Descriptors.FileDescriptor[]::new);
    }

    private static Descriptors.FileDescriptor buildDependencyFileDescriptor(String dependency, @NotNull DescriptorProtos.FileDescriptorSet fileDescriptorSet) {
        return buildFileDescriptor(getDependencyFileDescProto(dependency, fileDescriptorSet), fileDescriptorSet);
    }

    @NotNull
    private static DescriptorProtos.FileDescriptorProto getDependencyFileDescProto(String dependency, @NotNull DescriptorProtos.FileDescriptorSet fileDescriptorSet) {
        return fileDescriptorSet
                .getFileList()
                .stream()
                .filter(f -> f.getName().equals(dependency))
                .findFirst()
                .orElseThrow();
    }
}
