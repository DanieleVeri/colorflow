package com.colorflow.play;

import com.colorflow.entity.Path;
import com.colorflow.entity.bonus.Bonus;
import com.colorflow.entity.bonus.BonusPool;
import com.colorflow.entity.dot.Color;
import com.colorflow.entity.dot.Dot;
import com.colorflow.entity.dot.DotPool;
import com.colorflow.utility.Position;

import java.util.ArrayList;
import java.util.List;

public class Spawner {

    private static final float SPAWN_DIST = (float) (Math.sqrt(Math.pow(Position.getHeightScreen(), 2)
            + Math.pow(Position.getWidthScreen(), 2)) / 2);

    private PlayStage playStage;
    private float dotSpeed, bonusSpeed;
    private Path.Type dotPath;

    public Spawner(PlayStage playStage) {
        this.playStage = playStage;
        this.pickedColors = new ArrayList<Color>();
        this.start = new Position.Radial(0, SPAWN_DIST);
        reset();
    }

    public void reset() {
        dotSpeed = 2.5f;
        bonusSpeed = 1;
        dotPath = Path.Type.RADIAL;
    }

    public void setDotSpeed(float dotSpeed) {
        this.dotSpeed = dotSpeed;
    }

    public void setBonusSpeed(float bonusSpeed) {
        this.bonusSpeed = bonusSpeed;
    }

    public void setDotPath(Path.Type dotPath) {
        this.dotPath = dotPath;
    }

    private List<Color> pickedColors;
    private Position.Radial start;

    public void waveDotStd(int num) {
        pickedColors.clear();
        float ang = (float) Math.random() * 360f;
        if (num > 6 || num <= 0) {
            throw new IndexOutOfBoundsException();
        }
        for (int i = 0; i < num; i++) {
            pickedColors.add(Color.getRandomExcept(pickedColors));
            if (i == 0) {
                start.setAngle(ang);
            } else {
                start.setAngle(Position.Radial.regulateAngle(ang + Color.getAngleBetween(
                        pickedColors.get(pickedColors.size() - 1),
                        pickedColors.get(0))));
            }
            playStage.addActor(DotPool.getInstance().get(Dot.Type.COIN, pickedColors.get(pickedColors.size() - 1),
                    dotPath, start, dotSpeed));
        }
    }

    public void waveDotMix(int num) {
        pickedColors.clear();
        float ang = (float) Math.random() * 360f;
        if (num > 6 || num <= 0) {
            throw new IndexOutOfBoundsException();
        }
        for (int i = 0; i < num; i++) {
            pickedColors.add(Color.getRandomExcept(pickedColors));
            if (i == 0) {
                start.setAngle(ang);
            } else {
                start.setAngle(Position.Radial.regulateAngle(ang + Color.getAngleBetween(
                        pickedColors.get(pickedColors.size() - 1),
                        pickedColors.get(0))));
            }
            if (Math.random() < 0.5) {
                playStage.addActor(DotPool.getInstance().get(Dot.Type.STD, pickedColors.get(pickedColors.size() - 1),
                        dotPath, start, dotSpeed));
            } else {
                playStage.addActor(DotPool.getInstance().get(Dot.Type.REVERSE,
                        Color.getRandomExcept(pickedColors.subList(pickedColors.size() - 1, pickedColors.size())),
                        dotPath, start, dotSpeed));
            }
        }
    }

    public void bonus() {
        playStage.addActor(BonusPool.getInstance().get(Bonus.Type.BOMB, Path.Type.RADIAL,
                new Position.Radial((float) Math.random() * 360f, SPAWN_DIST), bonusSpeed));
    }

}
