package ru.pay.android.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import ru.pay.domain.PaymentRepository;
import ru.pay.domain.SearchRecipients;

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
public final class AppModule_SearchFactory implements Factory<SearchRecipients> {
  private final Provider<PaymentRepository> repositoryProvider;

  public AppModule_SearchFactory(Provider<PaymentRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public SearchRecipients get() {
    return search(repositoryProvider.get());
  }

  public static AppModule_SearchFactory create(Provider<PaymentRepository> repositoryProvider) {
    return new AppModule_SearchFactory(repositoryProvider);
  }

  public static SearchRecipients search(PaymentRepository repository) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.search(repository));
  }
}
