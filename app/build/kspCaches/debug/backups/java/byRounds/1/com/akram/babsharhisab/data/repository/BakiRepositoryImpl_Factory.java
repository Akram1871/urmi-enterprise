package com.akram.babsharhisab.data.repository;

import com.akram.babsharhisab.data.local.AppDatabase;
import com.akram.babsharhisab.data.local.dao.CategoryDao;
import com.akram.babsharhisab.data.local.dao.ShopkeeperDao;
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
public final class BakiRepositoryImpl_Factory implements Factory<BakiRepositoryImpl> {
  private final Provider<AppDatabase> dbProvider;

  private final Provider<ShopkeeperDao> shopkeeperDaoProvider;

  private final Provider<TransactionDao> transactionDaoProvider;

  private final Provider<CategoryDao> categoryDaoProvider;

  public BakiRepositoryImpl_Factory(Provider<AppDatabase> dbProvider,
      Provider<ShopkeeperDao> shopkeeperDaoProvider,
      Provider<TransactionDao> transactionDaoProvider, Provider<CategoryDao> categoryDaoProvider) {
    this.dbProvider = dbProvider;
    this.shopkeeperDaoProvider = shopkeeperDaoProvider;
    this.transactionDaoProvider = transactionDaoProvider;
    this.categoryDaoProvider = categoryDaoProvider;
  }

  @Override
  public BakiRepositoryImpl get() {
    return newInstance(dbProvider.get(), shopkeeperDaoProvider.get(), transactionDaoProvider.get(), categoryDaoProvider.get());
  }

  public static BakiRepositoryImpl_Factory create(Provider<AppDatabase> dbProvider,
      Provider<ShopkeeperDao> shopkeeperDaoProvider,
      Provider<TransactionDao> transactionDaoProvider, Provider<CategoryDao> categoryDaoProvider) {
    return new BakiRepositoryImpl_Factory(dbProvider, shopkeeperDaoProvider, transactionDaoProvider, categoryDaoProvider);
  }

  public static BakiRepositoryImpl newInstance(AppDatabase db, ShopkeeperDao shopkeeperDao,
      TransactionDao transactionDao, CategoryDao categoryDao) {
    return new BakiRepositoryImpl(db, shopkeeperDao, transactionDao, categoryDao);
  }
}
