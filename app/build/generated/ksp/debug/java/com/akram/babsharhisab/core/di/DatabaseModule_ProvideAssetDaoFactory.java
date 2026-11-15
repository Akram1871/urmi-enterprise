package com.akram.babsharhisab.core.di;

import com.akram.babsharhisab.data.local.AppDatabase;
import com.akram.babsharhisab.data.local.dao.AssetDao;
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
public final class DatabaseModule_ProvideAssetDaoFactory implements Factory<AssetDao> {
  private final Provider<AppDatabase> dbProvider;

  public DatabaseModule_ProvideAssetDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public AssetDao get() {
    return provideAssetDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideAssetDaoFactory create(Provider<AppDatabase> dbProvider) {
    return new DatabaseModule_ProvideAssetDaoFactory(dbProvider);
  }

  public static AssetDao provideAssetDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideAssetDao(db));
  }
}
