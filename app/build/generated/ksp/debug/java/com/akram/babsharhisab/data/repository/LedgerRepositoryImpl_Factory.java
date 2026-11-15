package com.akram.babsharhisab.data.repository;

import com.akram.babsharhisab.data.local.AppDatabase;
import com.akram.babsharhisab.data.local.dao.AssetDao;
import com.akram.babsharhisab.data.local.dao.CategoryDao;
import com.akram.babsharhisab.data.local.dao.ShopkeeperDao;
import com.akram.babsharhisab.data.local.dao.TransactionDao;
import com.akram.babsharhisab.data.local.dao.VendorDao;
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
public final class LedgerRepositoryImpl_Factory implements Factory<LedgerRepositoryImpl> {
  private final Provider<AppDatabase> dbProvider;

  private final Provider<CategoryDao> categoryDaoProvider;

  private final Provider<VendorDao> vendorDaoProvider;

  private final Provider<ShopkeeperDao> shopkeeperDaoProvider;

  private final Provider<AssetDao> assetDaoProvider;

  private final Provider<TransactionDao> transactionDaoProvider;

  public LedgerRepositoryImpl_Factory(Provider<AppDatabase> dbProvider,
      Provider<CategoryDao> categoryDaoProvider, Provider<VendorDao> vendorDaoProvider,
      Provider<ShopkeeperDao> shopkeeperDaoProvider, Provider<AssetDao> assetDaoProvider,
      Provider<TransactionDao> transactionDaoProvider) {
    this.dbProvider = dbProvider;
    this.categoryDaoProvider = categoryDaoProvider;
    this.vendorDaoProvider = vendorDaoProvider;
    this.shopkeeperDaoProvider = shopkeeperDaoProvider;
    this.assetDaoProvider = assetDaoProvider;
    this.transactionDaoProvider = transactionDaoProvider;
  }

  @Override
  public LedgerRepositoryImpl get() {
    return newInstance(dbProvider.get(), categoryDaoProvider.get(), vendorDaoProvider.get(), shopkeeperDaoProvider.get(), assetDaoProvider.get(), transactionDaoProvider.get());
  }

  public static LedgerRepositoryImpl_Factory create(Provider<AppDatabase> dbProvider,
      Provider<CategoryDao> categoryDaoProvider, Provider<VendorDao> vendorDaoProvider,
      Provider<ShopkeeperDao> shopkeeperDaoProvider, Provider<AssetDao> assetDaoProvider,
      Provider<TransactionDao> transactionDaoProvider) {
    return new LedgerRepositoryImpl_Factory(dbProvider, categoryDaoProvider, vendorDaoProvider, shopkeeperDaoProvider, assetDaoProvider, transactionDaoProvider);
  }

  public static LedgerRepositoryImpl newInstance(AppDatabase db, CategoryDao categoryDao,
      VendorDao vendorDao, ShopkeeperDao shopkeeperDao, AssetDao assetDao,
      TransactionDao transactionDao) {
    return new LedgerRepositoryImpl(db, categoryDao, vendorDao, shopkeeperDao, assetDao, transactionDao);
  }
}
