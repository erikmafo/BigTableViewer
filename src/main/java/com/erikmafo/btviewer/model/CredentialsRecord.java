package com.erikmafo.btviewer.model;

import com.erikmafo.btviewer.exceptions.InvalidCredentialsRecordException;

import java.nio.file.Path;
import java.nio.file.Paths;

public class CredentialsRecord {

    private String projectId;

    private String instanceId;

    private Path credentialsPath;

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public Path getCredentialsPath() {
        return credentialsPath;
    }

    public void setCredentialsPath(Path credentialsPath) {
        this.credentialsPath = credentialsPath;
    }

    public void ensureIsValid() throws InvalidCredentialsRecordException {

        String errorMessage = null;

        if (projectId == null || projectId.isEmpty()) {
            errorMessage = "Project id is not specified";
        } else if (instanceId == null || instanceId.isEmpty()) {
            errorMessage = "Instance id is not specified";
        } else if (credentialsPath == null) {
            errorMessage = "Credentials path is not specified";
        } else if (!credentialsPath.toFile().exists()) {
            errorMessage = "Credentials path does not exist";
        }

        if (errorMessage != null) {
            throw new InvalidCredentialsRecordException(errorMessage);
        }
    }

    public CredentialsRecordDto toDto() {
        CredentialsRecordDto dto = new CredentialsRecordDto();
        dto.setCredentialsPath(credentialsPath.toFile().getPath());
        dto.setInstanceId(instanceId);
        dto.setProjectId(projectId);
        return dto;
    }

    public static CredentialsRecord fromDto(CredentialsRecordDto dto) {
        CredentialsRecord record = new CredentialsRecord();
        record.setCredentialsPath(Paths.get(dto.getCredentialsPath()));
        record.setProjectId(dto.getProjectId());
        record.setInstanceId(dto.getInstanceId());
        return record;
    }
}
