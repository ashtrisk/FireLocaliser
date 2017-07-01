package com.appfest.firefeeder;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "MainActivity";
    private static final String KEY_SAY_HELLO = "hello";
    private static final String VALUE_SAY_HELLO_DEFAULT = "Hello World";

    private TextView txtHelloWorld;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    private ConfigLocaliser configLocaliser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        txtHelloWorld = (TextView) findViewById(R.id.txt_hello);
        Button sayHello = (Button) findViewById(R.id.btn_say_hello);
        sayHello.setOnClickListener(getSayHelloListener());

        configLocaliser = new ConfigLocaliser();

        initConfig();
    }

    private void initConfig() {
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);

        // Define default config values. Defaults are used when configFetched config values are not
        // available. Eg: if an error occurred fetching values from the server.
        Map<String, Object> defaultConfigMap = new HashMap<>();
        defaultConfigMap.put(KEY_SAY_HELLO, VALUE_SAY_HELLO_DEFAULT);
        mFirebaseRemoteConfig.setDefaults(defaultConfigMap);

    }

    private View.OnClickListener getSayHelloListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                configLocaliser.fetch(mFirebaseRemoteConfig, KEY_SAY_HELLO, new ConfigLocaliser.FetchSuccessListener() {
                    @Override
                    public void fetchSuccess(String value) {
                        setSayHelloTxt(value);
                    }
                });
            }
        };
    }

    private void setSayHelloTxt(String valueSayHello) {

        Log.d(LOG_TAG, KEY_SAY_HELLO + " = " + valueSayHello);

        if(valueSayHello != null && !valueSayHello.isEmpty()) {
            txtHelloWorld.setText(valueSayHello);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        configLocaliser.onConfigChanged(newConfig);
    }

}
