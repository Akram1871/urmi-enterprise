package com.akram.babsharhisab.ui.dashboard;

import com.akram.babsharhisab.core.security.SecureConfigStore;
import com.akram.babsharhisab.data.worker.BackupWorkScheduler;
import com.akram.babsharhisab.domain.repository.BackupSyncRepository;
import com.akram.babsharhisab.domain.repository.LedgerRepository;
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
public final class DashboardViewModel_Factory implements Factory<DashboardViewModel> {
  private final Provider<LedgerRepository> ledgerRepositoryProvider;

  private final Provider<BackupSyncRepository> backupSyncRepositoryProvider;

  private final Provider<SecureConfigStore> secureConfigStoreProvider;

  private final Provider<BackupWorkScheduler> backupWorkSchedulerProvider;

  public DashboardViewModel_Factory(Provider<LedgerRepository> ledgerRepositoryProvider,
      Provider<BackupSyncRepository> backupSyncRepositoryProvider,
      Provider<SecureConfigStore> secureConfigStoreProvider,
      Provider<BackupWorkScheduler> backupWorkSchedulerProvider) {
    this.ledgerRepositoryProvider = ledgerRepositoryProvider;
    this.backupSyncRepositoryProvider = backupSyncRepositoryProvider;
    this.secureConfigStoreProvider = secureConfigStoreProvider;
    this.backupWorkSchedulerProvider = backupWorkSchedulerProvider;
  }

  @Override
  public DashboardViewModel get() {
    return newInstance(ledgerRepositoryProvider.get(), backupSyncRepositoryProvider.get(), secureConfigStoreProvider.get(), backupWorkSchedulerProvider.get());
  }

  public static DashboardViewModel_Factory create(
      Provider<LedgerRepository> ledgerRepositoryProvider,
      Provider<BackupSyncRepository> backupSyncRepositoryProvider,
      Provider<SecureConfigStore> secureConfigStoreProvider,
      Provider<BackupWorkScheduler> backupWorkSchedulerProvider) {
    return new DashboardViewModel_Factory(ledgerRepositoryProvider, backupSyncRepositoryProvider, secureConfigStoreProvider, backupWorkSchedulerProvider);
  }

  public static DashboardViewModel newInstance(LedgerRepository ledgerRepository,
      BackupSyncRepository backupSyncRepository, SecureConfigStore secureConfigStore,
      BackupWorkScheduler backupWorkScheduler) {
    return new DashboardViewModel(ledgerRepository, backupSyncRepository, secureConfigStore, backupWorkScheduler);
  }
}
