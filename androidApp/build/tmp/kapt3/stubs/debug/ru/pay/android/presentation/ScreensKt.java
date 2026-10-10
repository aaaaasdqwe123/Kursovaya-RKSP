package ru.pay.android.presentation;

import androidx.compose.foundation.*;
import androidx.compose.foundation.layout.*;
import androidx.compose.foundation.text.KeyboardOptions;
import androidx.compose.material3.*;
import androidx.compose.runtime.*;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.text.input.*;
import ru.pay.domain.*;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000@\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0003\u001a<\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032*\u0010\u0004\u001a&\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00010\u0005H\u0003\u001a\u0018\u0010\u0007\u001a\u00020\u00012\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bH\u0003\u001a\u0018\u0010\f\u001a\u00020\u00012\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bH\u0003\u001a&\u0010\r\u001a\u00020\u00012\u0006\u0010\u000e\u001a\u00020\u00062\u0006\u0010\u000f\u001a\u00020\u00032\f\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00010\u0011H\u0003\u001a\u0012\u0010\u0012\u001a\u00020\u00012\b\b\u0002\u0010\n\u001a\u00020\u000bH\u0007\u001a&\u0010\u0013\u001a\u00020\u00012\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u00032\f\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00010\u0011H\u0003\u001a\u0010\u0010\u0018\u001a\u00020\u00012\u0006\u0010\n\u001a\u00020\u000bH\u0003\u001a\u0018\u0010\u0019\u001a\u00020\u00012\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bH\u0003\u001a\u0010\u0010\u001a\u001a\u00020\u00012\u0006\u0010\u000e\u001a\u00020\u0006H\u0003\u001a\u0010\u0010\u001b\u001a\u00020\u00012\u0006\u0010\u001c\u001a\u00020\u001dH\u0003\u001a\u0018\u0010\u001e\u001a\u00020\u00012\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bH\u0003\u001a\u0018\u0010\u001f\u001a\u00020\u00012\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bH\u0003\u00a8\u0006 "}, d2 = {"AuthScreen", "", "busy", "", "submit", "Lkotlin/Function5;", "", "CardsScreen", "ui", "Lru/pay/android/presentation/UiState;", "vm", "Lru/pay/android/presentation/PaymentViewModel;", "Dashboard", "Notice", "text", "error", "onDismiss", "Lkotlin/Function0;", "PaymentRoot", "SearchRow", "item", "Lru/pay/domain/SearchItem;", "enabled", "onClick", "SearchScreen", "SettingsScreen", "Title", "TransactionRow", "tx", "Lru/pay/domain/Transaction;", "TransactionsScreen", "TransferScreen", "androidApp_debug"})
public final class ScreensKt {
    
    @androidx.compose.runtime.Composable()
    public static final void PaymentRoot(@org.jetbrains.annotations.NotNull()
    ru.pay.android.presentation.PaymentViewModel vm) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void Notice(java.lang.String text, boolean error, kotlin.jvm.functions.Function0<kotlin.Unit> onDismiss) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void Title(java.lang.String text) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void AuthScreen(boolean busy, kotlin.jvm.functions.Function5<? super java.lang.Boolean, ? super java.lang.String, ? super java.lang.String, ? super java.lang.String, ? super java.lang.String, kotlin.Unit> submit) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void Dashboard(ru.pay.android.presentation.UiState ui, ru.pay.android.presentation.PaymentViewModel vm) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void TransferScreen(ru.pay.android.presentation.UiState ui, ru.pay.android.presentation.PaymentViewModel vm) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void SearchScreen(ru.pay.android.presentation.PaymentViewModel vm) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void SearchRow(ru.pay.domain.SearchItem item, boolean enabled, kotlin.jvm.functions.Function0<kotlin.Unit> onClick) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void CardsScreen(ru.pay.android.presentation.UiState ui, ru.pay.android.presentation.PaymentViewModel vm) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void TransactionRow(ru.pay.domain.Transaction tx) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void TransactionsScreen(ru.pay.android.presentation.UiState ui, ru.pay.android.presentation.PaymentViewModel vm) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void SettingsScreen(ru.pay.android.presentation.UiState ui, ru.pay.android.presentation.PaymentViewModel vm) {
    }
}