package com.example.feelingsafe;

import android.content.Context;
import android.graphics.Color;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

/**
 * SirenActivity, acil durum anında görsel ve işitsel uyarılar üreten tam ekran bir aktivitedir.
 */
public class SirenActivity extends AppCompatActivity {

    // --- Arayüz ve Efektler için Değişkenler ---
    private View rootLayout; // Arkaplan rengini değiştirmek için layout referansı
    private MediaPlayer mediaPlayer; // Siren sesini çalmak için
    private Handler flashHandler = new Handler(Looper.getMainLooper()); // Flaş zamanlaması için
    private Handler screenHandler = new Handler(Looper.getMainLooper()); // Ekran yanıp sönme zamanlaması için
    private CameraManager cameraManager; // Cihazın kamerasına (ve flaşına) erişim için
    private String cameraId; // Kullanılacak kamera ID'si (genellikle arka kamera)
    private boolean isScreenRed = false; // Ekran rengini değiştirmek için durum değişkeni

    // S.O.S. Mors Alfabesi Zamanlaması (milisaniye cinsinden)
    // S (...): 3 kısa sinyal
    // O (---): 3 uzun sinyal
    private final int DOT = 150;                  // Kısa sinyal (nokta) süresi
    private final int DASH = 450;                 // Uzun sinyal (çizgi) süresi
    private final int GAP_BETWEEN_SIGNALS = 150;  // Aynı harfin sinyalleri arasındaki boşluk
    private final int GAP_BETWEEN_LETTERS = 450;  // Harfler arasındaki boşluk
    private final int GAP_BETWEEN_WORDS = 1000;   // Kelimeler arasındaki boşluk (S-O-S döngüsü için)


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_siren);

        // Bu aktivitenin ekranı sürekli açık tutmasını, tam ekran olmasını ve
        // telefon kilitliyken bile kilit ekranının üzerinde görünmesini sağlıyoruz.
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_FULLSCREEN |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        // Arayüz elemanlarını bağlıyoruz
        rootLayout = findViewById(R.id.root_layout_siren);
        Button stopButton = findViewById(R.id.buttonStopSiren);

        // Flaşa erişim için CameraManager'ı başlatıyoruz
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            // Cihazdaki ilk kameranın (genellikle arka kamera) ID'sini alıyoruz
            cameraId = cameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace(); // Hata olursa log'a yazdır
        }

        // Durdur butonuna tıklandığında tüm efektleri durdur ve ekranı kapat
        stopButton.setOnClickListener(v -> stopAllEffectsAndFinish());
    }

    /**
     * Aktivite görünür hale geldiğinde tüm efektleri başlatır.
     */
    @Override
    protected void onResume() {
        super.onResume();
        startScreenFlashing();
        startSirenSound();
        startSosFlashlight();
    }

    /**
     * Aktivite arkaplana gittiğinde veya kapatıldığında tüm efektleri durdurur.
     * Bu, pil tasarrufu ve kaynakların serbest bırakılması için çok önemlidir.
     */
    @Override
    protected void onPause() {
        super.onPause();
        stopAllEffectsAndFinish();
    }

    // --- Efektleri Başlatan Metodlar ---

    private void startScreenFlashing() {
        screenRunnable.run(); // Ekran yanıp sönme döngüsünü başlat
    }

    private void startSirenSound() {
        if (mediaPlayer == null) {
            // ses dosyasını res/raw klasöründen yüklüyoruz
            mediaPlayer = MediaPlayer.create(this, R.raw.siren_sound);
            mediaPlayer.setLooping(true); // Sesin sürekli çalması için döngüye alıyoruz
        }
        mediaPlayer.start(); // Sesi başlat
    }

    private void startSosFlashlight() {
        if (cameraId != null) {
            flashHandler.post(sosRunnable); // Flaş S.O.S. döngüsünü başlat
        }
    }

    // --- Efektleri Durduran Metodlar ---

    /**
     * Tüm zamanlayıcıları, sesleri ve flaşı durdurup aktiviteyi kapatan merkezi metod.
     */
    private void stopAllEffectsAndFinish() {
        // Beklemedeki tüm Handler görevlerini (yanıp sönme ve S.O.S.) iptal et
        screenHandler.removeCallbacks(screenRunnable);
        flashHandler.removeCallbacks(sosRunnable);

        // Medya oynatıcıyı durdur ve kaynakları serbest bırak
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
        // Flaşın açık kalma ihtimaline karşı kapat
        turnFlash(false);

        // Aktiviteyi kapat ve bir önceki ekrana dön
        finish();
    }

    // --- Runnable (Zamanlanmış Görev) Nesneleri ---

    /**
     * Ekranın rengini periyodik olarak değiştiren zamanlanmış görev.
     */
    private Runnable screenRunnable = new Runnable() {
        @Override
        public void run() {
            rootLayout.setBackgroundColor(isScreenRed ? Color.BLUE : Color.RED);
            isScreenRed = !isScreenRed; // Rengi tersine çevir (kırmızıysa mavi, maviyse kırmızı yap)
            screenHandler.postDelayed(this, 300); // 300 milisaniye sonra bu görevi tekrar çalıştır
        }
    };

    /**
     * S.O.S. mors alfabesi dizisini başlatan ana zamanlanmış görev.
     */
    private Runnable sosRunnable = new Runnable() {
        @Override
        public void run() {
            // S (... --- ...) harflerinin zamanlama dizisini başlat
            flashSequence(new int[]{DOT, DOT, DOT, DASH, DASH, DASH, DOT, DOT, DOT});
        }
    };

    /**
     * Verilen zamanlama dizisine göre flaşı açıp kapatarak mors alfabesini oluşturur.
     * @param pattern Milisaniye cinsinden sinyal sürelerini içeren bir dizi (örn: {150, 150, 150, 450, ...})
     */
    private void flashSequence(int[] pattern) {
        long delay = 0; // Toplam gecikme süresi
        for (int i = 0; i < pattern.length; i++) {
            // Flaş AÇ komutunu zamanla
            flashHandler.postDelayed(() -> turnFlash(true), delay);
            delay += pattern[i]; // Gecikmeye sinyal süresini ekle
            // Flaş KAPAT komutunu zamanla
            flashHandler.postDelayed(() -> turnFlash(false), delay);
            // Sinyalden sonraki boşluk süresini ekle (her 3 sinyalde bir harf arası boşluk)
            delay += (i % 3 == 2) ? GAP_BETWEEN_LETTERS : GAP_BETWEEN_SIGNALS;
        }
        // Tüm S-O-S dizisi bittikten sonra, kelime arası boşluk verip döngüyü yeniden başlat
        flashHandler.postDelayed(sosRunnable, delay + GAP_BETWEEN_WORDS);
    }

    /**
     * Flaş ışığını açıp kapatan yardımcı metod.
     * @param on true ise flaşı açar, false ise kapatır.
     */
    private void turnFlash(boolean on) {
        try {
            if (cameraId != null) {
                cameraManager.setTorchMode(cameraId, on);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
}