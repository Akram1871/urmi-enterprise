package com.akram.babsharhisab;

import com.akram.babsharhisab.core.security.SecureConfigStore;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class MainActivity_MembersInjector implements MembersInjector<MainActivity> {
  private final Provider<SecureConfigStore> secureConfigStoreProvider;

  public MainActivity_MembersInjector(Provider<SecureConfigStore> secureConfigStoreProvider) {
    this.secureConfigStoreProvider = secureConfigStoreProvider;
  }

  public static MembersInjector<MainActivity> create(
      Provider<SecureConfigStore> secureConfigStoreProvider) {
    return new MainActivity_MembersInjector(secureConfigStoreProvider);
  }

  @Override
  public void injectMembers(MainActivity instance) {
    injectSecureConfigStore(instance, secureConfigStoreProvider.get());
  }

  @InjectedFieldSignature("com.akram.babsharhisab.MainActivity.secureConfigStore")
  public static void injectSecureConfigStore(MainActivity instance,
      SecureConfigStore secureConfigStore) {
    instance.secureConfigStore = secureConfigStore;
  }
}
