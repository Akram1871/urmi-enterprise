package com.akram.babsharhisab.ui.update;

import com.akram.babsharhisab.core.update.GithubAutoUpdater;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class AppUpdateViewModel_Factory implements Factory<AppUpdateViewModel> {
  private final Provider<GithubAutoUpdater> githubAutoUpdaterProvider;

  public AppUpdateViewModel_Factory(Provider<GithubAutoUpdater> githubAutoUpdaterProvider) {
    this.githubAutoUpdaterProvider = githubAutoUpdaterProvider;
  }

  @Override
  public AppUpdateViewModel get() {
    return newInstance(githubAutoUpdaterProvider.get());
  }

  public static AppUpdateViewModel_Factory create(
      Provider<GithubAutoUpdater> githubAutoUpdaterProvider) {
    return new AppUpdateViewModel_Factory(githubAutoUpdaterProvider);
  }

  public static AppUpdateViewModel newInstance(GithubAutoUpdater githubAutoUpdater) {
    return new AppUpdateViewModel(githubAutoUpdater);
  }
}
