package com.example.feelingsafe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * TR: RouteHistoryAdapter, veritabanından gelen konum geçmişi listesini (List<LocationHistory>) alır
 * TR: ve her bir konumu RecyclerView içinde bir satır olarak nasıl göstereceğini yönetir.
 * EN: RouteHistoryAdapter takes the location history list (List<LocationHistory>) from the database
 * EN: and manages how to display each location as a row in the RecyclerView.
 */
public class RouteHistoryAdapter extends RecyclerView.Adapter<RouteHistoryAdapter.RouteViewHolder> {

    // TR: Adaptörün üzerinde çalışacağı veri listesi.
    // EN: The data list that the adapter will work on.
    private List<LocationHistory> historyList;

    /**
     * TR: RouteHistoryAdapter'ın kurucu metodu (Constructor).
     * TR: Bu adaptörü oluşturan aktivite (RouteHistoryActivity), ona göstereceği veri listesini bu metod aracılığıyla verir.
     * EN: The constructor for the RouteHistoryAdapter.
     * EN: The activity that creates this adapter (RouteHistoryActivity) provides the data list to display through this method.
     */
    public RouteHistoryAdapter(List<LocationHistory> historyList) {
        this.historyList = historyList;
    }

    /**
     * TR: RecyclerView, ekranda gösterilecek yeni bir satıra ihtiyaç duyduğunda bu metod çağrılır.
     * TR: Bu metod, satırın arayüzünü (route_list_item.xml) oluşturur ve onu bir ViewHolder içinde geri döndürür.
     * EN: This method is called when the RecyclerView needs a new row to display on the screen.
     * EN: This method creates the layout for the row (route_list_item.xml) and returns it inside a ViewHolder.
     */
    @NonNull
    @Override
    public RouteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // TR: LayoutInflater, XML layout dosyasını bir View (Görünüm) nesnesine dönüştürür.
        // EN: LayoutInflater "inflates" an XML layout file into a View object.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.route_list_item, parent, false);
        return new RouteViewHolder(view);
    }

    /**
     * TR: RecyclerView, belirli bir pozisyondaki satırı ilgili veriyle doldurmak istediğinde bu metod çağrılır.
     * TR: Bu metod, scrolling (kaydırma) sırasında sürekli olarak çağrılır.
     * EN: This method is called when the RecyclerView wants to populate a row at a specific position with the relevant data.
     * EN: This method is called continuously during scrolling.
     */
    @Override
    public void onBindViewHolder(@NonNull RouteViewHolder holder, int position) {
        // TR: Listeden o anki pozisyona denk gelen konum verisini alıyoruz.
        // EN: We get the location data object from the list that corresponds to the current position.
        LocationHistory historyItem = historyList.get(position);

        // TR: Enlem ve boylamı formatlayarak okunabilir bir metin haline getiriyoruz.
        // EN: We format the latitude and longitude into a readable string.
        String coordinates = String.format(Locale.US, "Coordinates: %.5f, %.5f", historyItem.latitude, historyItem.longitude);
        holder.coordinatesTextView.setText(coordinates);

        // TR: Veritabanında long olarak tutulan zaman damgasını (timestamp) okunabilir bir tarih formatına (dd-MM-yyyy HH:mm:ss) çeviriyoruz.
        // EN: We convert the timestamp, which is stored as a long in the database, into a readable date format (dd-MM-yyyy HH:mm:ss).
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        String dateString = sdf.format(new Date(historyItem.timestamp));
        holder.timestampTextView.setText("Time: " + dateString);
    }

    /**
     * TR: RecyclerView'a listede toplam kaç tane öğe olduğunu bildirir.
     * EN: Tells the RecyclerView the total number of items in the list.
     */
    @Override
    public int getItemCount() {
        return historyList.size();
    }

    /**
     * TR: RouteViewHolder, RecyclerView'daki tek bir satırın arayüz elemanlarını (TextView'lar) hafızada tutan bir önbellek (cache) görevi görür.
     * TR: Bu "ViewHolder Deseni", her satır için sürekli findViewById yapmayı engelleyerek performansı (özellikle kaydırma akıcılığını) ciddi şekilde artırır.
     * EN: RouteViewHolder acts as a cache that holds the UI elements (TextViews) of a single row in the RecyclerView.
     * EN: This "ViewHolder Pattern" significantly improves performance (especially scrolling smoothness) by preventing repeated calls to findViewById() for each row.
     */
    class RouteViewHolder extends RecyclerView.ViewHolder {
        TextView coordinatesTextView;
        TextView timestampTextView;

        public RouteViewHolder(@NonNull View itemView) {
            super(itemView);
            // TR: Satırın içindeki TextView'ları bir kere bulup değişkenlere atıyoruz.
            // EN: We find the TextViews inside the row once and assign them to variables.
            coordinatesTextView = itemView.findViewById(R.id.textViewCoordinates);
            timestampTextView = itemView.findViewById(R.id.textViewTimestamp);
        }
    }
}