package com.nyc.javadontlie.moneyModel;

/**
 * Created by Wayne Kellman on 1/11/18.
 */

public class MoneyModel {
    private String name;

    public MoneyModel() {
    }

    public MoneyModel(String name) {
        this.name = name;
    }

    public MoneyModel(String name, int amount) {
        this.name = name;
        this.amount = amount;
    }

    private int amount;


    public String getName() {
        return name;
    }

    public int getAmount() {
        return amount;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
