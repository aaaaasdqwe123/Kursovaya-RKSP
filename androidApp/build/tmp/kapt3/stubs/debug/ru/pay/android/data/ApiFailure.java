package ru.pay.android.data;

import ru.pay.domain.*;
import ru.pay.android.BuildConfig;
import io.ktor.client.*;
import io.ktor.client.call.*;
import io.ktor.client.request.*;
import io.ktor.client.statement.*;
import io.ktor.http.*;
import kotlinx.coroutines.Dispatchers;
import javax.inject.Inject;
import javax.inject.Singleton;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0006\b\u0007\u0018\u00002\u00060\u0001j\u0002`\u0002B\u0015\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\u0002\u0010\u0007R\u0014\u0010\u0005\u001a\u00020\u0006X\u0096\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0003\u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b\u00a8\u0006\f"}, d2 = {"Lru/pay/android/data/ApiFailure;", "Ljava/lang/Exception;", "Lkotlin/Exception;", "status", "", "message", "", "(ILjava/lang/String;)V", "getMessage", "()Ljava/lang/String;", "getStatus", "()I", "androidApp_debug"})
public final class ApiFailure extends java.lang.Exception {
    private final int status = 0;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String message = null;
    
    public ApiFailure(int status, @org.jetbrains.annotations.NotNull()
    java.lang.String message) {
        super();
    }
    
    public final int getStatus() {
        return 0;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.lang.String getMessage() {
        return null;
    }
}