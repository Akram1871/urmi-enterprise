package com.akram.babsharhisab.ui.baki;

import com.akram.babsharhisab.data.worker.BackupWorkScheduler;
import com.akram.babsharhisab.domain.repository.BakiRepository;
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
public final class BakiViewModel_Factory implements Factory<BakiViewModel> {
  private final Provider<BakiRepository> bakiRepositoryProvider;

  private final Provider<LedgerRepository> ledgerRepositoryProvider;

  private final Provider<BackupWorkScheduler> backupWorkSchedulerProvider;

  public BakiViewModel_Factory(Provider<BakiRepository> bakiRepositoryProvider,
      Provider<LedgerRepository> ledgerRepositoryProvider,
      Provider<BackupWorkScheduler> backupWorkSchedulerProvider) {
    this.bakiRepositoryProvider = bakiRepositoryProvider;
    this.ledgerRepositoryProvider = ledgerRepositoryProvider;
    this.backupWorkSchedulerProvider = backupWorkSchedulerProvider;
  }

  @Override
  public BakiViewModel get() {
    return newInstance(bakiRepositoryProvider.get(), ledgerRepositoryProvider.get(), backupWorkSchedulerProvider.get());
  }

  public static BakiViewModel_Factory create(Provider<BakiRepository> bakiRepositoryProvider,
      Provider<LedgerRepository> ledgerRepositoryProvider,
      Provider<BackupWorkScheduler> backupWorkSchedulerProvider) {
    return new BakiViewModel_Factory(bakiRepositoryProvider, ledgerRepositoryProvider, backupWorkSchedulerProvider);
  }

  public static BakiViewModel newInstance(BakiRepository bakiRepository,
      LedgerRepository ledgerRepository, BackupWorkScheduler backupWorkScheduler) {
    return new BakiViewModel(bakiRepository, ledgerRepository, backupWorkScheduler);
  }
}
