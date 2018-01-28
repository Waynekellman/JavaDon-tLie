package com.nyc.javadontlie.moneyModel;

import java.util.ArrayList;

/**
 * Created by Wayne Kellman on 1/25/18.
 */

public class Games {
    private String gameName;
    private int amount;
    private ArrayList<String> log;

    public Games() {
    }

    public Games(String gameName, int amount) {
        this.gameName = gameName;
        this.amount = amount;
        this.log = new ArrayList<>();
    }

    public Games(String gameName) {
        this.gameName = gameName;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public ArrayList<String> getLog() {
        if (log.size() >= 1) {
            return log;
        }else {
            return new ArrayList<>();
        }
    }

    public void setLog(ArrayList<String> log) {
        this.log = log;
    }
}
