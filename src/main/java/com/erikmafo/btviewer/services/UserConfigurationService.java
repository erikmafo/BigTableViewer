package com.erikmafo.btviewer.services;
import com.erikmafo.btviewer.model.*;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

/**
 * Created by erikmafo on 25.12.17.
 */
public class UserConfigurationService {

    private static final String PREFERENCES_USER_ROOT_NODE_NAME = "bigtable-viewer-configs";
    private static final String BIGTABLE_TABLE_DEFINITIONS = "bigtable-table-definitions";
    private static final String CREDENTIAL_RECORDS = "credential-records";
    private static final String CREDENTIALS_PATH = "credentials-path";

    private final Gson gson = new Gson();

    public String getCredentialsPath() {
        return getPreferences().get(CREDENTIALS_PATH, null);
    }

    public void setCredentialsPath(String credentialsPath) {
        getPreferences().put(CREDENTIALS_PATH, credentialsPath);
    }

    public void updateBigtableDefinitions(List<BigtableTable> bigtableOptions) {

        String json = gson.toJson(bigtableOptions);

        getPreferences().put(BIGTABLE_TABLE_DEFINITIONS, json);
    }

    public List<BigtableTable> loadBigtableDefinitions() {

        Preferences prefs = getPreferences();

        String json = prefs.get(BIGTABLE_TABLE_DEFINITIONS, null);

        if (json == null) {
            return Collections.emptyList();
        }

        Type listType = new TypeToken<List<BigtableValueParser>>() {
        }.getType();


        try {
            return gson.fromJson(json, listType);
        } catch (Exception ex) {
            Logger.getLogger(UserConfigurationService.class.getName()).log(Level.WARNING, "Unable to read bigtable options", ex);
            return Collections.emptyList();
        }
    }


    public List<CredentialsRecord> loadCredentialRecords() {

        String json = getPreferences().get(CREDENTIAL_RECORDS, null);

        if (json == null) {
            return Collections.emptyList();
        }

        Type listType = new TypeToken<List<CredentialsRecordDto>>() {}.getType();

        List<CredentialsRecordDto> dtos = gson.fromJson(json, listType);

        if (dtos == null) {
            return Collections.emptyList();
        }

        return dtos.stream().map(CredentialsRecord::fromDto).collect(Collectors.toList());
    }


    public void updateCredentialRecords(List<CredentialsRecord> records) {

        List<CredentialsRecordDto> dtos = records.stream().map(CredentialsRecord::toDto).collect(Collectors.toList());

        String json = gson.toJson(dtos);

        getPreferences().put(CREDENTIAL_RECORDS, json);
    }

    private Preferences getPreferences() {
        return Preferences.userRoot().node(PREFERENCES_USER_ROOT_NODE_NAME);
    }

}
