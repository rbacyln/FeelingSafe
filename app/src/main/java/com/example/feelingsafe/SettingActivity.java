package com.example.feelingsafe;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;

public class SettingActivity extends AppCompatActivity {

    private EditText editTextSosMessage;
    private SwitchCompat switchFallDetection;
    private SwitchCompat switchRouteTracking;
    private Button buttonSaveSettings;
    private Button buttonShowRouteHistory;

    // Ayarları kaydetmek için kullanılacak anahtarlar (Keys)
    private static final String PREFS_NAME = "MyPrefs";
    public static final String CUSTOM_MESSAGE_KEY = "custom_message";
    public static final String FALL_DETECTION_KEY = "fall_detection_enabled";
    public static final String TRACKING_STATE_KEY = "tracking_state";
    // DÜZELTME: Hard-coded varsayılan mesaj buradan kaldırıldı, çünkü artık strings.xml'den okunuyor.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // DÜZELTME: Doğru layout dosyası çağrılıyor (activity_settings)
        setContentView(R.layout.activity_settings);

        // Arayüz elemanlarını Java koduna bağlıyoruz
        editTextSosMessage = findViewById(R.id.editTextSosMessage);
        switchFallDetection = findViewById(R.id.switchFallDetection);
        switchRouteTracking = findViewById(R.id.switchRouteTracking);
        buttonSaveSettings = findViewById(R.id.buttonSaveSettings);
        buttonShowRouteHistory = findViewById(R.id.buttonShowRouteHistory);

        // Kayıtlı ayarları yükleyip ekranda gösteriyoruz
        loadSettings();

        // Butonlara tıklama olaylarını atıyoruz
        buttonSaveSettings.setOnClickListener(v -> saveSettings());

        buttonShowRouteHistory.setOnClickListener(v -> {
            Intent intent = new Intent(SettingActivity.this, RouteHistoryActivity.class);
            startActivity(intent);
        });
    }

    private void saveSettings() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String customMessage = editTextSosMessage.getText().toString();
        boolean isFallDetectionEnabled = switchFallDetection.isChecked();
        boolean isTrackingEnabled = switchRouteTracking.isChecked();

        editor.putString(CUSTOM_MESSAGE_KEY, customMessage);
        editor.putBoolean(FALL_DETECTION_KEY, isFallDetectionEnabled);
        editor.putBoolean(TRACKING_STATE_KEY, isTrackingEnabled);
        editor.apply();

        if (isTrackingEnabled) {
            startTrackingService();
        } else {
            stopTrackingService();
        }

        Toast.makeText(this, getString(R.string.settings_saved), Toast.LENGTH_SHORT).show();
        finish();
    }

    private void loadSettings() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // DÜZELTME: Varsayılan mesaj artık R.string referansından doğru şekilde okunuyor.
        String savedMessage = sharedPreferences.getString(CUSTOM_MESSAGE_KEY, getString(R.string.default_sos_message_value));
        boolean isFallDetectionEnabled = sharedPreferences.getBoolean(FALL_DETECTION_KEY, false);
        boolean isTrackingEnabled = sharedPreferences.getBoolean(TRACKING_STATE_KEY, false);

        editTextSosMessage.setText(savedMessage);
        switchFallDetection.setChecked(isFallDetectionEnabled);
        switchRouteTracking.setChecked(isTrackingEnabled);
    }

    private void startTrackingService() {
        Intent serviceIntent = new Intent(this, LocationTrackingService.class);
        ContextCompat.startForegroundService(this, serviceIntent);
    }

    private void stopTrackingService() {
        Intent serviceIntent = new Intent(this, LocationTrackingService.class);
        stopService(serviceIntent);
    }
}