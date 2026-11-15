package com.akram.babsharhisab;

import androidx.hilt.work.HiltWorkerFactory;
import com.akram.babsharhisab.data.worker.BackupWorkScheduler;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class BabsharHisabApp_MembersInjector implements MembersInjector<BabsharHisabApp> {
  private final Provider<HiltWorkerFactory> workerFactoryProvider;

  private final Provider<BackupWorkScheduler> backupWorkSchedulerProvider;

  public BabsharHisabApp_MembersInjector(Provider<HiltWorkerFactory> workerFactoryProvider,
      Provider<BackupWorkScheduler> backupWorkSchedulerProvider) {
    this.workerFactoryProvider = workerFactoryProvider;
    this.backupWorkSchedulerProvider = backupWorkSchedulerProvider;
  }

  public static MembersInjector<BabsharHisabApp> create(
      Provider<HiltWorkerFactory> workerFactoryProvider,
      Provider<BackupWorkScheduler> backupWorkSchedulerProvider) {
    return new BabsharHisabApp_MembersInjector(workerFactoryProvider, backupWorkSchedulerProvider);
  }

  @Override
  public void injectMembers(BabsharHisabApp instance) {
    injectWorkerFactory(instance, workerFactoryProvider.get());
    injectBackupWorkScheduler(instance, backupWorkSchedulerProvider.get());
  }

  @InjectedFieldSignature("com.akram.babsharhisab.BabsharHisabApp.workerFactory")
  public static void injectWorkerFactory(BabsharHisabApp instance,
      HiltWorkerFactory workerFactory) {
    instance.workerFactory = workerFactory;
  }

  @InjectedFieldSignature("com.akram.babsharhisab.BabsharHisabApp.backupWorkScheduler")
  public static void injectBackupWorkScheduler(BabsharHisabApp instance,
      BackupWorkScheduler backupWorkScheduler) {
    instance.backupWorkScheduler = backupWorkScheduler;
  }
}
