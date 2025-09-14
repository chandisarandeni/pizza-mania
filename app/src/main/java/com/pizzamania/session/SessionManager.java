package com.pizzamania.session;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class SessionManager {
    private static final String PREF_NAME = "user_prefs";
    private static final String KEY_EMAIL = "key_email";
    private static final String KEY_NAME = "key_name";
    private static final String KEY_PHONE = "key_phone";
    private static final String KEY_ADDRESS = "key_address";

    private static SessionManager instance;
    private final SharedPreferences prefs;

    private SessionManager(@NonNull Context context) {
        SharedPreferences sp;
        try {
            String masterKey = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            sp = EncryptedSharedPreferences.create(
                    PREF_NAME,
                    masterKey,
                    context.getApplicationContext(),
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            sp = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        }
        this.prefs = sp;
    }

    public static synchronized SessionManager getInstance(@NonNull Context context) {
        if (instance == null) instance = new SessionManager(context);
        return instance;
    }

    public void saveEmail(@NonNull String email) {
        prefs.edit().putString(KEY_EMAIL, email).apply();
    }

    //save users
        public void saveUser(@NonNull String email, @NonNull String name, @NonNull String phone, @NonNull String address) {
            prefs.edit()
                    .putString(KEY_EMAIL, email)
                    .putString(KEY_NAME, name)
                    .putString(KEY_PHONE, phone)
                    .putString(KEY_ADDRESS, address)
                    .apply();
        }


    public String getEmail() {
        return prefs.getString(KEY_EMAIL, null);
    }

    public String getName() {
        return prefs.getString(KEY_NAME, null);
    }

    public String getPhone() {
        return prefs.getString(KEY_PHONE, null);
    }

    public String getAddress() {
        return prefs.getString(KEY_ADDRESS, null);
    }

    public boolean isLoggedIn() {
        return prefs.contains(KEY_EMAIL) && getEmail() != null;
    }

    public void clear() {
        prefs.edit().remove(KEY_EMAIL).apply();
    }

    public void createFakeUser() {
        saveUser(
                "guest@pizzamania.com",
                "Guest User",
                "000-000-0000",
                "123 Fake Street, Nowhere"
        );
    }
}
