package com.example.feelingsafe;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SensorEventListener, SOSButtonView.OnSOSListener {

    // --- Sabitler ---
    private static final int PERMISSIONS_REQUEST_CODE = 1002;
    // Hassasiyet ayarlarını buradan kolayca değiştirebilirsin
    private static final float FREEFALL_THRESHOLD = 9.0f;
    private static final float IMPACT_THRESHOLD = 11.0f; // yüksek Hassasiyet

    // --- Arayüz Elemanları ---
    private Button buttonContacts, buttonSettings;
    private TextView textViewAddress;
    private SOSButtonView sosButtonView;

    // --- Konum ve Sensör Bileşenleri ---
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private SensorManager sensorManager;
    private Sensor accelerometer;

    // --- Durum Değişkenleri ---
    private boolean isFallDetectionEnabled = false;
    private boolean inFreefall = false;
    private long freefallStartTime = 0;
    private CountDownTimer sosCountDownTimer;
    private AlertDialog countdownDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Arayüz Elemanlarını Bağlama
        buttonContacts = findViewById(R.id.buttonContacts);
        buttonSettings = findViewById(R.id.buttonSettings);
        textViewAddress = findViewById(R.id.textViewAddress);
        sosButtonView = findViewById(R.id.sosButtonView);

        // 2. Servisleri ve Yöneticileri Başlatma
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        // 3. Listener'ları ve Ayarları Yükleme
        sosButtonView.setOnSOSListener(this);
        loadFallDetectionSetting();

        buttonContacts.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ContactActivity.class);
            startActivity(intent);
        });
        buttonSettings.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(intent);
        });

        // 4. Gerekli İzinleri Kontrol Et ve İşlemleri Başlat
        checkAndRequestPermissions();
    }

    //==============================================================================================
    // Lifecycle Metodları
    //==============================================================================================

    @Override
    protected void onResume() {
        super.onResume();
        loadFallDetectionSetting();
        if (isFallDetectionEnabled && accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        if (sosCountDownTimer != null) sosCountDownTimer.cancel();
        if (countdownDialog != null && countdownDialog.isShowing()) countdownDialog.dismiss();
    }

    //==============================================================================================
    // İZİN YÖNETİMİ
    //==============================================================================================
    private void checkAndRequestPermissions() {
        String[] permissionsToRequest = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.SEND_SMS};
        List<String> permissionsNeeded = new ArrayList<>();
        for (String permission : permissionsToRequest) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(permission);
            }
        }
        if (!permissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsNeeded.toArray(new String[0]), PERMISSIONS_REQUEST_CODE);
        } else {
            getLastKnownLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                getLastKnownLocation();
            } else {
                Toast.makeText(this, "Address feature cannot work without location permission.", Toast.LENGTH_LONG).show();
                textViewAddress.setText("Location permission not granted.");
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "S.O.S. messages cannot be sent without SMS permission.", Toast.LENGTH_LONG).show();
            }
        }
    }

    //==============================================================================================
    // S.O.S. VE DÜŞME TESPİTİ MANTIĞI
    //==============================================================================================
    @Override
    public void onSOSActivated() {
        Toast.makeText(this, "S.O.S. Button Activated!", Toast.LENGTH_SHORT).show();
        triggerSmsAlert();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (isFallDetectionEnabled && event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0], y = event.values[1], z = event.values[2];
            double magnitude = Math.sqrt(x * x + y * y + z * z);
            if (magnitude < FREEFALL_THRESHOLD) {
                inFreefall = true;
                freefallStartTime = System.currentTimeMillis();
            }
            if (inFreefall && magnitude > IMPACT_THRESHOLD) {
                if (System.currentTimeMillis() - freefallStartTime < 1000) {
                    startSosCountdown();
                }
                inFreefall = false;
            }
            if (inFreefall && (System.currentTimeMillis() - freefallStartTime > 1000)) {
                inFreefall = false;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    // HATA DÜZELTMESİNİN YAPILDIĞI METOD BURASI
    private void startSosCountdown() {
        // Eğer zaten bir geri sayım varsa, yenisini başlatma
        if (countdownDialog != null && countdownDialog.isShowing()) {
            return;
        }

        // 1. Yeni özel arayüzümüzü (dialog_countdown.xml) yüklüyoruz
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_countdown, null);
        // 2. Arayüzün içindeki TextView'ı buluyoruz
        final TextView countdownTextView = dialogView.findViewById(R.id.textViewCountdown);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Fall Detected!");
        // 3. Standart mesaj yerine kendi özel arayüzümüzü ayarlıyoruz
        builder.setView(dialogView);
        builder.setCancelable(false);
        builder.setNegativeButton("CANCEL S.O.S.", (dialog, which) -> {
            if (sosCountDownTimer != null) {
                sosCountDownTimer.cancel();
            }
            Toast.makeText(this, "S.O.S. cancelled.", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        countdownDialog = builder.create();
        countdownDialog.show();

        // 4. Geri sayımı 15000ms (15sn) yerine 10000ms (10sn) olarak güncelliyoruz
        sosCountDownTimer = new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // 5. Artık kendi TextView'ımızı güvenilir bir şekilde güncelleyebiliriz
                int secondsLeft = (int) (millisUntilFinished / 1000);
                countdownTextView.setText("S.O.S. message will be sent in " + secondsLeft + " seconds.");
            }

            @Override
            public void onFinish() {
                if (countdownDialog.isShowing()) {
                    countdownDialog.dismiss();
                }
                Toast.makeText(MainActivity.this, "Time is up! Sending S.O.S...", Toast.LENGTH_LONG).show();
                triggerSmsAlert();
            }
        }.start();
    }

    private void triggerSmsAlert() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, getString(R.string.sms_permission_denied), Toast.LENGTH_LONG).show();
            checkAndRequestPermissions();
            return;
        }

        SharedPreferences prefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String json = prefs.getString("contacts", null);
        Type type = new TypeToken<ArrayList<Contact>>() {}.getType();
        List<Contact> contacts = new Gson().fromJson(json, type);

        if (contacts == null || contacts.isEmpty()) {
            Toast.makeText(this, getString(R.string.no_emergency_contacts), Toast.LENGTH_LONG).show();
            return;
        }

        // DÜZELTME BURADA: Varsayılan mesaj artık var olmayan değişkenden değil,
        // doğrudan strings.xml dosyasından okunuyor.
        String customMessage = prefs.getString(SettingActivity.CUSTOM_MESSAGE_KEY, getString(R.string.default_sos_message_value));
        String locationInfo = textViewAddress.getText().toString();
        String finalMessage = customMessage + "\n" + locationInfo;

        try {
            SmsManager smsManager = SmsManager.getDefault();
            ArrayList<String> messageParts = smsManager.divideMessage(finalMessage);
            for (Contact contact : contacts) {
                smsManager.sendMultipartTextMessage(contact.getPhone(), null, messageParts, null, null);
            }
            Toast.makeText(this, getString(R.string.sms_sending_auto), Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, SirenActivity.class));
        } catch (Exception e) {
            String errorMessage = String.format(getString(R.string.sms_send_error), e.getMessage());
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    //==============================================================================================
    // AYARLARI YÜKLEME
    //==============================================================================================
    private void loadFallDetectionSetting() {
        isFallDetectionEnabled = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE).getBoolean(SettingActivity.FALL_DETECTION_KEY, false);
    }

    //==============================================================================================
    // KONUM METODLARI
    //==============================================================================================
    @SuppressLint("MissingPermission")
    private void getLastKnownLocation() {
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                getAddressFromLocation(location);
            } else {
                requestNewLocationData();
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {
        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY, 60000)
                .setMinUpdateIntervalMillis(30000).setMinUpdateDistanceMeters(50).build();
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                Location lastLocation = locationResult.getLastLocation();
                if (lastLocation != null) {
                    getAddressFromLocation(lastLocation);
                    if (fusedLocationProviderClient != null && locationCallback != null) {
                        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
                    }
                }
            }
        };
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private void getAddressFromLocation(Location location) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null && !addresses.isEmpty()) {
                textViewAddress.setText(addresses.get(0).getAddressLine(0));
            } else {
                textViewAddress.setText("Address not found for this location.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            textViewAddress.setText("Address lookup service is unavailable.");
        }
    }
}