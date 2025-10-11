package com.example.feelingsafe;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

/**
 * TR: ContactAdapter, kişi listesindeki verileri alır ve bunları RecyclerView'da
 * TR: görsel olarak nasıl göstereceğini yönetir.
 * EN: ContactAdapter takes the data from the contact list and manages how to visually
 * EN: display it in the RecyclerView.
 */
public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    /**
     * TR: Adapter'ın Activity ile iletişim kurmasını sağlayan bir arayüz (interface).
     * TR: Kişi silindiğinde Activity'ye haber vermek için kullanılır.
     * EN: An interface that allows the Adapter to communicate with the Activity.
     * EN: Used to notify the Activity when a contact is deleted.
     */
    public interface OnContactDeleteListener {
        void onContactDeleted();
    }

    private List<Contact> contactList;
    private OnContactDeleteListener deleteListener;

    // TR: Kurucu metod (Constructor). Kişi listesini ve silme olaylarını dinleyecek listener'ı alır.
    // EN: The constructor. It takes the contact list and the listener for delete events.
    public ContactAdapter(List<Contact> contactList, OnContactDeleteListener deleteListener) {
        this.contactList = contactList;
        this.deleteListener = deleteListener;
    }

    /**
     * TR: RecyclerView için her bir satırın arayüzü (contact_list_item.xml) oluşturulduğunda çağrılır.
     * EN: Called when the layout for each row (contact_list_item.xml) is created for the RecyclerView.
     */
    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_list_item, parent, false);
        return new ContactViewHolder(view);
    }

    /**
     * TR: Belirli bir pozisyondaki satırı verilerle doldurmak için çağrılır.
     * TR: Silme butonunun tıklama olayı da burada tanımlanır.
     * EN: Called to populate a row at a specific position with data.
     * EN: The click event for the delete button is also defined here.
     */
    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact contact = contactList.get(position);
        holder.nameTextView.setText(contact.getName());
        holder.phoneTextView.setText(contact.getPhone());

        holder.deleteButton.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION) {
                contactList.remove(currentPosition);
                notifyDataSetChanged(); // TR: Çökmeyi önleyen en güvenli yenileme metodu. / EN: The safest refresh method to prevent crashes.

                // TR: Activity'ye haber veriyoruz. / EN: We notify the Activity.
                if (deleteListener != null) {
                    deleteListener.onContactDeleted();
                }
            }
        });
    }

    /**
     * TR: Listede toplam kaç tane öğe olduğunu döndürür.
     * EN: Returns the total number of items in the list.
     */
    @Override
    public int getItemCount() {
        return contactList.size();
    }

    /**
     * TR: RecyclerView'daki tek bir satırın arayüz elemanlarını (TextView, Button vb.) tutan sınıf.
     * EN: A class that holds the UI elements (TextView, Button, etc.) for a single row in the RecyclerView.
     */
    class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView phoneTextView;
        ImageButton deleteButton;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.textViewContactName);
            phoneTextView = itemView.findViewById(R.id.textViewContactPhone);
            deleteButton = itemView.findViewById(R.id.buttonDeleteContact);
        }
    }
}