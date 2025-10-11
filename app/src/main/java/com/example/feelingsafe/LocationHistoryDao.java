package com.example.feelingsafe;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

// @Dao işareti, bu arayüzün veritabanı sorguları içerdiğini Room'a söyler.
@Dao
public interface LocationHistoryDao {

    // @Insert, bu metodun veritabanına yeni bir kayıt ekleyeceğini belirtir.
    @Insert
    void insert(LocationHistory locationHistory);

    // @Query, karmaşık sorgular yazmamızı sağlar. Bu sorgu, tüm konum geçmişini
    // en yeniden en eskiye doğru sıralayarak getirir.
    @Query("SELECT * FROM location_history ORDER BY timestamp DESC")
    List<LocationHistory> getAll();

    // Bu sorgu, tüm kayıtları siler.
    @Query("DELETE FROM location_history")
    void deleteAll();
}