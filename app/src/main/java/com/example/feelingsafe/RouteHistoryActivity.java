package com.example.feelingsafe;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * TR: RouteHistoryActivity, Room veritabanına kaydedilmiş olan konum geçmişini
 * TR: bir liste halinde kullanıcıya gösteren ekrandır.
 * EN: RouteHistoryActivity is the screen that displays the location history,
 * EN: which was saved to the Room database, to the user in a list format.
 */
public class RouteHistoryActivity extends AppCompatActivity {

    // --- Arayüz Elemanları ve Liste Değişkenleri ---
    // --- UI Elements and List Variables ---
    private RecyclerView recyclerView;          // TR: Konum kayıtlarını gösterecek olan liste. / EN: The list that will display the location records.
    private RouteHistoryAdapter adapter;        // TR: Verileri RecyclerView'a bağlayan adaptör. / EN: The adapter that binds the data to the RecyclerView.
    private List<LocationHistory> historyList = new ArrayList<>(); // TR: Veritabanından çekilen konumların tutulacağı liste. / EN: The list that will hold the locations fetched from the database.

    // --- Veritabanı Bileşenleri ---
    // --- Database Components ---
    private AppDatabase database;               // TR: Room veritabanımızın ana erişim nesnesi. / EN: The main access object for our Room database.
    private ExecutorService databaseExecutor;   // TR: Veritabanı işlemlerini arkaplan thread'inde çalıştırmak için. / EN: For running database operations on a background thread.

    /**
     * TR: Aktivite ilk oluşturulduğunda çağrılır.
     * EN: Called when the activity is first created.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TR: Bu aktivitenin arayüzünü activity_route_history.xml dosyasından yüklüyoruz.
        // EN: We are loading the UI for this activity from the activity_route_history.xml file.
        setContentView(R.layout.activity_route_history);

        // TR: XML'deki RecyclerView'ı Java koduna bağlıyoruz.
        // EN: We connect the RecyclerView from the XML to the Java code.
        recyclerView = findViewById(R.id.recyclerViewRouteHistory);

        // TR: Arkaplanda tek bir işlem yapacak olan thread havuzumuzu oluşturuyoruz.
        // EN: We are creating our thread pool that will execute a single task in the background.
        databaseExecutor = Executors.newSingleThreadExecutor();

        // TR: Veritabanımızın tekil örneğini (singleton instance) alıyoruz.
        // EN: We are getting the singleton instance of our database.
        database = AppDatabase.getInstance(getApplicationContext());

        // TR: Adaptörümüzü boş bir liste ile başlatıyoruz. Veriler daha sonra yüklenecek.
        // EN: We initialize our adapter with an empty list. The data will be loaded later.
        adapter = new RouteHistoryAdapter(historyList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // TR: Veritabanından konum geçmişini yüklemek için metodumuzu çağırıyoruz.
        // EN: We call our method to load the location history from the database.
        loadLocationHistory();
    }

    /**
     * TR: Veritabanından tüm konum kayıtlarını arkaplanda çeker ve sonucu arayüzde gösterir.
     * EN: Fetches all location records from the database in the background and displays the result on the UI.
     */
    private void loadLocationHistory() {
        // TR: databaseExecutor.execute(), içine yazılan kodun arkaplan thread'inde çalışmasını sağlar.
        // TR: Veritabanı işlemleri gibi uzun sürebilecek işlemlerin ana arayüz thread'ini (UI Thread)
        // TR: kilitlemesini ve uygulamanın donmasını engellemek için bu zorunludur.
        // EN: databaseExecutor.execute() ensures that the code written inside it runs on a background thread.
        // EN: This is mandatory to prevent long-running operations like database queries from blocking
        // EN: the main UI thread and causing the application to freeze (ANR).
        databaseExecutor.execute(() -> {
            // TR: Arkaplan thread'i üzerinde: Veritabanından tüm geçmişi DAO aracılığıyla alıyoruz.
            // EN: On the background thread: We get all history from the database via the DAO.
            final List<LocationHistory> loadedHistory = database.locationHistoryDao().getAll();

            // TR: Arayüz elemanları sadece ana thread'den güncellenebilir.
            // TR: Bu yüzden, aldığımız verileri listeye eklemek için runOnUiThread ile ana thread'e geri dönüyoruz.
            // EN: UI elements can only be updated from the main thread.
            // EN: Therefore, we return to the main thread using runOnUiThread to add the data we fetched to the list.
            runOnUiThread(() -> {
                historyList.clear(); // TR: Önceki verileri temizle. / EN: Clear previous data.
                historyList.addAll(loadedHistory); // TR: Yeni verileri ekle. / EN: Add the new data.
                adapter.notifyDataSetChanged(); // TR: Adaptöre verilerin değiştiğini ve listeyi yenilemesi gerektiğini bildir. / EN: Notify the adapter that the data has changed and it should refresh the list.
            });
        });
    }
}