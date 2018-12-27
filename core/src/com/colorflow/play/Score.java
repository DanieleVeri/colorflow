package com.colorflow.play;

import java.util.Observable;

/**
 * Created by daniele on 11/05/17.
 */

public class Score extends Observable {

    private float multiplier;
    private int points, coins;

    public Score() {
        reset();
    }

    public float getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(float multiplier) {
        this.multiplier = multiplier;
        setChanged();
        notifyObservers();
    }

    public int getPoints() {
        return points;
    }

    public void incPoints(int points) {
        this.points += points * multiplier;
        setChanged();
        notifyObservers();
    }

    public int getCoins() {
        return coins;
    }

    public void incCoins(int coins) {
        this.coins += coins;
        setChanged();
        notifyObservers();
    }

    public void reset() {
        points = 0;
        coins = 0;
        multiplier = 1;
        setChanged();
        notifyObservers();
    }

}
