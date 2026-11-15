package com.akram.babsharhisab.core.update;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class GithubAutoUpdater_Factory implements Factory<GithubAutoUpdater> {
  private final Provider<Context> contextProvider;

  public GithubAutoUpdater_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public GithubAutoUpdater get() {
    return newInstance(contextProvider.get());
  }

  public static GithubAutoUpdater_Factory create(Provider<Context> contextProvider) {
    return new GithubAutoUpdater_Factory(contextProvider);
  }

  public static GithubAutoUpdater newInstance(Context context) {
    return new GithubAutoUpdater(context);
  }
}
