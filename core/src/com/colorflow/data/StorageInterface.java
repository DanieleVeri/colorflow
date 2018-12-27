package com.colorflow.data;

import java.util.List;

/**
 * Created by daniele on 28/07/17.
 */

public interface StorageInterface {
     int getCoins();
     String getVersion();
     int getRecord();
     List<String> getRings();
     List<String> getBonus();
     String getUsedRing();
     void incCoins(int coins);
     void setRecord(int score);
     void setVersion(String version);
     void setUsedRing(String ringId);
     void purchaseRing(int cost, String id);
     void purchaseBonus(int cost, String id);
}
