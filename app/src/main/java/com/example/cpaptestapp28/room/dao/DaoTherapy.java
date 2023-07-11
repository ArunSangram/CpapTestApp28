package com.example.cpaptestapp28.room.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.cpaptestapp28.room.model.Therapy;

@Dao
public interface DaoTherapy {
    @Insert
    void insert(Therapy therapy);

    @Query("SELECT * FROM therapy WHERE id=:id LIMIT 1")
    Therapy getTherapyByID(int id);
}
