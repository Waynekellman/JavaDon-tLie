package com.nyc.javadontlie.roomDao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.nyc.javadontlie.moneyModel.User;

import java.util.List;

/**
 * Created by Wayne Kellman on 1/25/18.
 */
@Dao
public interface UserDao {
    @Query("SELECT * FROM user")
    List<User> getAll();

    @Query("SELECT * FROM user WHERE userName LIKE :userName AND "
            + "password LIKE :password LIMIT 1")
    User findByLogin(String userName, String password);

    @Query("SELECT * FROM user WHERE id LIKE :userIds  LIMIT 1")
    User findById(int userIds);

    @Insert
    void insertAll(User... user);

    @Update
    void updateUsers(User... users);

    @Delete
    void delete(User user);

}
