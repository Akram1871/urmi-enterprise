package com.akram.babsharhisab;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.hilt.work.HiltWorkerFactory;
import androidx.hilt.work.WorkerAssistedFactory;
import androidx.hilt.work.WorkerFactoryModule_ProvideFactoryFactory;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;
import com.akram.babsharhisab.core.di.DatabaseModule_ProvideAppDatabaseFactory;
import com.akram.babsharhisab.core.di.DatabaseModule_ProvideAssetDaoFactory;
import com.akram.babsharhisab.core.di.DatabaseModule_ProvideCategoryDaoFactory;
import com.akram.babsharhisab.core.di.DatabaseModule_ProvideShopkeeperDaoFactory;
import com.akram.babsharhisab.core.di.DatabaseModule_ProvideSyncAuditDaoFactory;
import com.akram.babsharhisab.core.di.DatabaseModule_ProvideTransactionDaoFactory;
import com.akram.babsharhisab.core.di.DatabaseModule_ProvideVendorDaoFactory;
import com.akram.babsharhisab.core.security.SecureConfigStore;
import com.akram.babsharhisab.core.update.GithubAutoUpdater;
import com.akram.babsharhisab.data.local.AppDatabase;
import com.akram.babsharhisab.data.local.dao.AssetDao;
import com.akram.babsharhisab.data.local.dao.CategoryDao;
import com.akram.babsharhisab.data.local.dao.ShopkeeperDao;
import com.akram.babsharhisab.data.local.dao.SyncAuditDao;
import com.akram.babsharhisab.data.local.dao.TransactionDao;
import com.akram.babsharhisab.data.local.dao.VendorDao;
import com.akram.babsharhisab.data.remote.SheetsWebhookClient;
import com.akram.babsharhisab.data.repository.AssetRepositoryImpl;
import com.akram.babsharhisab.data.repository.BackupSyncRepositoryImpl;
import com.akram.babsharhisab.data.repository.BakiRepositoryImpl;
import com.akram.babsharhisab.data.repository.LedgerRepositoryImpl;
import com.akram.babsharhisab.data.worker.BackupWorkScheduler;
import com.akram.babsharhisab.data.worker.GoogleSheetsBackupWorker;
import com.akram.babsharhisab.data.worker.GoogleSheetsBackupWorker_AssistedFactory;
import com.akram.babsharhisab.ui.analytics.AnalyticsViewModel;
import com.akram.babsharhisab.ui.analytics.AnalyticsViewModel_HiltModules;
import com.akram.babsharhisab.ui.assets.AssetViewModel;
import com.akram.babsharhisab.ui.assets.AssetViewModel_HiltModules;
import com.akram.babsharhisab.ui.baki.BakiViewModel;
import com.akram.babsharhisab.ui.baki.BakiViewModel_HiltModules;
import com.akram.babsharhisab.ui.dashboard.DashboardViewModel;
import com.akram.babsharhisab.ui.dashboard.DashboardViewModel_HiltModules;
import com.akram.babsharhisab.ui.settings.BackupSettingsViewModel;
import com.akram.babsharhisab.ui.settings.BackupSettingsViewModel_HiltModules;
import com.akram.babsharhisab.ui.transaction.TransactionViewModel;
import com.akram.babsharhisab.ui.transaction.TransactionViewModel_HiltModules;
import com.akram.babsharhisab.ui.update.AppUpdateViewModel;
import com.akram.babsharhisab.ui.update.AppUpdateViewModel_HiltModules;
import dagger.hilt.android.ActivityRetainedLifecycle;
import dagger.hilt.android.ViewModelLifecycle;
import dagger.hilt.android.internal.builders.ActivityComponentBuilder;
import dagger.hilt.android.internal.builders.ActivityRetainedComponentBuilder;
import dagger.hilt.android.internal.builders.FragmentComponentBuilder;
import dagger.hilt.android.internal.builders.ServiceComponentBuilder;
import dagger.hilt.android.internal.builders.ViewComponentBuilder;
import dagger.hilt.android.internal.builders.ViewModelComponentBuilder;
import dagger.hilt.android.internal.builders.ViewWithFragmentComponentBuilder;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories_InternalFactoryFactory_Factory;
import dagger.hilt.android.internal.managers.ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory;
import dagger.hilt.android.internal.managers.SavedStateHandleHolder;
import dagger.hilt.android.internal.modules.ApplicationContextModule;
import dagger.hilt.android.internal.modules.ApplicationContextModule_ProvideContextFactory;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.IdentifierNameString;
import dagger.internal.KeepFieldType;
import dagger.internal.LazyClassKeyMap;
import dagger.internal.MapBuilder;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.SingleCheck;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

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
public final class DaggerBabsharHisabApp_HiltComponents_SingletonC {
  private DaggerBabsharHisabApp_HiltComponents_SingletonC() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private ApplicationContextModule applicationContextModule;

    private Builder() {
    }

    public Builder applicationContextModule(ApplicationContextModule applicationContextModule) {
      this.applicationContextModule = Preconditions.checkNotNull(applicationContextModule);
      return this;
    }

    public BabsharHisabApp_HiltComponents.SingletonC build() {
      Preconditions.checkBuilderRequirement(applicationContextModule, ApplicationContextModule.class);
      return new SingletonCImpl(applicationContextModule);
    }
  }

  private static final class ActivityRetainedCBuilder implements BabsharHisabApp_HiltComponents.ActivityRetainedC.Builder {
    private final SingletonCImpl singletonCImpl;

    private SavedStateHandleHolder savedStateHandleHolder;

    private ActivityRetainedCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ActivityRetainedCBuilder savedStateHandleHolder(
        SavedStateHandleHolder savedStateHandleHolder) {
      this.savedStateHandleHolder = Preconditions.checkNotNull(savedStateHandleHolder);
      return this;
    }

    @Override
    public BabsharHisabApp_HiltComponents.ActivityRetainedC build() {
      Preconditions.checkBuilderRequirement(savedStateHandleHolder, SavedStateHandleHolder.class);
      return new ActivityRetainedCImpl(singletonCImpl, savedStateHandleHolder);
    }
  }

  private static final class ActivityCBuilder implements BabsharHisabApp_HiltComponents.ActivityC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private Activity activity;

    private ActivityCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ActivityCBuilder activity(Activity activity) {
      this.activity = Preconditions.checkNotNull(activity);
      return this;
    }

    @Override
    public BabsharHisabApp_HiltComponents.ActivityC build() {
      Preconditions.checkBuilderRequirement(activity, Activity.class);
      return new ActivityCImpl(singletonCImpl, activityRetainedCImpl, activity);
    }
  }

  private static final class FragmentCBuilder implements BabsharHisabApp_HiltComponents.FragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private Fragment fragment;

    private FragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public FragmentCBuilder fragment(Fragment fragment) {
      this.fragment = Preconditions.checkNotNull(fragment);
      return this;
    }

    @Override
    public BabsharHisabApp_HiltComponents.FragmentC build() {
      Preconditions.checkBuilderRequirement(fragment, Fragment.class);
      return new FragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragment);
    }
  }

  private static final class ViewWithFragmentCBuilder implements BabsharHisabApp_HiltComponents.ViewWithFragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private View view;

    private ViewWithFragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;
    }

    @Override
    public ViewWithFragmentCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public BabsharHisabApp_HiltComponents.ViewWithFragmentC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewWithFragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl, view);
    }
  }

  private static final class ViewCBuilder implements BabsharHisabApp_HiltComponents.ViewC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private View view;

    private ViewCBuilder(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public ViewCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public BabsharHisabApp_HiltComponents.ViewC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, view);
    }
  }

  private static final class ViewModelCBuilder implements BabsharHisabApp_HiltComponents.ViewModelC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private SavedStateHandle savedStateHandle;

    private ViewModelLifecycle viewModelLifecycle;

    private ViewModelCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ViewModelCBuilder savedStateHandle(SavedStateHandle handle) {
      this.savedStateHandle = Preconditions.checkNotNull(handle);
      return this;
    }

    @Override
    public ViewModelCBuilder viewModelLifecycle(ViewModelLifecycle viewModelLifecycle) {
      this.viewModelLifecycle = Preconditions.checkNotNull(viewModelLifecycle);
      return this;
    }

    @Override
    public BabsharHisabApp_HiltComponents.ViewModelC build() {
      Preconditions.checkBuilderRequirement(savedStateHandle, SavedStateHandle.class);
      Preconditions.checkBuilderRequirement(viewModelLifecycle, ViewModelLifecycle.class);
      return new ViewModelCImpl(singletonCImpl, activityRetainedCImpl, savedStateHandle, viewModelLifecycle);
    }
  }

  private static final class ServiceCBuilder implements BabsharHisabApp_HiltComponents.ServiceC.Builder {
    private final SingletonCImpl singletonCImpl;

    private Service service;

    private ServiceCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ServiceCBuilder service(Service service) {
      this.service = Preconditions.checkNotNull(service);
      return this;
    }

    @Override
    public BabsharHisabApp_HiltComponents.ServiceC build() {
      Preconditions.checkBuilderRequirement(service, Service.class);
      return new ServiceCImpl(singletonCImpl, service);
    }
  }

  private static final class ViewWithFragmentCImpl extends BabsharHisabApp_HiltComponents.ViewWithFragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private final ViewWithFragmentCImpl viewWithFragmentCImpl = this;

    private ViewWithFragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;


    }
  }

  private static final class FragmentCImpl extends BabsharHisabApp_HiltComponents.FragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl = this;

    private FragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        Fragment fragmentParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return activityCImpl.getHiltInternalFactoryFactory();
    }

    @Override
    public ViewWithFragmentComponentBuilder viewWithFragmentComponentBuilder() {
      return new ViewWithFragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl);
    }
  }

  private static final class ViewCImpl extends BabsharHisabApp_HiltComponents.ViewC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final ViewCImpl viewCImpl = this;

    private ViewCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }
  }

  private static final class ActivityCImpl extends BabsharHisabApp_HiltComponents.ActivityC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl = this;

    private ActivityCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, Activity activityParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;


    }

    @Override
    public void injectMainActivity(MainActivity mainActivity) {
      injectMainActivity2(mainActivity);
    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return DefaultViewModelFactories_InternalFactoryFactory_Factory.newInstance(getViewModelKeys(), new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl));
    }

    @Override
    public Map<Class<?>, Boolean> getViewModelKeys() {
      return LazyClassKeyMap.<Boolean>of(MapBuilder.<String, Boolean>newMapBuilder(7).put(LazyClassKeyProvider.com_akram_babsharhisab_ui_analytics_AnalyticsViewModel, AnalyticsViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_akram_babsharhisab_ui_update_AppUpdateViewModel, AppUpdateViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_akram_babsharhisab_ui_assets_AssetViewModel, AssetViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_akram_babsharhisab_ui_settings_BackupSettingsViewModel, BackupSettingsViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_akram_babsharhisab_ui_baki_BakiViewModel, BakiViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_akram_babsharhisab_ui_dashboard_DashboardViewModel, DashboardViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_akram_babsharhisab_ui_transaction_TransactionViewModel, TransactionViewModel_HiltModules.KeyModule.provide()).build());
    }

    @Override
    public ViewModelComponentBuilder getViewModelComponentBuilder() {
      return new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public FragmentComponentBuilder fragmentComponentBuilder() {
      return new FragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @Override
    public ViewComponentBuilder viewComponentBuilder() {
      return new ViewCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    private MainActivity injectMainActivity2(MainActivity instance) {
      MainActivity_MembersInjector.injectSecureConfigStore(instance, singletonCImpl.secureConfigStoreProvider.get());
      return instance;
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String com_akram_babsharhisab_ui_dashboard_DashboardViewModel = "com.akram.babsharhisab.ui.dashboard.DashboardViewModel";

      static String com_akram_babsharhisab_ui_analytics_AnalyticsViewModel = "com.akram.babsharhisab.ui.analytics.AnalyticsViewModel";

      static String com_akram_babsharhisab_ui_assets_AssetViewModel = "com.akram.babsharhisab.ui.assets.AssetViewModel";

      static String com_akram_babsharhisab_ui_settings_BackupSettingsViewModel = "com.akram.babsharhisab.ui.settings.BackupSettingsViewModel";

      static String com_akram_babsharhisab_ui_update_AppUpdateViewModel = "com.akram.babsharhisab.ui.update.AppUpdateViewModel";

      static String com_akram_babsharhisab_ui_baki_BakiViewModel = "com.akram.babsharhisab.ui.baki.BakiViewModel";

      static String com_akram_babsharhisab_ui_transaction_TransactionViewModel = "com.akram.babsharhisab.ui.transaction.TransactionViewModel";

      @KeepFieldType
      DashboardViewModel com_akram_babsharhisab_ui_dashboard_DashboardViewModel2;

      @KeepFieldType
      AnalyticsViewModel com_akram_babsharhisab_ui_analytics_AnalyticsViewModel2;

      @KeepFieldType
      AssetViewModel com_akram_babsharhisab_ui_assets_AssetViewModel2;

      @KeepFieldType
      BackupSettingsViewModel com_akram_babsharhisab_ui_settings_BackupSettingsViewModel2;

      @KeepFieldType
      AppUpdateViewModel com_akram_babsharhisab_ui_update_AppUpdateViewModel2;

      @KeepFieldType
      BakiViewModel com_akram_babsharhisab_ui_baki_BakiViewModel2;

      @KeepFieldType
      TransactionViewModel com_akram_babsharhisab_ui_transaction_TransactionViewModel2;
    }
  }

  private static final class ViewModelCImpl extends BabsharHisabApp_HiltComponents.ViewModelC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ViewModelCImpl viewModelCImpl = this;

    private Provider<AnalyticsViewModel> analyticsViewModelProvider;

    private Provider<AppUpdateViewModel> appUpdateViewModelProvider;

    private Provider<AssetViewModel> assetViewModelProvider;

    private Provider<BackupSettingsViewModel> backupSettingsViewModelProvider;

    private Provider<BakiViewModel> bakiViewModelProvider;

    private Provider<DashboardViewModel> dashboardViewModelProvider;

    private Provider<TransactionViewModel> transactionViewModelProvider;

    private ViewModelCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, SavedStateHandle savedStateHandleParam,
        ViewModelLifecycle viewModelLifecycleParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;

      initialize(savedStateHandleParam, viewModelLifecycleParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandle savedStateHandleParam,
        final ViewModelLifecycle viewModelLifecycleParam) {
      this.analyticsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 0);
      this.appUpdateViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 1);
      this.assetViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 2);
      this.backupSettingsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 3);
      this.bakiViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 4);
      this.dashboardViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 5);
      this.transactionViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 6);
    }

    @Override
    public Map<Class<?>, javax.inject.Provider<ViewModel>> getHiltViewModelMap() {
      return LazyClassKeyMap.<javax.inject.Provider<ViewModel>>of(MapBuilder.<String, javax.inject.Provider<ViewModel>>newMapBuilder(7).put(LazyClassKeyProvider.com_akram_babsharhisab_ui_analytics_AnalyticsViewModel, ((Provider) analyticsViewModelProvider)).put(LazyClassKeyProvider.com_akram_babsharhisab_ui_update_AppUpdateViewModel, ((Provider) appUpdateViewModelProvider)).put(LazyClassKeyProvider.com_akram_babsharhisab_ui_assets_AssetViewModel, ((Provider) assetViewModelProvider)).put(LazyClassKeyProvider.com_akram_babsharhisab_ui_settings_BackupSettingsViewModel, ((Provider) backupSettingsViewModelProvider)).put(LazyClassKeyProvider.com_akram_babsharhisab_ui_baki_BakiViewModel, ((Provider) bakiViewModelProvider)).put(LazyClassKeyProvider.com_akram_babsharhisab_ui_dashboard_DashboardViewModel, ((Provider) dashboardViewModelProvider)).put(LazyClassKeyProvider.com_akram_babsharhisab_ui_transaction_TransactionViewModel, ((Provider) transactionViewModelProvider)).build());
    }

    @Override
    public Map<Class<?>, Object> getHiltViewModelAssistedMap() {
      return Collections.<Class<?>, Object>emptyMap();
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String com_akram_babsharhisab_ui_baki_BakiViewModel = "com.akram.babsharhisab.ui.baki.BakiViewModel";

      static String com_akram_babsharhisab_ui_assets_AssetViewModel = "com.akram.babsharhisab.ui.assets.AssetViewModel";

      static String com_akram_babsharhisab_ui_dashboard_DashboardViewModel = "com.akram.babsharhisab.ui.dashboard.DashboardViewModel";

      static String com_akram_babsharhisab_ui_analytics_AnalyticsViewModel = "com.akram.babsharhisab.ui.analytics.AnalyticsViewModel";

      static String com_akram_babsharhisab_ui_settings_BackupSettingsViewModel = "com.akram.babsharhisab.ui.settings.BackupSettingsViewModel";

      static String com_akram_babsharhisab_ui_transaction_TransactionViewModel = "com.akram.babsharhisab.ui.transaction.TransactionViewModel";

      static String com_akram_babsharhisab_ui_update_AppUpdateViewModel = "com.akram.babsharhisab.ui.update.AppUpdateViewModel";

      @KeepFieldType
      BakiViewModel com_akram_babsharhisab_ui_baki_BakiViewModel2;

      @KeepFieldType
      AssetViewModel com_akram_babsharhisab_ui_assets_AssetViewModel2;

      @KeepFieldType
      DashboardViewModel com_akram_babsharhisab_ui_dashboard_DashboardViewModel2;

      @KeepFieldType
      AnalyticsViewModel com_akram_babsharhisab_ui_analytics_AnalyticsViewModel2;

      @KeepFieldType
      BackupSettingsViewModel com_akram_babsharhisab_ui_settings_BackupSettingsViewModel2;

      @KeepFieldType
      TransactionViewModel com_akram_babsharhisab_ui_transaction_TransactionViewModel2;

      @KeepFieldType
      AppUpdateViewModel com_akram_babsharhisab_ui_update_AppUpdateViewModel2;
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final ViewModelCImpl viewModelCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          ViewModelCImpl viewModelCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.viewModelCImpl = viewModelCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.akram.babsharhisab.ui.analytics.AnalyticsViewModel 
          return (T) new AnalyticsViewModel(singletonCImpl.ledgerRepositoryImplProvider.get());

          case 1: // com.akram.babsharhisab.ui.update.AppUpdateViewModel 
          return (T) new AppUpdateViewModel(singletonCImpl.githubAutoUpdaterProvider.get());

          case 2: // com.akram.babsharhisab.ui.assets.AssetViewModel 
          return (T) new AssetViewModel(singletonCImpl.assetRepositoryImplProvider.get(), singletonCImpl.backupWorkSchedulerProvider.get());

          case 3: // com.akram.babsharhisab.ui.settings.BackupSettingsViewModel 
          return (T) new BackupSettingsViewModel(singletonCImpl.backupSyncRepositoryImplProvider.get(), singletonCImpl.secureConfigStoreProvider.get(), singletonCImpl.backupWorkSchedulerProvider.get());

          case 4: // com.akram.babsharhisab.ui.baki.BakiViewModel 
          return (T) new BakiViewModel(singletonCImpl.bakiRepositoryImplProvider.get(), singletonCImpl.ledgerRepositoryImplProvider.get(), singletonCImpl.backupWorkSchedulerProvider.get());

          case 5: // com.akram.babsharhisab.ui.dashboard.DashboardViewModel 
          return (T) new DashboardViewModel(singletonCImpl.ledgerRepositoryImplProvider.get(), singletonCImpl.backupSyncRepositoryImplProvider.get(), singletonCImpl.secureConfigStoreProvider.get(), singletonCImpl.backupWorkSchedulerProvider.get());

          case 6: // com.akram.babsharhisab.ui.transaction.TransactionViewModel 
          return (T) new TransactionViewModel(singletonCImpl.ledgerRepositoryImplProvider.get(), singletonCImpl.bakiRepositoryImplProvider.get(), singletonCImpl.backupWorkSchedulerProvider.get());

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ActivityRetainedCImpl extends BabsharHisabApp_HiltComponents.ActivityRetainedC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl = this;

    private Provider<ActivityRetainedLifecycle> provideActivityRetainedLifecycleProvider;

    private ActivityRetainedCImpl(SingletonCImpl singletonCImpl,
        SavedStateHandleHolder savedStateHandleHolderParam) {
      this.singletonCImpl = singletonCImpl;

      initialize(savedStateHandleHolderParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandleHolder savedStateHandleHolderParam) {
      this.provideActivityRetainedLifecycleProvider = DoubleCheck.provider(new SwitchingProvider<ActivityRetainedLifecycle>(singletonCImpl, activityRetainedCImpl, 0));
    }

    @Override
    public ActivityComponentBuilder activityComponentBuilder() {
      return new ActivityCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public ActivityRetainedLifecycle getActivityRetainedLifecycle() {
      return provideActivityRetainedLifecycleProvider.get();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // dagger.hilt.android.ActivityRetainedLifecycle 
          return (T) ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory.provideActivityRetainedLifecycle();

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ServiceCImpl extends BabsharHisabApp_HiltComponents.ServiceC {
    private final SingletonCImpl singletonCImpl;

    private final ServiceCImpl serviceCImpl = this;

    private ServiceCImpl(SingletonCImpl singletonCImpl, Service serviceParam) {
      this.singletonCImpl = singletonCImpl;


    }
  }

  private static final class SingletonCImpl extends BabsharHisabApp_HiltComponents.SingletonC {
    private final ApplicationContextModule applicationContextModule;

    private final SingletonCImpl singletonCImpl = this;

    private Provider<AppDatabase> provideAppDatabaseProvider;

    private Provider<SecureConfigStore> secureConfigStoreProvider;

    private Provider<SheetsWebhookClient> sheetsWebhookClientProvider;

    private Provider<BackupSyncRepositoryImpl> backupSyncRepositoryImplProvider;

    private Provider<GoogleSheetsBackupWorker_AssistedFactory> googleSheetsBackupWorker_AssistedFactoryProvider;

    private Provider<BackupWorkScheduler> backupWorkSchedulerProvider;

    private Provider<LedgerRepositoryImpl> ledgerRepositoryImplProvider;

    private Provider<GithubAutoUpdater> githubAutoUpdaterProvider;

    private Provider<AssetRepositoryImpl> assetRepositoryImplProvider;

    private Provider<BakiRepositoryImpl> bakiRepositoryImplProvider;

    private SingletonCImpl(ApplicationContextModule applicationContextModuleParam) {
      this.applicationContextModule = applicationContextModuleParam;
      initialize(applicationContextModuleParam);

    }

    private TransactionDao transactionDao() {
      return DatabaseModule_ProvideTransactionDaoFactory.provideTransactionDao(provideAppDatabaseProvider.get());
    }

    private ShopkeeperDao shopkeeperDao() {
      return DatabaseModule_ProvideShopkeeperDaoFactory.provideShopkeeperDao(provideAppDatabaseProvider.get());
    }

    private VendorDao vendorDao() {
      return DatabaseModule_ProvideVendorDaoFactory.provideVendorDao(provideAppDatabaseProvider.get());
    }

    private AssetDao assetDao() {
      return DatabaseModule_ProvideAssetDaoFactory.provideAssetDao(provideAppDatabaseProvider.get());
    }

    private CategoryDao categoryDao() {
      return DatabaseModule_ProvideCategoryDaoFactory.provideCategoryDao(provideAppDatabaseProvider.get());
    }

    private SyncAuditDao syncAuditDao() {
      return DatabaseModule_ProvideSyncAuditDaoFactory.provideSyncAuditDao(provideAppDatabaseProvider.get());
    }

    private Map<String, javax.inject.Provider<WorkerAssistedFactory<? extends ListenableWorker>>> mapOfStringAndProviderOfWorkerAssistedFactoryOf(
        ) {
      return Collections.<String, javax.inject.Provider<WorkerAssistedFactory<? extends ListenableWorker>>>singletonMap("com.akram.babsharhisab.data.worker.GoogleSheetsBackupWorker", ((Provider) googleSheetsBackupWorker_AssistedFactoryProvider));
    }

    private HiltWorkerFactory hiltWorkerFactory() {
      return WorkerFactoryModule_ProvideFactoryFactory.provideFactory(mapOfStringAndProviderOfWorkerAssistedFactoryOf());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final ApplicationContextModule applicationContextModuleParam) {
      this.provideAppDatabaseProvider = DoubleCheck.provider(new SwitchingProvider<AppDatabase>(singletonCImpl, 2));
      this.secureConfigStoreProvider = DoubleCheck.provider(new SwitchingProvider<SecureConfigStore>(singletonCImpl, 3));
      this.sheetsWebhookClientProvider = DoubleCheck.provider(new SwitchingProvider<SheetsWebhookClient>(singletonCImpl, 4));
      this.backupSyncRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<BackupSyncRepositoryImpl>(singletonCImpl, 1));
      this.googleSheetsBackupWorker_AssistedFactoryProvider = SingleCheck.provider(new SwitchingProvider<GoogleSheetsBackupWorker_AssistedFactory>(singletonCImpl, 0));
      this.backupWorkSchedulerProvider = DoubleCheck.provider(new SwitchingProvider<BackupWorkScheduler>(singletonCImpl, 5));
      this.ledgerRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<LedgerRepositoryImpl>(singletonCImpl, 6));
      this.githubAutoUpdaterProvider = DoubleCheck.provider(new SwitchingProvider<GithubAutoUpdater>(singletonCImpl, 7));
      this.assetRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<AssetRepositoryImpl>(singletonCImpl, 8));
      this.bakiRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<BakiRepositoryImpl>(singletonCImpl, 9));
    }

    @Override
    public void injectBabsharHisabApp(BabsharHisabApp babsharHisabApp) {
      injectBabsharHisabApp2(babsharHisabApp);
    }

    @Override
    public Set<Boolean> getDisableFragmentGetContextFix() {
      return Collections.<Boolean>emptySet();
    }

    @Override
    public ActivityRetainedComponentBuilder retainedComponentBuilder() {
      return new ActivityRetainedCBuilder(singletonCImpl);
    }

    @Override
    public ServiceComponentBuilder serviceComponentBuilder() {
      return new ServiceCBuilder(singletonCImpl);
    }

    private BabsharHisabApp injectBabsharHisabApp2(BabsharHisabApp instance) {
      BabsharHisabApp_MembersInjector.injectWorkerFactory(instance, hiltWorkerFactory());
      BabsharHisabApp_MembersInjector.injectBackupWorkScheduler(instance, backupWorkSchedulerProvider.get());
      return instance;
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.akram.babsharhisab.data.worker.GoogleSheetsBackupWorker_AssistedFactory 
          return (T) new GoogleSheetsBackupWorker_AssistedFactory() {
            @Override
            public GoogleSheetsBackupWorker create(Context appContext,
                WorkerParameters workerParams) {
              return new GoogleSheetsBackupWorker(appContext, workerParams, singletonCImpl.backupSyncRepositoryImplProvider.get(), singletonCImpl.secureConfigStoreProvider.get());
            }
          };

          case 1: // com.akram.babsharhisab.data.repository.BackupSyncRepositoryImpl 
          return (T) new BackupSyncRepositoryImpl(singletonCImpl.provideAppDatabaseProvider.get(), singletonCImpl.transactionDao(), singletonCImpl.shopkeeperDao(), singletonCImpl.vendorDao(), singletonCImpl.assetDao(), singletonCImpl.categoryDao(), singletonCImpl.syncAuditDao(), singletonCImpl.secureConfigStoreProvider.get(), singletonCImpl.sheetsWebhookClientProvider.get());

          case 2: // com.akram.babsharhisab.data.local.AppDatabase 
          return (T) DatabaseModule_ProvideAppDatabaseFactory.provideAppDatabase(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 3: // com.akram.babsharhisab.core.security.SecureConfigStore 
          return (T) new SecureConfigStore(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 4: // com.akram.babsharhisab.data.remote.SheetsWebhookClient 
          return (T) new SheetsWebhookClient();

          case 5: // com.akram.babsharhisab.data.worker.BackupWorkScheduler 
          return (T) new BackupWorkScheduler(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 6: // com.akram.babsharhisab.data.repository.LedgerRepositoryImpl 
          return (T) new LedgerRepositoryImpl(singletonCImpl.provideAppDatabaseProvider.get(), singletonCImpl.categoryDao(), singletonCImpl.vendorDao(), singletonCImpl.shopkeeperDao(), singletonCImpl.assetDao(), singletonCImpl.transactionDao());

          case 7: // com.akram.babsharhisab.core.update.GithubAutoUpdater 
          return (T) new GithubAutoUpdater(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 8: // com.akram.babsharhisab.data.repository.AssetRepositoryImpl 
          return (T) new AssetRepositoryImpl(singletonCImpl.provideAppDatabaseProvider.get(), singletonCImpl.assetDao(), singletonCImpl.transactionDao());

          case 9: // com.akram.babsharhisab.data.repository.BakiRepositoryImpl 
          return (T) new BakiRepositoryImpl(singletonCImpl.provideAppDatabaseProvider.get(), singletonCImpl.shopkeeperDao(), singletonCImpl.transactionDao(), singletonCImpl.categoryDao());

          default: throw new AssertionError(id);
        }
      }
    }
  }
}
