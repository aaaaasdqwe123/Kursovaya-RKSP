package ru.pay.android.data;

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
public final class SecureStore_Factory implements Factory<SecureStore> {
  private final Provider<Context> contextProvider;

  public SecureStore_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public SecureStore get() {
    return newInstance(contextProvider.get());
  }

  public static SecureStore_Factory create(Provider<Context> contextProvider) {
    return new SecureStore_Factory(contextProvider);
  }

  public static SecureStore newInstance(Context context) {
    return new SecureStore(context);
  }
}
