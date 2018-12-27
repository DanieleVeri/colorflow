package com.colorflow.entity.dot;

import java.util.List;

public enum Color {
    RED, YELLOW, GREEN, MAGENTA, BLUE, CYAN;

    public static Color getRandomExcept(List<Color> colors) {
        int colorNum;
        while (true) {
            colorNum = (int) (Math.random() * 6.0);
            switch (colorNum) {
                case 0:
                    if (!colors.contains(Color.RED)) {
                        return RED;
                    }
                    break;
                case 1:
                    if (!colors.contains(Color.YELLOW)) {
                        return YELLOW;
                    }
                    break;
                case 2:
                    if (!colors.contains(Color.GREEN)) {
                        return GREEN;
                    }
                    break;
                case 3:
                    if (!colors.contains(Color.MAGENTA)) {
                        return MAGENTA;
                    }
                    break;
                case 4:
                    if (!colors.contains(Color.BLUE)) {
                        return BLUE;
                    }
                    break;
                case 5:
                    if (!colors.contains(Color.CYAN)) {
                        return CYAN;
                    }
                    break;
                default:
                    throw new IllegalStateException();
            }
        }
    }

    public static float getAngleBetween(Color c1, Color c2) {
        int dif = c1.ordinal() - c2.ordinal();
        return 60 * dif;
    }

    public com.badlogic.gdx.graphics.Color getRGB() {
        switch (this) {
            case RED:
                return com.badlogic.gdx.graphics.Color.RED;
            case YELLOW:
                return com.badlogic.gdx.graphics.Color.YELLOW;
            case GREEN:
                return com.badlogic.gdx.graphics.Color.GREEN;
            case MAGENTA:
                return com.badlogic.gdx.graphics.Color.MAGENTA;
            case BLUE:
                return com.badlogic.gdx.graphics.Color.BLUE;
            case CYAN:
                return com.badlogic.gdx.graphics.Color.CYAN;
            default:
                throw new IllegalStateException();
        }
    }
}
