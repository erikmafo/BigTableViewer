package com.erikmafo.btviewer.services;

import com.erikmafo.btviewer.model.*;
import com.google.bigtable.repackaged.com.google.bigtable.admin.v2.ListTablesRequest;
import com.google.bigtable.repackaged.com.google.bigtable.admin.v2.ProjectName;
import com.google.bigtable.repackaged.com.google.bigtable.admin.v2.Table;
import com.google.bigtable.repackaged.com.google.bigtable.v2.ReadRowsRequest;
import com.google.bigtable.repackaged.com.google.bigtable.v2.RowRange;
import com.google.bigtable.repackaged.com.google.bigtable.v2.RowSet;
import com.google.bigtable.repackaged.com.google.cloud.bigtable.admin.v2.BigtableInstanceAdminClient;
import com.google.bigtable.repackaged.com.google.cloud.bigtable.admin.v2.BigtableInstanceAdminSettings;
import com.google.bigtable.repackaged.com.google.cloud.bigtable.config.BigtableOptions;
import com.google.bigtable.repackaged.com.google.cloud.bigtable.config.CallOptionsConfig;
import com.google.bigtable.repackaged.com.google.cloud.bigtable.config.CredentialOptions;
import com.google.bigtable.repackaged.com.google.cloud.bigtable.grpc.BigtableInstanceName;
import com.google.bigtable.repackaged.com.google.cloud.bigtable.grpc.BigtableSession;
import com.google.bigtable.repackaged.com.google.cloud.bigtable.grpc.BigtableTableAdminClient;
import com.google.bigtable.repackaged.com.google.protobuf.ByteString;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by erikmafo on 23.12.17.
 */
public class BigtableClient {

    private final Map<String, CredentialOptions> credentialOptionsCache = new ConcurrentHashMap<>();
    private static final Charset DEFAULT_CHARSET = Charset.defaultCharset();

    private CredentialOptions getCredentialOptions(Path credentialsPath) throws IOException {

        if (credentialsPath == null) {
            return CredentialOptions.defaultCredentials();
        }

        if (credentialOptionsCache.containsKey(credentialsPath.toString())) {
            return credentialOptionsCache.get(credentialsPath.toString());
        }

        CredentialOptions credentialOptions = CredentialOptions.jsonCredentials(Files.newInputStream(credentialsPath));
        credentialOptionsCache.put(credentialsPath.toString(), credentialOptions);
        return credentialOptions;
    }

    public BigtableResultScanner execute(BigtableReadRequest bigtableReadRequest) throws IOException {

        BigtableTable bigtableTable = bigtableReadRequest.getBigtableTable();
        BigtableRowRange bigtableRowRange = bigtableReadRequest.getScan();
        Path credentialsPath = bigtableReadRequest.getCredentialsPath();

        BigtableSession bigtableSession = getBigtableSession(
                bigtableTable.getProjectId(),
                bigtableTable.getInstanceId(),
                credentialsPath);

        RowRange rowRange = RowRange.newBuilder()
                .setStartKeyClosed(ByteString.copyFrom(bigtableRowRange.getFrom(), DEFAULT_CHARSET))
                .setEndKeyClosed(ByteString.copyFrom(bigtableRowRange.getTo(), DEFAULT_CHARSET))
                .build();

        RowSet rowSet = RowSet.newBuilder()
                .addRowRanges(rowRange)
                .build();

        ReadRowsRequest readRowsRequest = ReadRowsRequest.newBuilder()
                .setRowsLimit(bigtableRowRange.getMaxRows())
                .setRows(rowSet)
                .setTableName(bigtableTable.getName())
                .build();

        return new BigtableResultScanner(
                bigtableSession,
                bigtableSession.getDataClient().readFlatRows(readRowsRequest),
                bigtableTable.getCellDefinitions());

    }

    public List<String> listTables(BigtableInstance bigtableInstance, Path credentialsPath) throws IOException {
        try (BigtableSession session = getBigtableSession(
                bigtableInstance.getProjectId(),
                bigtableInstance.getInstanceId(),
                credentialsPath)) {
            BigtableTableAdminClient adminClient = session.getTableAdminClient();
            return adminClient
                    .listTables(ListTablesRequest.newBuilder()
                            .setParent(new BigtableInstanceName(
                                    bigtableInstance.getProjectId(),
                                    bigtableInstance.getInstanceId()).getInstanceName())
                            .build())
                    .getTablesList()
                    .stream()
                    .map(Table::getName)
                    .collect(Collectors.toList());
        }
    }


    public List<BigtableInstance> listInstances(String projectId, String credentialsPath) throws IOException {
        try (BigtableInstanceAdminClient client = BigtableInstanceAdminClient.create(
                BigtableInstanceAdminSettings.newBuilder()
                        .setProjectName(ProjectName.of(projectId))
                        .build()))
        {
            return client.listInstances()
                    .stream()
                    .map(instance -> new BigtableInstance(projectId, instance.getId()))
                    .collect(Collectors.toList());
        }
    }


    private BigtableSession getBigtableSession(String projectId, String instanceId, Path credentialsPath) throws IOException {

        BigtableOptions googleBigtableOptions =
                BigtableOptions.builder()
                        .setCallOptionsConfig(CallOptionsConfig.builder()
                                .setUseTimeout(true)
                                .setShortRpcTimeoutMs((int)TimeUnit.SECONDS.toMillis(30))
                                .setLongRpcTimeoutMs((int)TimeUnit.MINUTES.toMillis(1))
                                .build())
                        .setProjectId(projectId)
                        .setInstanceId(instanceId)
                        .setCredentialOptions(getCredentialOptions(credentialsPath))
                        .setUserAgent("bigtable-viewer")
                        .build();

        BigtableSession bigtableSession = new BigtableSession(googleBigtableOptions);

        return bigtableSession;
    }
}
