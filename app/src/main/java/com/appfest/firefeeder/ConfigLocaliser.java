package com.appfest.firefeeder;

import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.Locale;

/**
 * Created by Vostro-Daily on 6/21/2017.
 *
 * Class used to localise String data in remote config
 * It is used to fetch values from server
 * Steps :
 * 1. Initialise in onCreate
 * 2. call onConfigChanged() in Activity's onConfigurationChanged()
 */

public class ConfigLocaliser {

    private static final String LOG_TAG = "ConfigLocaliser";
    private String locale = "en";
    private String separator = "_";

    public ConfigLocaliser(){
        locale = Locale.getDefault().getLanguage();
        Log.d(LOG_TAG, "Locale:" + locale);
    }

    public void fetch(final FirebaseRemoteConfig mFirebaseRemoteConfig, final String configKey, final FetchSuccessListener fetchSuccessListener) {
        if (mFirebaseRemoteConfig == null) {
            return ;
        }
        Log.d(LOG_TAG, "fetching");
        long cacheExpiration = 3600; // 1 hour in seconds
        // If developer mode is enabled reduce cacheExpiration to 0 so that each fetch goes to the
        // server. This should not be used in release builds.
        if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }
        mFirebaseRemoteConfig.fetch(cacheExpiration)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mFirebaseRemoteConfig.activateFetched();

                        String key = buildKey(configKey);
                        String value = mFirebaseRemoteConfig.getString(key);
                        Log.d(LOG_TAG, "onSuccess:" + key + ":" + value);

                        if (fetchSuccessListener != null) {
                            fetchSuccessListener.fetchSuccess(value);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        String key = buildKey(configKey);
                        String value = mFirebaseRemoteConfig.getString(key);
                        Log.d(LOG_TAG, "onFailure:" + key + ":" + value);

                        if (fetchSuccessListener != null) {
                            fetchSuccessListener.fetchSuccess(value);
                        }
                    }
                });
    }

    public void onConfigChanged(Configuration newConfig){
        locale = newConfig.locale.getLanguage();
        Log.d(LOG_TAG, "onConfigChanged:" + locale);
    }

    private String buildKey(String baseKey){
        return baseKey + separator + locale;
    }

    public void setSeparator(String separator){
        this.separator = separator;
    }

    public interface FetchSuccessListener {
        void fetchSuccess(String value);
    }

}
