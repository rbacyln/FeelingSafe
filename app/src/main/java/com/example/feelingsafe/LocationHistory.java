package com.example.feelingsafe;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

// @Entity işareti, bu sınıfın bir veritabanı tablosu olduğunu Room'a söyler.
@Entity(tableName = "location_history")
public class LocationHistory {

    // @PrimaryKey, bu alanın her satır için benzersiz bir anahtar olduğunu belirtir.
    // autoGenerate = true, her yeni kayıtta ID'nin otomatik olarak artmasını sağlar.
    @PrimaryKey(autoGenerate = true)
    public int id;

    // @ColumnInfo, bu alanın tablodaki bir sütuna karşılık geldiğini belirtir.
    @ColumnInfo(name = "latitude")
    public double latitude;

    @ColumnInfo(name = "longitude")
    public double longitude;

    @ColumnInfo(name = "timestamp")
    public long timestamp;

    // Room'un nesneleri oluşturabilmesi için boş bir kurucu metod gerekebilir.
    public LocationHistory(double latitude, double longitude, long timestamp) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
    }
}