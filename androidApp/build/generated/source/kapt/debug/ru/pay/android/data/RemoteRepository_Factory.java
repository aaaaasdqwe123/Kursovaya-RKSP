package ru.pay.android.data;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.ktor.client.HttpClient;
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
public final class RemoteRepository_Factory implements Factory<RemoteRepository> {
  private final Provider<HttpClient> clientProvider;

  private final Provider<SecureStore> storeProvider;

  public RemoteRepository_Factory(Provider<HttpClient> clientProvider,
      Provider<SecureStore> storeProvider) {
    this.clientProvider = clientProvider;
    this.storeProvider = storeProvider;
  }

  @Override
  public RemoteRepository get() {
    return newInstance(clientProvider.get(), storeProvider.get());
  }

  public static RemoteRepository_Factory create(Provider<HttpClient> clientProvider,
      Provider<SecureStore> storeProvider) {
    return new RemoteRepository_Factory(clientProvider, storeProvider);
  }

  public static RemoteRepository newInstance(HttpClient client, SecureStore store) {
    return new RemoteRepository(client, store);
  }
}
