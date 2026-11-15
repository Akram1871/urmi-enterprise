package com.akram.babsharhisab.core.di;

import com.akram.babsharhisab.data.local.AppDatabase;
import com.akram.babsharhisab.data.local.dao.SyncAuditDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class DatabaseModule_ProvideSyncAuditDaoFactory implements Factory<SyncAuditDao> {
  private final Provider<AppDatabase> dbProvider;

  public DatabaseModule_ProvideSyncAuditDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public SyncAuditDao get() {
    return provideSyncAuditDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideSyncAuditDaoFactory create(Provider<AppDatabase> dbProvider) {
    return new DatabaseModule_ProvideSyncAuditDaoFactory(dbProvider);
  }

  public static SyncAuditDao provideSyncAuditDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideSyncAuditDao(db));
  }
}
