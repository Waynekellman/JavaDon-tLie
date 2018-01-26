package com.nyc.javadontlie.moneyModel;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by Wayne Kellman on 1/25/18.
 */
@Entity
public class User {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "userName")
    public String userName;

    @ColumnInfo(name = "password")
    public String password;

    public User(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public User() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Getters and setters are ignored for brevity,
    // but they're required for Room to work.
}
