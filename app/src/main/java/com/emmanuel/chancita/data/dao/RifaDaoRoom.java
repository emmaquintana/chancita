package com.emmanuel.chancita.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.emmanuel.chancita.data.model.Rifa;
import com.emmanuel.chancita.data.model.RifaEntityRoom;

import java.util.List;

@Dao
public interface RifaDaoRoom {
    @Query("SELECT * FROM rifas")
    LiveData<List<RifaEntityRoom>> getAll();

    @Query("SELECT * FROM rifas")
    List<RifaEntityRoom> getAllSync(); // <-- para operaciones en background

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<RifaEntityRoom> rifas);

    @Query("SELECT * FROM rifas WHERE id = :rifaId LIMIT 1")
    RifaEntityRoom getById(String rifaId);
}
