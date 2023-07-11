package com.example.cpaptestapp28.room.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.cpaptestapp28.room.dao.DaoTherapy;
import com.example.cpaptestapp28.room.model.Therapy;

@Database(entities = {Therapy.class}, version = 1)
public abstract class CpapDB extends RoomDatabase {
    private static CpapDB instance;

    public abstract DaoTherapy daoTherapy();
    public static synchronized CpapDB getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            CpapDB.class, "CPAP")
                    .fallbackToDestructiveMigration()
                    .build();

        }
        return instance;
    }
}
