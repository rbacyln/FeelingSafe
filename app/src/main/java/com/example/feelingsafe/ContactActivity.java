package com.example.feelingsafe;

// Gerekli Android ve Java kütüphanelerini projeye dahil ediyoruz.
// We are including the necessary Android and Java libraries in the project.
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * TR: ContactActivity, kullanıcının acil durum kişilerini yönettiği (eklediği/sildiği) ekrandır.
 * TR: ContactAdapter'dan gelen silme olaylarını dinlemek için OnContactDeleteListener arayüzünü uygular.
 * EN: ContactActivity is the screen where the user manages (adds/deletes) their emergency contacts.
 * EN: It implements the OnContactDeleteListener interface to listen for delete events from the ContactAdapter.
 */
public class ContactActivity extends AppCompatActivity implements ContactAdapter.OnContactDeleteListener {

    // --- Arayüz Elemanları ve Liste Değişkenleri ---
    // --- UI Elements and List Variables ---
    private RecyclerView recyclerView; // TR: Kişileri liste halinde gösteren arayüz elemanı. / EN: The UI element that displays contacts in a list.
    private FloatingActionButton fab;  // TR: Yeni kişi ekleme diyalogunu açan "+" butonu. / EN: The "+" button that opens the new contact dialog.
    private ContactAdapter adapter;    // TR: Veri listesi (contactList) ile RecyclerView arasında köprü kuran adaptör. / EN: The adapter that bridges the data list (contactList) and the RecyclerView.
    private List<Contact> contactList; // TR: Acil durum kişilerinin tutulduğu dinamik liste. / EN: The dynamic list that holds the emergency contacts.

    // --- Veri Saklama İçin Sabitler ---
    // --- Constants for Data Storage ---
    private static final String PREFS_NAME = "MyPrefs";       // TR: SharedPreferences dosyasının adı. / EN: The name of the SharedPreferences file.
    private static final String CONTACTS_KEY = "contacts";    // TR: Kişi listesini kaydetmek için kullanılacak anahtar. / EN: The key used to save the contact list.


    /**
     * TR: onCreate, bu aktivite ilk oluşturulduğunda çağrılır. Başlangıç ayarları burada yapılır.
     * EN: onCreate is called when this activity is first created. Initial setup is done here.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TR: Bu aktivitenin arayüzünü activity_contacts.xml dosyasından yüklüyoruz.
        // EN: We are loading the UI for this activity from the activity_contacts.xml file.
        setContentView(R.layout.activity_contacts);

        // TR: XML'deki arayüz elemanlarını ID'leri ile Java koduna bağlıyoruz.
        // EN: We are connecting the UI elements from the XML to the Java code using their IDs.
        recyclerView = findViewById(R.id.recyclerViewContacts);
        fab = findViewById(R.id.fabAddContact);

        // TR: Cihaz hafızasından kayıtlı kişileri yüklüyoruz.
        // EN: We are loading the saved contacts from the device's memory.
        loadContacts();

        // TR: Adaptörü oluşturup RecyclerView'a bağlıyoruz. `this` kelimesi, silme olaylarını bu aktivitenin dinleyeceğini belirtir.
        // EN: We create the adapter and connect it to the RecyclerView. The `this` keyword specifies that this activity will listen for delete events.
        adapter = new ContactAdapter(contactList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // TR: "+" butonuna tıklandığında showAddContactDialog metodunu çağırıyoruz.
        // EN: We call the showAddContactDialog method when the "+" button is clicked.
        fab.setOnClickListener(v -> showAddContactDialog());
    }



    /**
     * TR: Bu metod, ContactAdapter içinden bir kişi silindiğinde tetiklenir.
     * TR: Görevi, listenin son halini hafızaya kaydetmektir.
     * EN: This method is triggered from within the ContactAdapter when a contact is deleted.
     * EN: Its job is to save the final state of the list to memory.
     */
    @Override
    public void onContactDeleted() {
        saveContacts();
        Toast.makeText(this, getString(R.string.contact_deleted), Toast.LENGTH_SHORT).show();
    }




    /**
     * TR: Yeni kişi eklemek için bir pop-up diyalog (AlertDialog) gösterir.
     * EN: Displays an AlertDialog pop-up to add a new contact.
     */
    private void showAddContactDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.add_contact_dialog_title));

        // TR: Diyalogun içinde görünecek özel arayüzü (dialog_add_contact.xml) yüklüyoruz.
        // EN: We are inflating the custom layout (dialog_add_contact.xml) that will appear inside the dialog.
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_contact, null);
        final EditText nameEditText = view.findViewById(R.id.editTextContactName);
        final EditText phoneEditText = view.findViewById(R.id.editTextContactPhone);
        builder.setView(view);

        // TR: "Ekle" butonunun davranışını tanımlıyoruz.
        // EN: We are defining the behavior of the "Add" button.
        builder.setPositiveButton(getString(R.string.btn_add), (dialog, which) -> {
            String name = nameEditText.getText().toString().trim();
            String phone = phoneEditText.getText().toString().trim();

            // TR: İsim ve telefon alanlarının boş olup olmadığını kontrol ediyoruz.
            // EN: We check if the name and phone fields are empty.
            if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(phone)) {
                contactList.add(new Contact(name, phone));
                adapter.notifyItemInserted(contactList.size() - 1); // TR: Listeye yeni bir öğe eklendiğini adaptöre bildiriyoruz (animasyon için). / EN: We notify the adapter that a new item has been added to the list (for animation).
                saveContacts(); // TR: Listenin yeni halini hafızaya kaydediyoruz. / EN: We save the new state of the list to memory.
                Toast.makeText(this, getString(R.string.contact_added), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.contact_fields_cannot_be_empty), Toast.LENGTH_SHORT).show();
            }
        });
        // TR: "İptal" butonu sadece diyaloğu kapatır.
        // EN: The "Cancel" button just closes the dialog.
        builder.setNegativeButton(getString(R.string.btn_cancel), null);
        builder.create().show();
    }





    /**
     * TR: Kişi listesini (contactList) Gson kütüphanesi ile JSON formatına çevirir ve SharedPreferences'a kaydeder.
     * EN: Converts the contact list (contactList) to JSON format using the Gson library and saves it to SharedPreferences.
     */
    private void saveContacts() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(contactList); // TR: Listeyi metne (JSON) çevir. / EN: Convert the list to text (JSON).
        editor.putString(CONTACTS_KEY, json);
        editor.apply(); // TR: Değişiklikleri kaydet. / EN: Save the changes.
    }



    /**
     * TR: SharedPreferences'tan kayıtlı JSON metnini okur ve Gson ile tekrar kişi listesine (contactList) çevirir.
     * EN: Reads the saved JSON text from SharedPreferences and converts it back to a contact list (contactList) with Gson.
     */
    private void loadContacts() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(CONTACTS_KEY, null);
        Type type = new TypeToken<ArrayList<Contact>>() {}.getType(); // TR: Gson'a hangi tipte bir listeye çevireceğini söylüyoruz. / EN: We are telling Gson what type of list to convert to.
        contactList = gson.fromJson(json, type); // TR: Metni (JSON) listeye çevir. / EN: Convert the text (JSON) to a list.

        // TR: Eğer hafızada hiç kayıt yoksa (uygulama ilk kez açılıyorsa), boş bir liste oluştur.
        // EN: If there are no records in memory (if the app is opened for the first time), create an empty list.
        if (contactList == null) {
            contactList = new ArrayList<>();
        }
    }
}