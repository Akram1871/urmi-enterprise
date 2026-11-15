package com.akram.babsharhisab.ui.assets;

import com.akram.babsharhisab.data.worker.BackupWorkScheduler;
import com.akram.babsharhisab.domain.repository.AssetRepository;
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
public final class AssetViewModel_Factory implements Factory<AssetViewModel> {
  private final Provider<AssetRepository> assetRepositoryProvider;

  private final Provider<BackupWorkScheduler> backupWorkSchedulerProvider;

  public AssetViewModel_Factory(Provider<AssetRepository> assetRepositoryProvider,
      Provider<BackupWorkScheduler> backupWorkSchedulerProvider) {
    this.assetRepositoryProvider = assetRepositoryProvider;
    this.backupWorkSchedulerProvider = backupWorkSchedulerProvider;
  }

  @Override
  public AssetViewModel get() {
    return newInstance(assetRepositoryProvider.get(), backupWorkSchedulerProvider.get());
  }

  public static AssetViewModel_Factory create(Provider<AssetRepository> assetRepositoryProvider,
      Provider<BackupWorkScheduler> backupWorkSchedulerProvider) {
    return new AssetViewModel_Factory(assetRepositoryProvider, backupWorkSchedulerProvider);
  }

  public static AssetViewModel newInstance(AssetRepository assetRepository,
      BackupWorkScheduler backupWorkScheduler) {
    return new AssetViewModel(assetRepository, backupWorkScheduler);
  }
}
