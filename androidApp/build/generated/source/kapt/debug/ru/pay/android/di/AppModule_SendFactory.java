package ru.pay.android.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import ru.pay.domain.PaymentRepository;
import ru.pay.domain.SendPayment;

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
public final class AppModule_SendFactory implements Factory<SendPayment> {
  private final Provider<PaymentRepository> repositoryProvider;

  public AppModule_SendFactory(Provider<PaymentRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public SendPayment get() {
    return send(repositoryProvider.get());
  }

  public static AppModule_SendFactory create(Provider<PaymentRepository> repositoryProvider) {
    return new AppModule_SendFactory(repositoryProvider);
  }

  public static SendPayment send(PaymentRepository repository) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.send(repository));
  }
}
