package com.emmanuel.chancita.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.emmanuel.chancita.data.dao.RifaDaoRoom;
import com.emmanuel.chancita.data.model.RifaEntityRoom;
import com.emmanuel.chancita.utils.Utilidades;

@Database(entities = {RifaEntityRoom.class}, version = 2, exportSchema = false)
@TypeConverters({Utilidades.Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public abstract RifaDaoRoom rifaDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "rifas_db"
                            )
                            .fallbackToDestructiveMigration() // borra datos si cambias version
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}