package ru.pay.android.di;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import javax.inject.Singleton;
import io.ktor.client.engine.okhttp.OkHttp;
import io.ktor.client.plugins.HttpTimeout;
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation;
import ru.pay.domain.*;
import ru.pay.android.data.RemoteRepository;

@dagger.Module()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u00c7\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0003\u001a\u00020\u0004H\u0007J\u0010\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bH\u0007J\u0010\u0010\t\u001a\u00020\n2\u0006\u0010\u0005\u001a\u00020\u0006H\u0007J\u0010\u0010\u000b\u001a\u00020\f2\u0006\u0010\u0005\u001a\u00020\u0006H\u0007\u00a8\u0006\r"}, d2 = {"Lru/pay/android/di/AppModule;", "", "()V", "client", "Lio/ktor/client/HttpClient;", "repository", "Lru/pay/domain/PaymentRepository;", "remote", "Lru/pay/android/data/RemoteRepository;", "search", "Lru/pay/domain/SearchRecipients;", "send", "Lru/pay/domain/SendPayment;", "androidApp_debug"})
@dagger.hilt.InstallIn(value = {dagger.hilt.components.SingletonComponent.class})
public final class AppModule {
    @org.jetbrains.annotations.NotNull()
    public static final ru.pay.android.di.AppModule INSTANCE = null;
    
    private AppModule() {
        super();
    }
    
    @dagger.Provides()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public final io.ktor.client.HttpClient client() {
        return null;
    }
    
    @dagger.Provides()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public final ru.pay.domain.PaymentRepository repository(@org.jetbrains.annotations.NotNull()
    ru.pay.android.data.RemoteRepository remote) {
        return null;
    }
    
    @dagger.Provides()
    @org.jetbrains.annotations.NotNull()
    public final ru.pay.domain.SendPayment send(@org.jetbrains.annotations.NotNull()
    ru.pay.domain.PaymentRepository repository) {
        return null;
    }
    
    @dagger.Provides()
    @org.jetbrains.annotations.NotNull()
    public final ru.pay.domain.SearchRecipients search(@org.jetbrains.annotations.NotNull()
    ru.pay.domain.PaymentRepository repository) {
        return null;
    }
}