package com.akram.babsharhisab.data.remote;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class SheetsWebhookClient_Factory implements Factory<SheetsWebhookClient> {
  @Override
  public SheetsWebhookClient get() {
    return newInstance();
  }

  public static SheetsWebhookClient_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static SheetsWebhookClient newInstance() {
    return new SheetsWebhookClient();
  }

  private static final class InstanceHolder {
    private static final SheetsWebhookClient_Factory INSTANCE = new SheetsWebhookClient_Factory();
  }
}
