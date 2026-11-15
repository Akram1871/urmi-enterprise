package com.akram.babsharhisab.data.worker;

import android.content.Context;
import androidx.work.WorkerParameters;
import dagger.internal.DaggerGenerated;
import dagger.internal.InstanceFactory;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class GoogleSheetsBackupWorker_AssistedFactory_Impl implements GoogleSheetsBackupWorker_AssistedFactory {
  private final GoogleSheetsBackupWorker_Factory delegateFactory;

  GoogleSheetsBackupWorker_AssistedFactory_Impl(GoogleSheetsBackupWorker_Factory delegateFactory) {
    this.delegateFactory = delegateFactory;
  }

  @Override
  public GoogleSheetsBackupWorker create(Context p0, WorkerParameters p1) {
    return delegateFactory.get(p0, p1);
  }

  public static Provider<GoogleSheetsBackupWorker_AssistedFactory> create(
      GoogleSheetsBackupWorker_Factory delegateFactory) {
    return InstanceFactory.create(new GoogleSheetsBackupWorker_AssistedFactory_Impl(delegateFactory));
  }

  public static dagger.internal.Provider<GoogleSheetsBackupWorker_AssistedFactory> createFactoryProvider(
      GoogleSheetsBackupWorker_Factory delegateFactory) {
    return InstanceFactory.create(new GoogleSheetsBackupWorker_AssistedFactory_Impl(delegateFactory));
  }
}
