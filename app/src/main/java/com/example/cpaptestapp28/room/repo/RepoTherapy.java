package com.example.cpaptestapp28.room.repo;

import android.content.Context;

import com.example.cpaptestapp28.room.dao.DaoTherapy;
import com.example.cpaptestapp28.room.db.CpapDB;
import com.example.cpaptestapp28.room.model.Therapy;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class RepoTherapy {
    private DaoTherapy daoTherapy;

    public RepoTherapy(Context context) {
        CpapDB cpapDB = CpapDB.getInstance(context);
        daoTherapy = cpapDB.daoTherapy();
    }

    public void insert(Therapy therapy) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                daoTherapy.insert(therapy);
            }
        });
    }

    public Therapy getTherapyByID(int id) throws ExecutionException, InterruptedException {
        Callable<Therapy> callable = new Callable<Therapy>() {
            @Override
            public Therapy call() throws Exception {
                return daoTherapy.getTherapyByID(id);
            }
        };
        Future<Therapy> future = Executors.newSingleThreadExecutor().submit(callable);
        return future.get();
    }
}
