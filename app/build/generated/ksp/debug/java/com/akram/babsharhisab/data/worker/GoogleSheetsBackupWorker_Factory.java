package com.akram.babsharhisab.data.worker;

import android.content.Context;
import androidx.work.WorkerParameters;
import com.akram.babsharhisab.core.security.SecureConfigStore;
import com.akram.babsharhisab.domain.repository.BackupSyncRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation"
})
public final class GoogleSheetsBackupWorker_Factory {
  private final Provider<BackupSyncRepository> backupSyncRepositoryProvider;

  private final Provider<SecureConfigStore> secureConfigStoreProvider;

  public GoogleSheetsBackupWorker_Factory(
      Provider<BackupSyncRepository> backupSyncRepositoryProvider,
      Provider<SecureConfigStore> secureConfigStoreProvider) {
    this.backupSyncRepositoryProvider = backupSyncRepositoryProvider;
    this.secureConfigStoreProvider = secureConfigStoreProvider;
  }

  public GoogleSheetsBackupWorker get(Context appContext, WorkerParameters workerParams) {
    return newInstance(appContext, workerParams, backupSyncRepositoryProvider.get(), secureConfigStoreProvider.get());
  }

  public static GoogleSheetsBackupWorker_Factory create(
      Provider<BackupSyncRepository> backupSyncRepositoryProvider,
      Provider<SecureConfigStore> secureConfigStoreProvider) {
    return new GoogleSheetsBackupWorker_Factory(backupSyncRepositoryProvider, secureConfigStoreProvider);
  }

  public static GoogleSheetsBackupWorker newInstance(Context appContext,
      WorkerParameters workerParams, BackupSyncRepository backupSyncRepository,
      SecureConfigStore secureConfigStore) {
    return new GoogleSheetsBackupWorker(appContext, workerParams, backupSyncRepository, secureConfigStore);
  }
}
