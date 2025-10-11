package com.example.feelingsafe;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LocationTrackingService extends Service {

    private static final String CHANNEL_ID = "LocationTrackingChannel";
    private static final int NOTIFICATION_ID = 12345;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private AppDatabase database;
    // Arka planda veritabanı işlemi yapmak için bir ExecutorService
    private ExecutorService databaseExecutor;

    @Override
    public void onCreate() {
        super.onCreate();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        databaseExecutor = Executors.newSingleThreadExecutor();
        database = AppDatabase.getInstance(getApplicationContext()); // Veritabanı örneğini alıyoruz

        // Konum her güncellendiğinde ne olacağını tanımlıyoruz
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        Log.d("LocationService", "New location added: " + location.getLatitude() + ", " + location.getLongitude());

                        // Yeni bir konum geçmişi nesnesi oluştur
                        LocationHistory newLocation = new LocationHistory(
                                location.getLatitude(),
                                location.getLongitude(),
                                System.currentTimeMillis()
                        );

                        // Veritabanına kaydetme işlemini ana iş parçacığı dışında yap
                        databaseExecutor.execute(() -> {
                            database.locationHistoryDao().insert(newLocation);
                        });
                    }
                }
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Servisi bir Foreground Service'e dönüştürüyoruz
        createNotificationChannel();
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Location Tracking Active")
                .setContentText("FeelingSafe app is logging your route.")
                .setSmallIcon(R.drawable.ic_launcher_foreground) // İkon dosyanızın adını kontrol edin
                .build();

        startForeground(NOTIFICATION_ID, notification);

        // Konum güncellemelerini başlat
        startLocationUpdates();

        // Sistem servisi sonlandırırsa, yeniden başlatmasını istiyoruz
        return START_STICKY;
    }

    private void startLocationUpdates() {
        // Konuştuğumuz optimize edilmiş konum isteği
        LocationRequest locationRequest = new LocationRequest.Builder(
                Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                60000) // En geç 60 saniyede bir
                .setMinUpdateIntervalMillis(30000) // En erken 30 saniyede bir
                .setMinUpdateDistanceMeters(50) // VEYA en az 50 METRE hareket ettiğinde
                .build();

        // İzin kontrolü
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Servisler izin isteyemez, bu yüzden aktivitede iznin verildiğini varsayıyoruz.
            // Bu kontrol sadece Android Studio'nun uyarısını gidermek içindir.
            return;
        }

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Location Tracking Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Servis durduğunda konum güncellemelerini de durdur
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        // Arka plan işlemcisini kapat
        databaseExecutor.shutdown();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // Bu bir "started service" olduğu için onBind'a ihtiyacımız yok.
        return null;
    }
}