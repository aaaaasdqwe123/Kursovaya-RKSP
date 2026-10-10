package ru.pay.android.presentation;

import androidx.lifecycle.SavedStateHandle;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import ru.pay.domain.PaymentRepository;
import ru.pay.domain.SearchRecipients;
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
public final class PaymentViewModel_Factory implements Factory<PaymentViewModel> {
  private final Provider<PaymentRepository> repoProvider;

  private final Provider<SendPayment> sendPaymentProvider;

  private final Provider<SearchRecipients> searchRecipientsProvider;

  private final Provider<SavedStateHandle> savedProvider;

  public PaymentViewModel_Factory(Provider<PaymentRepository> repoProvider,
      Provider<SendPayment> sendPaymentProvider,
      Provider<SearchRecipients> searchRecipientsProvider,
      Provider<SavedStateHandle> savedProvider) {
    this.repoProvider = repoProvider;
    this.sendPaymentProvider = sendPaymentProvider;
    this.searchRecipientsProvider = searchRecipientsProvider;
    this.savedProvider = savedProvider;
  }

  @Override
  public PaymentViewModel get() {
    return newInstance(repoProvider.get(), sendPaymentProvider.get(), searchRecipientsProvider.get(), savedProvider.get());
  }

  public static PaymentViewModel_Factory create(Provider<PaymentRepository> repoProvider,
      Provider<SendPayment> sendPaymentProvider,
      Provider<SearchRecipients> searchRecipientsProvider,
      Provider<SavedStateHandle> savedProvider) {
    return new PaymentViewModel_Factory(repoProvider, sendPaymentProvider, searchRecipientsProvider, savedProvider);
  }

  public static PaymentViewModel newInstance(PaymentRepository repo, SendPayment sendPayment,
      SearchRecipients searchRecipients, SavedStateHandle saved) {
    return new PaymentViewModel(repo, sendPayment, searchRecipients, saved);
  }
}
