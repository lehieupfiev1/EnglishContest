package com.pfiev.englishcontest.roomdb.migration;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.pfiev.englishcontest.roomdb.AppDatabase;
import com.pfiev.englishcontest.roomdb.entity.AppConfig;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class DatabaseMigration {
    private static DatabaseMigration instance = null;
    private final String DB_VERSION_FIELD_NAME= "db_version";
    private final int VERSION_1 = 1;
    private final int VERSION_2 = 2;
    public static final int LATEST_VERSION = 2;
    private final int[] listVersion;

    public static DatabaseMigration getInstance() {
        if (instance == null) {
            instance = new DatabaseMigration();
        }
        return instance;
    }

    private DatabaseMigration() {
        listVersion = new int[2];
        listVersion[0] = VERSION_1;
        listVersion[1] = VERSION_2;
    }

    public void upgradeDatabase(AppDatabase dbInstance) {
        ListenableFuture<AppConfig> dbQuery = dbInstance.appConfigDao()
                .getConfig(DB_VERSION_FIELD_NAME);
        Futures.addCallback(
                dbQuery,
                new FutureCallback<AppConfig>() {
                    @Override
                    public void onSuccess(@Nullable AppConfig appConfig) {
                        int currentVersion = 0;
                        if (appConfig != null) {
                            currentVersion = Integer.parseInt(appConfig.value);
                        }
                        if (currentVersion == LATEST_VERSION) return;

                        for (int version : listVersion) {
                            if (currentVersion < version)
                                upgradeToVersion(dbInstance, version);
                        }
                        if (appConfig == null) {
                            appConfig = new AppConfig();
                            appConfig.field = DB_VERSION_FIELD_NAME;
                            appConfig.value = Integer.toString(LATEST_VERSION);
                            dbInstance.appConfigDao().insertConfig(appConfig);
                        } else {
                            appConfig.value = Integer.toString(LATEST_VERSION);
                            dbInstance.appConfigDao().updateConfig(appConfig);
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {}
                },
                Executors.newSingleThreadExecutor()
        );
    }

    private void upgradeToVersion(AppDatabase dbInstance, int version) {
        if (version == VERSION_1) {
            new Version_1().importData(dbInstance);
        }
        if (version == VERSION_2) {
            new Version_2().importData(dbInstance);
        }
    }

    public interface DbVersion {
        public void importData(AppDatabase dbInstance);
    }
}
