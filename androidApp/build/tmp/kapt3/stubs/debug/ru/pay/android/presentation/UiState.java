package ru.pay.android.presentation;

import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import dagger.hilt.android.lifecycle.HiltViewModel;
import kotlinx.coroutines.*;
import kotlinx.coroutines.flow.*;
import ru.pay.domain.*;
import javax.inject.Inject;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000F\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b&\b\u0087\b\u0018\u00002\u00020\u0001B\u008b\u0001\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u0006\u0012\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\u0006\u0012\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\t\u0012\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u000b\u0012\u000e\b\u0002\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000e0\r\u0012\u000e\b\u0002\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00100\r\u0012\n\b\u0002\u0010\u0011\u001a\u0004\u0018\u00010\u0012\u0012\b\b\u0002\u0010\u0013\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0014\u001a\u0004\u0018\u00010\u0015\u00a2\u0006\u0002\u0010\u0016J\t\u0010*\u001a\u00020\u0003H\u00c6\u0003J\t\u0010+\u001a\u00020\u0003H\u00c6\u0003J\u000b\u0010,\u001a\u0004\u0018\u00010\u0015H\u00c6\u0003J\t\u0010-\u001a\u00020\u0003H\u00c6\u0003J\u000b\u0010.\u001a\u0004\u0018\u00010\u0006H\u00c6\u0003J\u000b\u0010/\u001a\u0004\u0018\u00010\u0006H\u00c6\u0003J\u000b\u00100\u001a\u0004\u0018\u00010\tH\u00c6\u0003J\u000b\u00101\u001a\u0004\u0018\u00010\u000bH\u00c6\u0003J\u000f\u00102\u001a\b\u0012\u0004\u0012\u00020\u000e0\rH\u00c6\u0003J\u000f\u00103\u001a\b\u0012\u0004\u0012\u00020\u00100\rH\u00c6\u0003J\u0010\u00104\u001a\u0004\u0018\u00010\u0012H\u00c6\u0003\u00a2\u0006\u0002\u0010!J\u0094\u0001\u00105\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u00062\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\u00062\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\t2\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u000b2\u000e\b\u0002\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000e0\r2\u000e\b\u0002\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00100\r2\n\b\u0002\u0010\u0011\u001a\u0004\u0018\u00010\u00122\b\b\u0002\u0010\u0013\u001a\u00020\u00032\n\b\u0002\u0010\u0014\u001a\u0004\u0018\u00010\u0015H\u00c6\u0001\u00a2\u0006\u0002\u00106J\u0013\u00107\u001a\u00020\u00032\b\u00108\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u00109\u001a\u00020\u0012H\u00d6\u0001J\t\u0010:\u001a\u00020\u0006H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u0018R\u0017\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000e0\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u001bR\u0011\u0010\u0013\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u0018R\u0013\u0010\u0005\u001a\u0004\u0018\u00010\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u001eR\u0013\u0010\u0007\u001a\u0004\u0018\u00010\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010\u001eR\u0015\u0010\u0011\u001a\u0004\u0018\u00010\u0012\u00a2\u0006\n\n\u0002\u0010\"\u001a\u0004\b \u0010!R\u0013\u0010\u0014\u001a\u0004\u0018\u00010\u0015\u00a2\u0006\b\n\u0000\u001a\u0004\b#\u0010$R\u0013\u0010\b\u001a\u0004\u0018\u00010\t\u00a2\u0006\b\n\u0000\u001a\u0004\b%\u0010&R\u0017\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00100\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\'\u0010\u001bR\u0013\u0010\n\u001a\u0004\u0018\u00010\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b(\u0010)\u00a8\u0006;"}, d2 = {"Lru/pay/android/presentation/UiState;", "", "authenticated", "", "busy", "error", "", "message", "profile", "Lru/pay/domain/Profile;", "wallet", "Lru/pay/domain/Wallet;", "cards", "", "Lru/pay/domain/Card;", "transactions", "Lru/pay/domain/Transaction;", "nextOffset", "", "dark", "pending", "Lru/pay/domain/Transfer;", "(ZZLjava/lang/String;Ljava/lang/String;Lru/pay/domain/Profile;Lru/pay/domain/Wallet;Ljava/util/List;Ljava/util/List;Ljava/lang/Integer;ZLru/pay/domain/Transfer;)V", "getAuthenticated", "()Z", "getBusy", "getCards", "()Ljava/util/List;", "getDark", "getError", "()Ljava/lang/String;", "getMessage", "getNextOffset", "()Ljava/lang/Integer;", "Ljava/lang/Integer;", "getPending", "()Lru/pay/domain/Transfer;", "getProfile", "()Lru/pay/domain/Profile;", "getTransactions", "getWallet", "()Lru/pay/domain/Wallet;", "component1", "component10", "component11", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "(ZZLjava/lang/String;Ljava/lang/String;Lru/pay/domain/Profile;Lru/pay/domain/Wallet;Ljava/util/List;Ljava/util/List;Ljava/lang/Integer;ZLru/pay/domain/Transfer;)Lru/pay/android/presentation/UiState;", "equals", "other", "hashCode", "toString", "androidApp_debug"})
public final class UiState {
    private final boolean authenticated = false;
    private final boolean busy = false;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String error = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String message = null;
    @org.jetbrains.annotations.Nullable()
    private final ru.pay.domain.Profile profile = null;
    @org.jetbrains.annotations.Nullable()
    private final ru.pay.domain.Wallet wallet = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<ru.pay.domain.Card> cards = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<ru.pay.domain.Transaction> transactions = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Integer nextOffset = null;
    private final boolean dark = false;
    @org.jetbrains.annotations.Nullable()
    private final ru.pay.domain.Transfer pending = null;
    
    public UiState(boolean authenticated, boolean busy, @org.jetbrains.annotations.Nullable()
    java.lang.String error, @org.jetbrains.annotations.Nullable()
    java.lang.String message, @org.jetbrains.annotations.Nullable()
    ru.pay.domain.Profile profile, @org.jetbrains.annotations.Nullable()
    ru.pay.domain.Wallet wallet, @org.jetbrains.annotations.NotNull()
    java.util.List<ru.pay.domain.Card> cards, @org.jetbrains.annotations.NotNull()
    java.util.List<ru.pay.domain.Transaction> transactions, @org.jetbrains.annotations.Nullable()
    java.lang.Integer nextOffset, boolean dark, @org.jetbrains.annotations.Nullable()
    ru.pay.domain.Transfer pending) {
        super();
    }
    
    public final boolean getAuthenticated() {
        return false;
    }
    
    public final boolean getBusy() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getError() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getMessage() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final ru.pay.domain.Profile getProfile() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final ru.pay.domain.Wallet getWallet() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<ru.pay.domain.Card> getCards() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<ru.pay.domain.Transaction> getTransactions() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer getNextOffset() {
        return null;
    }
    
    public final boolean getDark() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final ru.pay.domain.Transfer getPending() {
        return null;
    }
    
    public UiState() {
        super();
    }
    
    public final boolean component1() {
        return false;
    }
    
    public final boolean component10() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final ru.pay.domain.Transfer component11() {
        return null;
    }
    
    public final boolean component2() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component3() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component4() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final ru.pay.domain.Profile component5() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final ru.pay.domain.Wallet component6() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<ru.pay.domain.Card> component7() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<ru.pay.domain.Transaction> component8() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer component9() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final ru.pay.android.presentation.UiState copy(boolean authenticated, boolean busy, @org.jetbrains.annotations.Nullable()
    java.lang.String error, @org.jetbrains.annotations.Nullable()
    java.lang.String message, @org.jetbrains.annotations.Nullable()
    ru.pay.domain.Profile profile, @org.jetbrains.annotations.Nullable()
    ru.pay.domain.Wallet wallet, @org.jetbrains.annotations.NotNull()
    java.util.List<ru.pay.domain.Card> cards, @org.jetbrains.annotations.NotNull()
    java.util.List<ru.pay.domain.Transaction> transactions, @org.jetbrains.annotations.Nullable()
    java.lang.Integer nextOffset, boolean dark, @org.jetbrains.annotations.Nullable()
    ru.pay.domain.Transfer pending) {
        return null;
    }
    
    @java.lang.Override()
    public boolean equals(@org.jetbrains.annotations.Nullable()
    java.lang.Object other) {
        return false;
    }
    
    @java.lang.Override()
    public int hashCode() {
        return 0;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.lang.String toString() {
        return null;
    }
}