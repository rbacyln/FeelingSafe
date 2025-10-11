package com.example.feelingsafe;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

// EN: This code block defines the database structure and specifies which table it will include.
// TR: Bu kod bloğu veritabanının yapısını tanımlar ve içinde hangi tabloyu barındıracağını belirtir.
@Database(entities = {LocationHistory.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    // EN: This part provides access to the DAO(Data Access Object) of the LocationHistory table. It is used for insert, delete, and query operations.
    // TR: Bu kısım LocationHistory tablosunun DAO’suna erişim sağlar. Ekleme, silme ve sorgulama işlemleri burada yapılır.
    public abstract LocationHistoryDao locationHistoryDao();

    // EN: This code block keeps only one instance of the database in memory (Singleton Pattern).
    // TR: Bu kod bloğu veritabanının hafızada yalnızca tek bir örneğinin tutulmasını sağlar (Singleton Deseni).
    private static volatile AppDatabase INSTANCE;

    // EN: This method checks if the database instance exists. If not, it safely creates it and then returns it.
    // TR: Bu metot veritabanı örneğinin var olup olmadığını kontrol eder. Yoksa güvenli bir şekilde oluşturur ve geri döndürür.
    public static AppDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    // EN: This part actually builds the database file named "sos_app_database".
                    // TR: Bu kısım "sos_app_database" isimli veritabanı dosyasını gerçekten oluşturur.
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "sos_app_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
