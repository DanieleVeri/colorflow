package com.colorflow.music;

import java.util.Observer;

public interface MusicManagerInterface {
    void init();
    void reset();
    void play();
    void pause();
    void stop();
    void addObserver(Observer o);
}
