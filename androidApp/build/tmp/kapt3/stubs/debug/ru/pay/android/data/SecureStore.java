package ru.pay.android.data;

import android.content.Context;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import dagger.hilt.android.qualifiers.ApplicationContext;
import java.security.KeyStore;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Small encrypted session and pending-payment records, excluded from Android backups.
 */
@javax.inject.Singleton()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\u0011\b\u0007\u0012\b\b\u0001\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0010\u0010\n\u001a\u0004\u0018\u00010\u00062\u0006\u0010\u000b\u001a\u00020\u0006J\b\u0010\f\u001a\u00020\rH\u0002J\u0018\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u000b\u001a\u00020\u00062\b\u0010\u0010\u001a\u0004\u0018\u00010\u0006R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082D\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u0007\u001a\n \t*\u0004\u0018\u00010\b0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0011"}, d2 = {"Lru/pay/android/data/SecureStore;", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "alias", "", "prefs", "Landroid/content/SharedPreferences;", "kotlin.jvm.PlatformType", "get", "name", "key", "Ljavax/crypto/SecretKey;", "set", "", "value", "androidApp_debug"})
public final class SecureStore {
    private final android.content.SharedPreferences prefs = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String alias = "eps-session-v1";
    
    @javax.inject.Inject()
    public SecureStore(@dagger.hilt.android.qualifiers.ApplicationContext()
    @org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super();
    }
    
    private final javax.crypto.SecretKey key() {
        return null;
    }
    
    @kotlin.jvm.Synchronized()
    @org.jetbrains.annotations.Nullable()
    public final synchronized java.lang.String get(@org.jetbrains.annotations.NotNull()
    java.lang.String name) {
        return null;
    }
    
    @kotlin.jvm.Synchronized()
    public final synchronized void set(@org.jetbrains.annotations.NotNull()
    java.lang.String name, @org.jetbrains.annotations.Nullable()
    java.lang.String value) {
    }
}