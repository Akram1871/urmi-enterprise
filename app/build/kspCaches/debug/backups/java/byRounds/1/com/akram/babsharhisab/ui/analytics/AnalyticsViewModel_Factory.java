package com.akram.babsharhisab.ui.analytics;

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
public final class AnalyticsViewModel_Factory implements Factory<AnalyticsViewModel> {
  private final Provider<LedgerRepository> ledgerRepositoryProvider;

  public AnalyticsViewModel_Factory(Provider<LedgerRepository> ledgerRepositoryProvider) {
    this.ledgerRepositoryProvider = ledgerRepositoryProvider;
  }

  @Override
  public AnalyticsViewModel get() {
    return newInstance(ledgerRepositoryProvider.get());
  }

  public static AnalyticsViewModel_Factory create(
      Provider<LedgerRepository> ledgerRepositoryProvider) {
    return new AnalyticsViewModel_Factory(ledgerRepositoryProvider);
  }

  public static AnalyticsViewModel newInstance(LedgerRepository ledgerRepository) {
    return new AnalyticsViewModel(ledgerRepository);
  }
}
