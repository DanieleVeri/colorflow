package com.colorflow.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.colorflow.MainGame;
import com.colorflow.music.BeatDetector;
import com.colorflow.music.IBeatDetector;
import com.colorflow.play.HUDStage;
import com.colorflow.play.PlayStage;
import com.colorflow.play.Score;
import com.colorflow.utility.Position;

public class PlayScreen implements Screen {

    private MainGame game;
    private OrthographicCamera camera, cameraFlipY;
    private Score score;
    private PlayStage playStage;
    private HUDStage hudStage;
    private IBeatDetector beatDetector;
    private State state;
    private InputMultiplexer multiplexer;

    public PlayScreen(MainGame game) {
        this.game = game;
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, Position.getWidthScreen(), Position.getHeightScreen());
        this.camera.update();
        this.cameraFlipY = new OrthographicCamera();
        this.cameraFlipY.setToOrtho(true, Position.getWidthScreen(), Position.getHeightScreen());
        this.cameraFlipY.update();
        this.playStage = new PlayStage(new ScreenViewport(this.cameraFlipY), this);
        this.hudStage = new HUDStage(new ScreenViewport(this.camera), this);
        this.score = new Score();
        this.score.addObserver(hudStage);
        this.beatDetector = new BeatDetector(game.getMusicManager());
        this.multiplexer = new InputMultiplexer();
    }

    @Override
    public void show() {
        reset();
        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        playStage.act(delta);
        hudStage.act(delta);
        playStage.draw();
        hudStage.draw();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
        if (state == State.PLAY) {
            setState(State.PAUSE);
        }
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        playStage.dispose();
        hudStage.dispose();
    }

    public MainGame getGame() {
        return game;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        switch (state) {
            case PLAY:
                game.getMusicManager().play();
                multiplexer.clear();
                multiplexer.addProcessor(playStage);
                multiplexer.addProcessor(playStage.getRing().getListener());
                multiplexer.addProcessor(hudStage);
                break;
            case PAUSE:
                game.getMusicManager().pause();
                multiplexer.clear();
                multiplexer.addProcessor(hudStage);
                break;
            case OVER:
                game.getMusicManager().stop();
                game.getDataManager().incCoins(score.getCoins());
                if (game.getDataManager().getRecord() < score.getPoints()) {
                    game.getDataManager().setRecord(score.getPoints());
                }
                multiplexer.clear();
                multiplexer.addProcessor(hudStage);
                break;
            default:
                throw new IllegalStateException();
        }
        playStage.setState(state);
        hudStage.setState(state);
        this.state = state;
    }

    public Score getScore() {
        return score;
    }

    public void reset() {
        game.getMusicManager().reset();
        score.reset();
        playStage.reset();
        setState(State.PLAY);
    }

    public enum State {
        PLAY, PAUSE, OVER
    }

}