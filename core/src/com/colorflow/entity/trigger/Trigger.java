package com.colorflow.entity.trigger;

import com.colorflow.entity.Entity;
import com.colorflow.play.PlayStage;

public abstract class Trigger {

    private static Trigger dot = new DotTrigger(),
                        bonus = new BonusTrigger();

    public static Trigger getDot(PlayStage playStage) {
        dot.setPlayStage(playStage);
        return dot;
    }

    public static Trigger getBonus(PlayStage playStage) {
        bonus.setPlayStage(playStage);
        return bonus;
    }

    protected PlayStage playStage;

    public Trigger() {
    }

    public void setPlayStage(PlayStage playStage) {
        this.playStage = playStage;
    }

    public abstract void run(Entity entity);
}
