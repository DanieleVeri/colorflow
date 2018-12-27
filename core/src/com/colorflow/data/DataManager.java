package com.colorflow.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/**
 * Created by daniele on 11/05/17.
 */

public class DataManager extends Observable {

    private StorageInterface storageInterface;

    public DataManager(StorageInterface storageInterface) {
        this.storageInterface = storageInterface;
    }

    public int getCoins() {
        return storageInterface.getCoins();
    }

    public String getVersion() {
        return storageInterface.getVersion();
    }

    public List<String> getUnlockedRings() {
        return storageInterface.getRings();
    }

    public List<String> getUnlockedBonus() {
        return storageInterface.getBonus();
    }

    public String getUsedRing() {
        return storageInterface.getUsedRing();
    }

    public int getRecord() {
        return storageInterface.getRecord();
    }

    public void incCoins(int coins) {
        storageInterface.incCoins(coins);
        setChanged();
        notifyObservers();
    }

    public void setVersion(String version) {
        storageInterface.setVersion(version);
        setChanged();
        notifyObservers();
    }

    public void setRecord(int score) {
        storageInterface.setRecord(score);
        setChanged();
        notifyObservers();
    }

    public void setUsedRing(String ringId) {
        storageInterface.setUsedRing(ringId);
        setChanged();
        notifyObservers();
    }


    public void purchaseRing(int cost, String id) {
        storageInterface.purchaseRing(cost, id);
        setChanged();
        notifyObservers();
    }

    public void purchaseBonus(int cost, String id) {
        storageInterface.purchaseBonus(cost, id);
        setChanged();
        notifyObservers();
    }

}
