package com.example.feelingsafe;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.Nullable;

/**
 * TR: SOSButtonView, Android'in standart View sınıfını genişleterek oluşturduğumuz özel bir arayüz bileşenidir.
 * TR: Amacı, basılı tutulduğunda aktif olan dairesel bir S.O.S. butonu oluşturmaktır.
 * EN: SOSButtonView is a custom UI component we created by extending Android's standard View class.
 * EN: Its purpose is to create a circular S.O.S. button that activates on press-and-hold.
 */
public class SOSButtonView extends View {

    /**
     * TR: Bu arayüz, bu özel butonun dış dünya ile (MainActivity ile) iletişim kurmasını sağlar.
     * TR: Buton 3 saniye basılı tutulduğunda bu arayüz üzerinden haber verilir. Buna "callback listener" denir.
     * EN: This interface allows this custom button to communicate with the outside world (with MainActivity).
     * EN: It gives a notification through this interface when the button is held for 3 seconds. This is called a "callback listener".
     */
    public interface OnSOSListener {
        void onSOSActivated();
    }

    private OnSOSListener sosListener; // TR: MainActivity'den gelen listener nesnesini tutar. / EN: Holds the listener object from MainActivity.
    private Paint circlePaint, progressPaint, textPaint; // TR: Çizim için kullanacağımız "fırçalar". / EN: The "brushes" we will use for drawing.
    private RectF progressRect; // TR: Dairesel ilerleme çubuğunun sınırlarını belirleyen dikdörtgen. / EN: The rectangle that defines the bounds of the circular progress bar.
    private CountDownTimer countDownTimer; // TR: 3 saniyelik basılı tutma süresini yöneten sayaç. / EN: The timer that manages the 3-second press-and-hold duration.
    private float progress = 0f; // TR: İlerleme çubuğunun ne kadar dolduğunu tutan değişken (0.0 ile 1.0 arası). / EN: The variable that holds how full the progress bar is (between 0.0 and 1.0).

    public SOSButtonView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(); // TR: Başlangıç ayarlarını yap. / EN: Do the initial setup.
    }

    /**
     * TR: Bu metod, buton ilk oluşturulduğunda bir kereliğine çalışır.
     * TR: Tüm çizim nesnelerini ve sayacı burada önceden hazırlayarak performansı artırırız.
     * EN: This method runs once when the button is first created.
     * EN: We improve performance by preparing all drawing objects and the timer here beforehand.
     */
    private void init() {
        // TR: Çizim fırçalarını ve renklerini hazırlıyoruz. ANTI_ALIAS_FLAG, çizimlerin kenarlarını pürüzsüzleştirir.
        // EN: We prepare the drawing brushes and their colors. ANTI_ALIAS_FLAG smoothens the edges of the drawings.
        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(Color.parseColor("#D32F2F")); // Koyu kırmızı

        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setColor(Color.parseColor("#FFC107")); // Parlak kırmızı
        progressPaint.setStyle(Paint.Style.STROKE); // Sadece dış çizgiyi çiz
        progressPaint.setStrokeWidth(20f); // Çizgi kalınlığı

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(120f);
        textPaint.setTextAlign(Paint.Align.CENTER); // Metni yatayda ortala

        progressRect = new RectF();

        // TR: 3 saniyelik (3000ms) geri sayım sayacını tanımlıyoruz. Her 30ms'de bir onTick çalışır.
        // EN: We define the 3-second (3000ms) countdown timer. onTick runs every 30ms.
        countDownTimer = new CountDownTimer(3000, 30) {
            /**
             * TR: Sayaç çalışırken periyodik olarak tetiklenir.
             * EN: Triggered periodically while the timer is running.
             */
            @Override
            public void onTick(long millisUntilFinished) {
                progress = (3000 - millisUntilFinished) / 3000f; // TR: İlerleme oranını hesapla (0.0 -> 1.0). / EN: Calculate the progress ratio (0.0 -> 1.0).
                invalidate(); // TR: Android sistemine "Görünüm değişti, ekranı yeniden çiz" komutunu verir. Bu, onDraw'u tetikler. / EN: Tells the Android system "The view has changed, redraw the screen". This triggers onDraw.
            }

            /**
             * TR: Sayaç bittiğinde (3 saniye dolduğunda) tetiklenir.
             * EN: Triggered when the timer finishes (when 3 seconds are up).
             */
            @Override
            public void onFinish() {
                progress = 1f;
                // TR: Eğer bir listener varsa (MainActivity kendini kaydettirdiyse), ona haber ver.
                // EN: If there is a listener (if MainActivity registered itself), notify it.
                if (sosListener != null) {
                    sosListener.onSOSActivated();
                }
                vibrate(); // TR: Cihazı titret. / EN: Vibrate the device.
                // TR: Butonun görselini 1 saniye sonra sıfırla.
                // EN: Reset the button's visual state after 1 second.
                postDelayed(() -> {
                    progress = 0f;
                    invalidate();
                }, 1000);
            }
        };
    }

    /**
     * TR: Bu metod, butonun ekrana çizildiği yerdir. Tüm görsel mantık buradadır.
     * EN: This method is where the button is drawn on the screen. All visual logic is here.
     * @param canvas TR: Üzerine çizim yapacağımız tuval. / EN: The canvas on which we will draw.
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        int radius = Math.min(width, height) / 2;

        // TR: Arkaplan dairesini çiz.
        // EN: Draw the background circle.
        canvas.drawCircle(width / 2f, height / 2f, radius, circlePaint);

        // TR: Dairesel ilerleme çubuğunu (arc) çiz. Açı, progress değişkenine göre 0'dan 360'a kadar artar.
        // EN: Draw the circular progress bar (arc). The angle increases from 0 to 360 based on the progress variable.
        progressRect.set(width / 2f - radius + 10f, height / 2f - radius + 10f, width / 2f + radius - 10f, height / 2f + radius - 10f);
        canvas.drawArc(progressRect, -90, 360 * progress, false, progressPaint);

        // TR: "S.O.S" metnini tam ortaya çiz.
        // EN: Draw the "S.O.S" text right in the center.
        float textY = height / 2f - ((textPaint.descent() + textPaint.ascent()) / 2);
        canvas.drawText("S.O.S", width / 2f, textY, textPaint);
    }

    /**
     * TR: Kullanıcının butona dokunma eylemlerini yönetir.
     * EN: Manages the user's touch actions on the button.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: // TR: Kullanıcı parmağını butona bastığında. / EN: When the user presses their finger on the button.
                countDownTimer.start(); // TR: Geri sayımı başlat. / EN: Start the countdown.
                return true;
            case MotionEvent.ACTION_UP:   // TR: Kullanıcı parmağını kaldırdığında. / EN: When the user lifts their finger.
            case MotionEvent.ACTION_CANCEL: // TR: Dokunma iptal edildiğinde. / EN: When the touch is cancelled.
                countDownTimer.cancel(); // TR: Geri sayımı iptal et. / EN: Cancel the countdown.
                progress = 0;
                invalidate(); // TR: İlerleme çubuğunu sıfırla. / EN: Reset the progress bar.
                return true;
        }
        return super.onTouchEvent(event);
    }

    /**
     * TR: Cihazın titreşim servisini kullanarak kısa bir titreşim efekti oluşturur.
     * EN: Creates a short vibration effect using the device's vibrator service.
     */
    private void vibrate() {
        Vibrator v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        if (v != null) {
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        }
    }

    /**
     * TR: MainActivity'nin bu butonu dinlemesi için kendini kaydettirdiği public metod.
     * EN: The public method that MainActivity calls to register itself to listen to this button.
     */
    public void setOnSOSListener(OnSOSListener listener) {
        this.sosListener = listener;
    }
}