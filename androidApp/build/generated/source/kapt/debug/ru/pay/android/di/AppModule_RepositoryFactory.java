package ru.pay.android.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import ru.pay.android.data.RemoteRepository;
import ru.pay.domain.PaymentRepository;

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
public final class AppModule_RepositoryFactory implements Factory<PaymentRepository> {
  private final Provider<RemoteRepository> remoteProvider;

  public AppModule_RepositoryFactory(Provider<RemoteRepository> remoteProvider) {
    this.remoteProvider = remoteProvider;
  }

  @Override
  public PaymentRepository get() {
    return repository(remoteProvider.get());
  }

  public static AppModule_RepositoryFactory create(Provider<RemoteRepository> remoteProvider) {
    return new AppModule_RepositoryFactory(remoteProvider);
  }

  public static PaymentRepository repository(RemoteRepository remote) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.repository(remote));
  }
}
