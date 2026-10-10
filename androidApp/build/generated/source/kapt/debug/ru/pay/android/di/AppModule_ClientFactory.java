package ru.pay.android.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.ktor.client.HttpClient;
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
public final class AppModule_ClientFactory implements Factory<HttpClient> {
  @Override
  public HttpClient get() {
    return client();
  }

  public static AppModule_ClientFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static HttpClient client() {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.client());
  }

  private static final class InstanceHolder {
    private static final AppModule_ClientFactory INSTANCE = new AppModule_ClientFactory();
  }
}
