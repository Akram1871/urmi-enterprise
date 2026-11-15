package com.akram.babsharhisab.data.repository;

import com.akram.babsharhisab.data.local.AppDatabase;
import com.akram.babsharhisab.data.local.dao.AssetDao;
import com.akram.babsharhisab.data.local.dao.TransactionDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class AssetRepositoryImpl_Factory implements Factory<AssetRepositoryImpl> {
  private final Provider<AppDatabase> dbProvider;

  private final Provider<AssetDao> assetDaoProvider;

  private final Provider<TransactionDao> transactionDaoProvider;

  public AssetRepositoryImpl_Factory(Provider<AppDatabase> dbProvider,
      Provider<AssetDao> assetDaoProvider, Provider<TransactionDao> transactionDaoProvider) {
    this.dbProvider = dbProvider;
    this.assetDaoProvider = assetDaoProvider;
    this.transactionDaoProvider = transactionDaoProvider;
  }

  @Override
  public AssetRepositoryImpl get() {
    return newInstance(dbProvider.get(), assetDaoProvider.get(), transactionDaoProvider.get());
  }

  public static AssetRepositoryImpl_Factory create(Provider<AppDatabase> dbProvider,
      Provider<AssetDao> assetDaoProvider, Provider<TransactionDao> transactionDaoProvider) {
    return new AssetRepositoryImpl_Factory(dbProvider, assetDaoProvider, transactionDaoProvider);
  }

  public static AssetRepositoryImpl newInstance(AppDatabase db, AssetDao assetDao,
      TransactionDao transactionDao) {
    return new AssetRepositoryImpl(db, assetDao, transactionDao);
  }
}
