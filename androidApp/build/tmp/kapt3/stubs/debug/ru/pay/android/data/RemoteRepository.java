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

@javax.inject.Singleton()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u00a2\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010$\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u0017\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u0016\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000eH\u0096@\u00a2\u0006\u0002\u0010\u000fJ\u0014\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\f0\u0011H\u0096@\u00a2\u0006\u0002\u0010\u0012J\u000e\u0010\u0013\u001a\u00020\u0014H\u0096@\u00a2\u0006\u0002\u0010\u0012J\b\u0010\u0015\u001a\u00020\u0016H\u0016J\u0014\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00180\u0011H\u0096@\u00a2\u0006\u0002\u0010\u0012J\u001e\u0010\u0019\u001a\u00020\u00142\u0006\u0010\u001a\u001a\u00020\u000e2\u0006\u0010\u001b\u001a\u00020\u000eH\u0096@\u00a2\u0006\u0002\u0010\u001cJ\u000e\u0010\u001d\u001a\u00020\u0014H\u0096@\u00a2\u0006\u0002\u0010\u0012J\u0010\u0010\u001e\u001a\u00020\u00182\u0006\u0010\u001f\u001a\u00020 H\u0002J\u0010\u0010!\u001a\u00020\"2\u0006\u0010\u001f\u001a\u00020#H\u0002J\b\u0010$\u001a\u00020\u000eH\u0002J\n\u0010%\u001a\u0004\u0018\u00010&H\u0016J\u000e\u0010\'\u001a\u00020(H\u0096@\u00a2\u0006\u0002\u0010\u0012J.\u0010)\u001a\u00020\u00142\u0006\u0010\u001a\u001a\u00020\u000e2\u0006\u0010\u001b\u001a\u00020\u000e2\u0006\u0010*\u001a\u00020\u000e2\u0006\u0010+\u001a\u00020\u000eH\u0096@\u00a2\u0006\u0002\u0010,J\u0016\u0010-\u001a\u00020\u00142\u0006\u0010.\u001a\u00020\u000eH\u0096@\u00a2\u0006\u0002\u0010\u000fJV\u0010/\u001a\u0002H0\"\u0006\b\u0000\u00100\u0018\u00012\u0006\u00101\u001a\u00020\u000e2\b\b\u0002\u00102\u001a\u0002032\n\b\u0002\u00104\u001a\u0004\u0018\u0001052\n\b\u0002\u00106\u001a\u0004\u0018\u00010\u000e2\u0014\b\u0002\u00107\u001a\u000e\u0012\u0004\u0012\u00020\u000e\u0012\u0004\u0012\u00020\u000e08H\u0082H\u00a2\u0006\u0002\u00109J\u001c\u0010:\u001a\b\u0012\u0004\u0012\u00020\u00180\u00112\u0006\u00107\u001a\u00020\u000eH\u0096@\u00a2\u0006\u0002\u0010\u000fJ\u001c\u0010;\u001a\b\u0012\u0004\u0012\u00020\u00180\u00112\u0006\u0010.\u001a\u00020\u000eH\u0096@\u00a2\u0006\u0002\u0010\u000fJ\u0016\u0010<\u001a\u00020=2\u0006\u0010>\u001a\u00020\u0016H\u0096@\u00a2\u0006\u0002\u0010?J\u000e\u0010@\u001a\u00020=H\u0096@\u00a2\u0006\u0002\u0010\u0012J&\u0010A\u001a\u00020B2\u0006\u00107\u001a\u00020\u000e2\u0006\u0010C\u001a\u00020\u000e2\u0006\u0010D\u001a\u00020EH\u0096@\u00a2\u0006\u0002\u0010FJ\u0016\u0010G\u001a\u00020\"2\u0006\u0010/\u001a\u00020&H\u0096@\u00a2\u0006\u0002\u0010HJ\u000e\u0010I\u001a\u00020JH\u0096@\u00a2\u0006\u0002\u0010\u0012R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006K"}, d2 = {"Lru/pay/android/data/RemoteRepository;", "Lru/pay/domain/PaymentRepository;", "client", "Lio/ktor/client/HttpClient;", "store", "Lru/pay/android/data/SecureStore;", "(Lio/ktor/client/HttpClient;Lru/pay/android/data/SecureStore;)V", "json", "Lkotlinx/serialization/json/Json;", "paymentMutex", "Lkotlinx/coroutines/sync/Mutex;", "addCard", "Lru/pay/domain/Card;", "alias", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "cards", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "clearHistory", "", "hasSession", "", "history", "Lru/pay/domain/SearchItem;", "login", "email", "password", "(Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "logout", "mapSearch", "it", "Lru/pay/contracts/SearchItem;", "mapTransaction", "Lru/pay/domain/Transaction;", "Lru/pay/contracts/Transaction;", "pendingKey", "pendingTransfer", "Lru/pay/domain/Transfer;", "profile", "Lru/pay/domain/Profile;", "register", "name", "phone", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "removeCard", "id", "request", "T", "path", "method", "Lio/ktor/http/HttpMethod;", "body", "", "key", "query", "", "(Ljava/lang/String;Lio/ktor/http/HttpMethod;Ljava/lang/Object;Ljava/lang/String;Ljava/util/Map;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "search", "select", "setDarkTheme", "Lru/pay/domain/Settings;", "dark", "(ZLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "settings", "transactions", "Lru/pay/domain/TransactionPage;", "direction", "offset", "", "(Ljava/lang/String;Ljava/lang/String;ILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "transfer", "(Lru/pay/domain/Transfer;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "wallet", "Lru/pay/domain/Wallet;", "androidApp_debug"})
public final class RemoteRepository implements ru.pay.domain.PaymentRepository {
    @org.jetbrains.annotations.NotNull()
    private final io.ktor.client.HttpClient client = null;
    @org.jetbrains.annotations.NotNull()
    private final ru.pay.android.data.SecureStore store = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.serialization.json.Json json = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.sync.Mutex paymentMutex = null;
    
    @javax.inject.Inject()
    public RemoteRepository(@org.jetbrains.annotations.NotNull()
    io.ktor.client.HttpClient client, @org.jetbrains.annotations.NotNull()
    ru.pay.android.data.SecureStore store) {
        super();
    }
    
    @java.lang.Override()
    public boolean hasSession() {
        return false;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object login(@org.jetbrains.annotations.NotNull()
    java.lang.String email, @org.jetbrains.annotations.NotNull()
    java.lang.String password, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object register(@org.jetbrains.annotations.NotNull()
    java.lang.String email, @org.jetbrains.annotations.NotNull()
    java.lang.String password, @org.jetbrains.annotations.NotNull()
    java.lang.String name, @org.jetbrains.annotations.NotNull()
    java.lang.String phone, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object logout(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object profile(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super ru.pay.domain.Profile> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object wallet(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super ru.pay.domain.Wallet> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object settings(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super ru.pay.domain.Settings> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object setDarkTheme(boolean dark, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super ru.pay.domain.Settings> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object cards(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<ru.pay.domain.Card>> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object addCard(@org.jetbrains.annotations.NotNull()
    java.lang.String alias, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super ru.pay.domain.Card> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object removeCard(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object transactions(@org.jetbrains.annotations.NotNull()
    java.lang.String query, @org.jetbrains.annotations.NotNull()
    java.lang.String direction, int offset, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super ru.pay.domain.TransactionPage> $completion) {
        return null;
    }
    
    private final java.lang.String pendingKey() {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public ru.pay.domain.Transfer pendingTransfer() {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object transfer(@org.jetbrains.annotations.NotNull()
    ru.pay.domain.Transfer request, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super ru.pay.domain.Transaction> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object search(@org.jetbrains.annotations.NotNull()
    java.lang.String query, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<ru.pay.domain.SearchItem>> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object history(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<ru.pay.domain.SearchItem>> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object select(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<ru.pay.domain.SearchItem>> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object clearHistory(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    private final ru.pay.domain.SearchItem mapSearch(ru.pay.contracts.SearchItem it) {
        return null;
    }
    
    private final ru.pay.domain.Transaction mapTransaction(ru.pay.contracts.Transaction it) {
        return null;
    }
}