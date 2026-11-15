package com.akram.babsharhisab.ui.transaction;

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
public final class TransactionViewModel_Factory implements Factory<TransactionViewModel> {
  private final Provider<LedgerRepository> ledgerRepositoryProvider;

  private final Provider<BakiRepository> bakiRepositoryProvider;

  private final Provider<BackupWorkScheduler> backupWorkSchedulerProvider;

  public TransactionViewModel_Factory(Provider<LedgerRepository> ledgerRepositoryProvider,
      Provider<BakiRepository> bakiRepositoryProvider,
      Provider<BackupWorkScheduler> backupWorkSchedulerProvider) {
    this.ledgerRepositoryProvider = ledgerRepositoryProvider;
    this.bakiRepositoryProvider = bakiRepositoryProvider;
    this.backupWorkSchedulerProvider = backupWorkSchedulerProvider;
  }

  @Override
  public TransactionViewModel get() {
    return newInstance(ledgerRepositoryProvider.get(), bakiRepositoryProvider.get(), backupWorkSchedulerProvider.get());
  }

  public static TransactionViewModel_Factory create(
      Provider<LedgerRepository> ledgerRepositoryProvider,
      Provider<BakiRepository> bakiRepositoryProvider,
      Provider<BackupWorkScheduler> backupWorkSchedulerProvider) {
    return new TransactionViewModel_Factory(ledgerRepositoryProvider, bakiRepositoryProvider, backupWorkSchedulerProvider);
  }

  public static TransactionViewModel newInstance(LedgerRepository ledgerRepository,
      BakiRepository bakiRepository, BackupWorkScheduler backupWorkScheduler) {
    return new TransactionViewModel(ledgerRepository, bakiRepository, backupWorkScheduler);
  }
}
