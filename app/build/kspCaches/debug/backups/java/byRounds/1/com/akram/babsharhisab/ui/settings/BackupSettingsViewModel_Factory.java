package com.akram.babsharhisab.ui.settings;

import com.akram.babsharhisab.core.security.SecureConfigStore;
import com.akram.babsharhisab.data.worker.BackupWorkScheduler;
import com.akram.babsharhisab.domain.repository.BackupSyncRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class BackupSettingsViewModel_Factory implements Factory<BackupSettingsViewModel> {
  private final Provider<BackupSyncRepository> backupSyncRepositoryProvider;

  private final Provider<SecureConfigStore> secureConfigStoreProvider;

  private final Provider<BackupWorkScheduler> backupWorkSchedulerProvider;

  public BackupSettingsViewModel_Factory(
      Provider<BackupSyncRepository> backupSyncRepositoryProvider,
      Provider<SecureConfigStore> secureConfigStoreProvider,
      Provider<BackupWorkScheduler> backupWorkSchedulerProvider) {
    this.backupSyncRepositoryProvider = backupSyncRepositoryProvider;
    this.secureConfigStoreProvider = secureConfigStoreProvider;
    this.backupWorkSchedulerProvider = backupWorkSchedulerProvider;
  }

  @Override
  public BackupSettingsViewModel get() {
    return newInstance(backupSyncRepositoryProvider.get(), secureConfigStoreProvider.get(), backupWorkSchedulerProvider.get());
  }

  public static BackupSettingsViewModel_Factory create(
      Provider<BackupSyncRepository> backupSyncRepositoryProvider,
      Provider<SecureConfigStore> secureConfigStoreProvider,
      Provider<BackupWorkScheduler> backupWorkSchedulerProvider) {
    return new BackupSettingsViewModel_Factory(backupSyncRepositoryProvider, secureConfigStoreProvider, backupWorkSchedulerProvider);
  }

  public static BackupSettingsViewModel newInstance(BackupSyncRepository backupSyncRepository,
      SecureConfigStore secureConfigStore, BackupWorkScheduler backupWorkScheduler) {
    return new BackupSettingsViewModel(backupSyncRepository, secureConfigStore, backupWorkScheduler);
  }
}
