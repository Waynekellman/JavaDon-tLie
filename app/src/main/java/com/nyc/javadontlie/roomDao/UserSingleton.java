package com.nyc.javadontlie.roomDao;

import com.nyc.javadontlie.moneyModel.Games;
import com.nyc.javadontlie.moneyModel.User;

/**
 * Created by Wayne Kellman on 3/14/18.
 */

public class UserSingleton {
    private User user;
    private Boolean isNotNull;
    private static UserSingleton userSingleton;
    private Games game;
    private int indexInList;

    private UserSingleton() {
        game = new Games("",0);
        user = new User("","");
        indexInList = 0;
    }

    public static UserSingleton getInstance() {
        if (userSingleton == null) {
            userSingleton = new UserSingleton();
            return userSingleton;
        } else {
            return userSingleton;
        }

    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Boolean getNotNull() {
        return isNotNull;
    }

    public void setNotNull(Boolean notNull) {
        isNotNull = notNull;
    }

    public Games getGame() {
        return game;
    }

    public void setGame(Games game) {
        this.game = game;
    }

    public int getIndexInList() {
        return indexInList;
    }

    public void setIndexInList(int indexInList) {
        this.indexInList = indexInList;
    }
}
