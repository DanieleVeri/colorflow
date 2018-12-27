package com.colorflow.utility;

import com.badlogic.gdx.Gdx;

public class Position {

    protected float x, y;

    protected Position(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getXPerc() {
        return x / widthScreen * 100.0f;
    }

    public float getYPerc() {
        return y / heightScreen * 100.0f;
    }

    public float getDistRadial() {
        return (float) Math.sqrt(getXCartesian() * getXCartesian() +
                getYCartesian() * getYCartesian());
    }

    public float getAngleRadial() {
        double alpha = Math.toDegrees(Math.atan2(getYCartesian(), getXCartesian()));
        return (float) (alpha > 0 ? alpha : alpha + 360);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (!(obj instanceof Position))
            return false;
        Position other = (Position) obj;
        if (x == other.getX() && y == other.getY())
            return true;
        return false;
    }

    private double getXCartesian() {
        return x - widthScreen / 2.0;
    }

    private double getYCartesian() {
        return heightScreen / 2.0 - y;
    }

    /* STATIC */

    protected static float widthScreen = Gdx.graphics.getWidth();
    protected static float heightScreen = Gdx.graphics.getHeight();

    public static float getWidthScreen() {
        return widthScreen;
    }

    public static float getHeightScreen() {
        return heightScreen;
    }

    public static Position getCenter() {
        return new Position(widthScreen / 2.0f, heightScreen / 2.0f);
    }

    public static class Pixel extends Position {
        public Pixel(float x, float y) {
            super(x, y);
        }
    }

    public static class Radial extends Position {
        public Radial(float angle, float dist) {
            super((float) (Math.cos(Math.toRadians(angle)) * dist + getWidthScreen() / 2.0),
                    (float) (getHeightScreen() / 2.0 - Math.sin(Math.toRadians(angle)) * dist));
        }

        public void setAngle(float angle) {
            double dist = getDistRadial();
            super.setX((float) (Math.cos(Math.toRadians(angle)) * dist + getWidthScreen() / 2.0));
            super.setY((float) (getHeightScreen() / 2.0 - Math.sin(Math.toRadians(angle)) * dist));
        }

        public void setDist(float dist) {
            double angle = Math.toRadians(getAngleRadial());
            super.setX((float) (Math.cos(angle) * dist + getWidthScreen() / 2.0));
            super.setY((float) (getHeightScreen() / 2.0 - Math.sin(angle) * dist));
        }

        public static float regulateAngle(float val) {
            if (val >= 0 && val < 360) {
                return val;
            }
            while (val < 0) {
                val += 360;
            }
            while (val >= 360) {
                val -= 360;
            }
            return val;
        }
    }

    public static class Percent extends Position {
        public Percent(float x, float y) {
            super(x / 100.0f * widthScreen, y / 100.0f * heightScreen);
        }

        @Override
        public void setX(float x) {
            super.setX(x / 100.0f * widthScreen);
        }

        @Override
        public void setY(float y) {
            super.setY(y / 100.0f * heightScreen);
        }
    }

}
