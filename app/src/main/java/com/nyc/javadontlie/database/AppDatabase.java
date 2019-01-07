package com.nyc.javadontlie.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.nyc.javadontlie.moneyModel.User;
import com.nyc.javadontlie.roomDao.UserDao;

/**
 * Created by Wayne Kellman on 1/25/18.
 */

@Database(entities = {User.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
}
