package ru.pay.android.presentation;

import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import dagger.hilt.android.lifecycle.HiltViewModel;
import kotlinx.coroutines.*;
import kotlinx.coroutines.flow.*;
import ru.pay.domain.*;
import javax.inject.Inject;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0088\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\r\n\u0002\u0010\u0003\n\u0002\b\u0007\n\u0002\u0010\b\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0007\u0018\u00002\u00020\u0001B\'\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u00a2\u0006\u0002\u0010\nJ+\u0010+\u001a\u00020,2\u001c\u0010-\u001a\u0018\b\u0001\u0012\n\u0012\b\u0012\u0004\u0012\u00020,0/\u0012\u0006\u0012\u0004\u0018\u0001000.H\u0002\u00a2\u0006\u0002\u00101J\u000e\u00102\u001a\u00020,2\u0006\u00103\u001a\u00020\rJ.\u00104\u001a\u00020,2\u0006\u00105\u001a\u0002062\u0006\u00107\u001a\u00020\r2\u0006\u00108\u001a\u00020\r2\u0006\u00109\u001a\u00020\r2\u0006\u0010:\u001a\u00020\rJ\u000e\u0010;\u001a\u00020,2\u0006\u0010<\u001a\u00020\rJ\u0006\u0010=\u001a\u00020,J\u000e\u0010>\u001a\u00020,2\u0006\u0010<\u001a\u000206J\u000e\u0010?\u001a\u00020,2\u0006\u0010@\u001a\u00020\rJ\u0006\u0010A\u001a\u00020,J\u0010\u0010B\u001a\u00020,2\u0006\u0010C\u001a\u00020DH\u0002J\u0016\u0010E\u001a\u00020,2\u0006\u0010F\u001a\u00020\r2\u0006\u0010<\u001a\u00020\rJ\u0006\u0010G\u001a\u00020,J\u000e\u0010H\u001a\u00020,H\u0082@\u00a2\u0006\u0002\u0010IJ\u0018\u0010J\u001a\u00020,2\b\b\u0002\u0010K\u001a\u00020LH\u0082@\u00a2\u0006\u0002\u0010MJ\u0006\u0010N\u001a\u00020,J\u0006\u0010O\u001a\u00020,J\u000e\u0010P\u001a\u00020,2\u0006\u0010<\u001a\u00020\rJ\u0006\u0010Q\u001a\u00020,J\u0006\u0010R\u001a\u00020,J\u000e\u0010S\u001a\u00020,2\u0006\u0010T\u001a\u00020UJ\u0010\u0010V\u001a\u00020,2\b\b\u0002\u0010W\u001a\u000206J\u0006\u0010X\u001a\u00020,R\u0017\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\r0\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0017\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\r0\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u000fR\u0017\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\r0\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u000fR\u0014\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00160\u0015X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\r0\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u000fR\u0017\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\r0\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u000fR\u0017\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\r0\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u000fR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\u001e0\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010\u000fR\u000e\u0010 \u001a\u00020!X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\"\u001a\u0004\u0018\u00010#X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010$\u001a\b\u0012\u0004\u0012\u00020\u001e0\u0015X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010%\u001a\b\u0012\u0004\u0012\u00020\r0\f\u00a2\u0006\b\n\u0000\u001a\u0004\b&\u0010\u000fR\u0017\u0010\'\u001a\b\u0012\u0004\u0012\u00020\r0\f\u00a2\u0006\b\n\u0000\u001a\u0004\b(\u0010\u000fR\u0017\u0010)\u001a\b\u0012\u0004\u0012\u00020\u00160\f\u00a2\u0006\b\n\u0000\u001a\u0004\b*\u0010\u000f\u00a8\u0006Y"}, d2 = {"Lru/pay/android/presentation/PaymentViewModel;", "Landroidx/lifecycle/ViewModel;", "repo", "Lru/pay/domain/PaymentRepository;", "sendPayment", "Lru/pay/domain/SendPayment;", "searchRecipients", "Lru/pay/domain/SearchRecipients;", "saved", "Landroidx/lifecycle/SavedStateHandle;", "(Lru/pay/domain/PaymentRepository;Lru/pay/domain/SendPayment;Lru/pay/domain/SearchRecipients;Landroidx/lifecycle/SavedStateHandle;)V", "amount", "Lkotlinx/coroutines/flow/StateFlow;", "", "getAmount", "()Lkotlinx/coroutines/flow/StateFlow;", "filter", "getFilter", "kind", "getKind", "mutable", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lru/pay/android/presentation/UiState;", "note", "getNote", "query", "getQuery", "recipient", "getRecipient", "search", "Lru/pay/android/presentation/SearchState;", "getSearch", "searchGeneration", "", "searchJob", "Lkotlinx/coroutines/Job;", "searchMutable", "tab", "getTab", "transactionQuery", "getTransactionQuery", "ui", "getUi", "action", "", "block", "Lkotlin/Function1;", "Lkotlin/coroutines/Continuation;", "", "(Lkotlin/jvm/functions/Function1;)V", "addCard", "alias", "auth", "register", "", "email", "password", "name", "phone", "changeQuery", "value", "clearHistory", "dark", "deleteCard", "id", "dismiss", "failure", "e", "", "field", "key", "history", "loadDashboard", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "loadTransactions", "offset", "", "(ILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "logout", "more", "navigate", "pay", "refresh", "select", "item", "Lru/pay/domain/SearchItem;", "submitSearch", "retry", "transactions", "androidApp_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class PaymentViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final ru.pay.domain.PaymentRepository repo = null;
    @org.jetbrains.annotations.NotNull()
    private final ru.pay.domain.SendPayment sendPayment = null;
    @org.jetbrains.annotations.NotNull()
    private final ru.pay.domain.SearchRecipients searchRecipients = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.SavedStateHandle saved = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<ru.pay.android.presentation.UiState> mutable = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<ru.pay.android.presentation.UiState> ui = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<ru.pay.android.presentation.SearchState> searchMutable = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<ru.pay.android.presentation.SearchState> search = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.String> query = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.String> tab = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.String> recipient = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.String> amount = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.String> note = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.String> kind = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.String> filter = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.String> transactionQuery = null;
    @org.jetbrains.annotations.Nullable()
    private kotlinx.coroutines.Job searchJob;
    private long searchGeneration = 0L;
    
    @javax.inject.Inject()
    public PaymentViewModel(@org.jetbrains.annotations.NotNull()
    ru.pay.domain.PaymentRepository repo, @org.jetbrains.annotations.NotNull()
    ru.pay.domain.SendPayment sendPayment, @org.jetbrains.annotations.NotNull()
    ru.pay.domain.SearchRecipients searchRecipients, @org.jetbrains.annotations.NotNull()
    androidx.lifecycle.SavedStateHandle saved) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<ru.pay.android.presentation.UiState> getUi() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<ru.pay.android.presentation.SearchState> getSearch() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.String> getQuery() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.String> getTab() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.String> getRecipient() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.String> getAmount() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.String> getNote() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.String> getKind() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.String> getFilter() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.String> getTransactionQuery() {
        return null;
    }
    
    public final void navigate(@org.jetbrains.annotations.NotNull()
    java.lang.String value) {
    }
    
    public final void field(@org.jetbrains.annotations.NotNull()
    java.lang.String key, @org.jetbrains.annotations.NotNull()
    java.lang.String value) {
    }
    
    public final void dismiss() {
    }
    
    private final void failure(java.lang.Throwable e) {
    }
    
    private final void action(kotlin.jvm.functions.Function1<? super kotlin.coroutines.Continuation<? super kotlin.Unit>, ? extends java.lang.Object> block) {
    }
    
    public final void auth(boolean register, @org.jetbrains.annotations.NotNull()
    java.lang.String email, @org.jetbrains.annotations.NotNull()
    java.lang.String password, @org.jetbrains.annotations.NotNull()
    java.lang.String name, @org.jetbrains.annotations.NotNull()
    java.lang.String phone) {
    }
    
    private final java.lang.Object loadDashboard(kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    public final void refresh() {
    }
    
    private final java.lang.Object loadTransactions(int offset, kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    public final void transactions() {
    }
    
    public final void more() {
    }
    
    public final void dark(boolean value) {
    }
    
    public final void addCard(@org.jetbrains.annotations.NotNull()
    java.lang.String alias) {
    }
    
    public final void deleteCard(@org.jetbrains.annotations.NotNull()
    java.lang.String id) {
    }
    
    public final void pay() {
    }
    
    public final void logout() {
    }
    
    public final void changeQuery(@org.jetbrains.annotations.NotNull()
    java.lang.String value) {
    }
    
    public final void submitSearch(boolean retry) {
    }
    
    public final void history() {
    }
    
    public final void clearHistory() {
    }
    
    public final void select(@org.jetbrains.annotations.NotNull()
    ru.pay.domain.SearchItem item) {
    }
}