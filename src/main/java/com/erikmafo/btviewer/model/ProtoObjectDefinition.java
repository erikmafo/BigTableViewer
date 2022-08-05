package com.erikmafo.btviewer.model;

import com.erikmafo.btviewer.util.Check;
import org.jetbrains.annotations.NotNull;

public class ProtoObjectDefinition {
    private final String descriptorSetFile;
    private final String protoFile;
    private final String messageType;

    public ProtoObjectDefinition(String descriptorSetFile, String protoFile, String messageType) {
        Check.notNullOrEmpty(descriptorSetFile, "descriptorSetFile");
        Check.notNullOrEmpty(protoFile, "protoFile");
        Check.notNullOrEmpty(messageType, "messageType");

        this.descriptorSetFile = descriptorSetFile;
        this.protoFile = protoFile;
        this.messageType = messageType;
    }

    @NotNull
    public String getDescriptorSetFile() {
        return descriptorSetFile;
    }

    @NotNull
    public String getProtoFile() {
        return protoFile;
    }

    @NotNull
    public String getMessageType() {
        return messageType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProtoObjectDefinition that = (ProtoObjectDefinition) o;

        if (!descriptorSetFile.equals(that.descriptorSetFile)) return false;
        if (!protoFile.equals(that.protoFile)) return false;
        return messageType.equals(that.messageType);
    }

    @Override
    public int hashCode() {
        int result = descriptorSetFile.hashCode();
        result = 31 * result + protoFile.hashCode();
        result = 31 * result + messageType.hashCode();
        return result;
    }
}
