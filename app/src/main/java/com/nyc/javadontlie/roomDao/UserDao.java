package com.nyc.javadontlie.roomDao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.nyc.javadontlie.moneyModel.User;

import java.util.List;

/**
 * Created by Wayne Kellman on 1/25/18.
 */
@Dao
public interface UserDao {
    @Query("SELECT * FROM user")
    List<User> getAll();

    @Query("SELECT * FROM user WHERE id IN (:userIds)")
    List<User> loadAllByIds(int[] userIds);

    @Query("SELECT * FROM user WHERE userName LIKE :userName AND "
            + "password LIKE :password LIMIT 1")
    User findByLogin(String userName, String password);

    @Insert
    void insertAll(User... user);

    @Delete
    void delete(User user);

}
