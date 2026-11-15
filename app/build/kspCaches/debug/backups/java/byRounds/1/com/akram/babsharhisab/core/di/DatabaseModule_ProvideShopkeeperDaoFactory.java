package com.akram.babsharhisab.core.di;

import com.akram.babsharhisab.data.local.AppDatabase;
import com.akram.babsharhisab.data.local.dao.ShopkeeperDao;
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
public final class DatabaseModule_ProvideShopkeeperDaoFactory implements Factory<ShopkeeperDao> {
  private final Provider<AppDatabase> dbProvider;

  public DatabaseModule_ProvideShopkeeperDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public ShopkeeperDao get() {
    return provideShopkeeperDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideShopkeeperDaoFactory create(
      Provider<AppDatabase> dbProvider) {
    return new DatabaseModule_ProvideShopkeeperDaoFactory(dbProvider);
  }

  public static ShopkeeperDao provideShopkeeperDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideShopkeeperDao(db));
  }
}
