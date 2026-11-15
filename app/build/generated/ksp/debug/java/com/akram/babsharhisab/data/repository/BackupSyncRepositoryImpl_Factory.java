package com.akram.babsharhisab.data.repository;

import com.akram.babsharhisab.core.security.SecureConfigStore;
import com.akram.babsharhisab.data.local.AppDatabase;
import com.akram.babsharhisab.data.local.dao.AssetDao;
import com.akram.babsharhisab.data.local.dao.CategoryDao;
import com.akram.babsharhisab.data.local.dao.ShopkeeperDao;
import com.akram.babsharhisab.data.local.dao.SyncAuditDao;
import com.akram.babsharhisab.data.local.dao.TransactionDao;
import com.akram.babsharhisab.data.local.dao.VendorDao;
import com.akram.babsharhisab.data.remote.SheetsWebhookClient;
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
public final class BackupSyncRepositoryImpl_Factory implements Factory<BackupSyncRepositoryImpl> {
  private final Provider<AppDatabase> dbProvider;

  private final Provider<TransactionDao> transactionDaoProvider;

  private final Provider<ShopkeeperDao> shopkeeperDaoProvider;

  private final Provider<VendorDao> vendorDaoProvider;

  private final Provider<AssetDao> assetDaoProvider;

  private final Provider<CategoryDao> categoryDaoProvider;

  private final Provider<SyncAuditDao> syncAuditDaoProvider;

  private final Provider<SecureConfigStore> secureConfigStoreProvider;

  private final Provider<SheetsWebhookClient> webhookClientProvider;

  public BackupSyncRepositoryImpl_Factory(Provider<AppDatabase> dbProvider,
      Provider<TransactionDao> transactionDaoProvider,
      Provider<ShopkeeperDao> shopkeeperDaoProvider, Provider<VendorDao> vendorDaoProvider,
      Provider<AssetDao> assetDaoProvider, Provider<CategoryDao> categoryDaoProvider,
      Provider<SyncAuditDao> syncAuditDaoProvider,
      Provider<SecureConfigStore> secureConfigStoreProvider,
      Provider<SheetsWebhookClient> webhookClientProvider) {
    this.dbProvider = dbProvider;
    this.transactionDaoProvider = transactionDaoProvider;
    this.shopkeeperDaoProvider = shopkeeperDaoProvider;
    this.vendorDaoProvider = vendorDaoProvider;
    this.assetDaoProvider = assetDaoProvider;
    this.categoryDaoProvider = categoryDaoProvider;
    this.syncAuditDaoProvider = syncAuditDaoProvider;
    this.secureConfigStoreProvider = secureConfigStoreProvider;
    this.webhookClientProvider = webhookClientProvider;
  }

  @Override
  public BackupSyncRepositoryImpl get() {
    return newInstance(dbProvider.get(), transactionDaoProvider.get(), shopkeeperDaoProvider.get(), vendorDaoProvider.get(), assetDaoProvider.get(), categoryDaoProvider.get(), syncAuditDaoProvider.get(), secureConfigStoreProvider.get(), webhookClientProvider.get());
  }

  public static BackupSyncRepositoryImpl_Factory create(Provider<AppDatabase> dbProvider,
      Provider<TransactionDao> transactionDaoProvider,
      Provider<ShopkeeperDao> shopkeeperDaoProvider, Provider<VendorDao> vendorDaoProvider,
      Provider<AssetDao> assetDaoProvider, Provider<CategoryDao> categoryDaoProvider,
      Provider<SyncAuditDao> syncAuditDaoProvider,
      Provider<SecureConfigStore> secureConfigStoreProvider,
      Provider<SheetsWebhookClient> webhookClientProvider) {
    return new BackupSyncRepositoryImpl_Factory(dbProvider, transactionDaoProvider, shopkeeperDaoProvider, vendorDaoProvider, assetDaoProvider, categoryDaoProvider, syncAuditDaoProvider, secureConfigStoreProvider, webhookClientProvider);
  }

  public static BackupSyncRepositoryImpl newInstance(AppDatabase db, TransactionDao transactionDao,
      ShopkeeperDao shopkeeperDao, VendorDao vendorDao, AssetDao assetDao, CategoryDao categoryDao,
      SyncAuditDao syncAuditDao, SecureConfigStore secureConfigStore,
      SheetsWebhookClient webhookClient) {
    return new BackupSyncRepositoryImpl(db, transactionDao, shopkeeperDao, vendorDao, assetDao, categoryDao, syncAuditDao, secureConfigStore, webhookClient);
  }
}
