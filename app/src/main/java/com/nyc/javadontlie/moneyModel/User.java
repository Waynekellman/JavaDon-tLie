package com.nyc.javadontlie.moneyModel;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.Update;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

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

    @ColumnInfo(name = "gameList")
    public String gameListJson;

    public User(String userName, String password) {
        this.userName = userName;
        this.password = password;
        Type type = new TypeToken<List<Games>>(){}.getType();
        List<Games> games = new Gson().fromJson(gameListJson,type);
        this.gameListJson = new Gson().toJson(games);
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

    public List<Games> getGameList() {
        Type type = new TypeToken<List<Games>>(){}.getType();
        return new Gson().fromJson(gameListJson,type);
    }

    public void addGameToList(Games newGame){
        Type type = new TypeToken<List<Games>>() {
        }.getType();
        List<Games> games =  new Gson().fromJson(gameListJson,type);
        if (games != null) {
            games.add(newGame);
        } else {
            games = new ArrayList<>();
            games.add(newGame);
        }
        this.gameListJson = new Gson().toJson(games);
    }
    public void setGameList(List<Games> gameList) {

        this.gameListJson = new Gson().toJson(gameList);
    }
// Getters and setters are ignored for brevity,
    // but they're required for Room to work.
}
