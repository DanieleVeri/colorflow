package com.colorflow.play;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.StringBuilder;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.colorflow.screen.PlayScreen;
import com.colorflow.screen.PlayScreen.State;
import com.colorflow.utility.Position;
import com.colorflow.utility.ButtonListener;

import java.util.Observable;
import java.util.Observer;

public class HUDStage extends Stage implements Observer {

    private PlayScreen playScreen;
    private ShapeRenderer shapeRenderer;
    private Table play, pause, over;

    public HUDStage(Viewport viewport, PlayScreen playScreen) {
        super(viewport);
        this.playScreen = playScreen;
        this.builder = new StringBuilder();
        this.shapeRenderer = new ShapeRenderer();
        initUI();
        setState(PlayScreen.State.PLAY);
    }

    private StringBuilder builder;
    private Label scorePlay, coinsPlay, scorePause, coinsPause, scoreOver, coinsOver;

    @Override
    public void update(Observable o, Object arg) {
        scorePlay.setText(String.valueOf(playScreen.getScore().getPoints()));
        builder.append(playScreen.getScore().getCoins()).append("Z");
        coinsPlay.setText(builder);
        scorePause.setText(String.valueOf(playScreen.getScore().getPoints()));
        coinsPause.setText(builder);
        builder.delete(0, builder.length);
        if (playScreen.getScore().getPoints() <= playScreen.getGame().getDataManager().getRecord()) {
            builder.append("SCORE: ").append(playScreen.getScore().getPoints()).append("\nRECORD: ")
                    .append(playScreen.getGame().getDataManager().getRecord());

        } else {
            builder.append("NEW R3C0RD!\n").append(playScreen.getScore().getPoints());
        }
        scoreOver.setText(builder);
        builder.delete(0, builder.length);
        builder.append("COINS: ").append(playScreen.getScore().getCoins()).append("Z");
        coinsOver.setText(builder);
        builder.delete(0, builder.length);
    }

    @Override
    public void draw() {
        if (playScreen.getState() != State.PLAY) {
            Gdx.graphics.getGL20().glEnable(GL20.GL_BLEND);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0, 0, 0, 0.8f);
            shapeRenderer.rect(0, 0, Position.getWidthScreen(), Position.getHeightScreen());
            shapeRenderer.end();
            Gdx.graphics.getGL20().glDisable(GL20.GL_BLEND);
        }
        super.draw();
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        super.dispose();
    }

    public void setState(State state) {
        switch (state) {
            case PLAY:
                play.setVisible(true);
                pause.setVisible(false);
                over.setVisible(false);
                break;
            case PAUSE:
                play.setVisible(false);
                pause.setVisible(true);
                over.setVisible(false);
                break;
            case OVER:
                play.setVisible(false);
                pause.setVisible(false);
                over.setVisible(true);
                break;
            default:
                throw new IllegalStateException();
        }
    }

    private void initUI() {
        float tablePad = Position.getHeightScreen() / 48f;
        scorePlay = new Label("", playScreen.getGame().getAssetProvider().getSkin("Play"), "Score");
        coinsPlay = new Label("", playScreen.getGame().getAssetProvider().getSkin("Play"), "Score");
        scorePause = new Label("", playScreen.getGame().getAssetProvider().getSkin("Play"), "Score");
        coinsPause = new Label("", playScreen.getGame().getAssetProvider().getSkin("Play"), "Score");
        scoreOver = new Label("", playScreen.getGame().getAssetProvider().getSkin("Play"), "Score");
        coinsOver = new Label("", playScreen.getGame().getAssetProvider().getSkin("Play"), "Score");
        Label gameOver = new Label("GAME OVER", playScreen.getGame().getAssetProvider().getSkin("Play"), "GameOver");
        ImageButton restartButton = new ImageButton(playScreen.getGame().getAssetProvider().getSkin("Play"), "Redo"),
                adsButton = new ImageButton(playScreen.getGame().getAssetProvider().getSkin("Play"), "Ads"),
                pauseButton = new ImageButton(playScreen.getGame().getAssetProvider().getSkin("Play"), "Pause"),
                playButton = new ImageButton(playScreen.getGame().getAssetProvider().getSkin("Play"), "Play"),
                homeButtonPause = new ImageButton(playScreen.getGame().getAssetProvider().getSkin("Play"), "Home"),
                homeButtonOver = new ImageButton(playScreen.getGame().getAssetProvider().getSkin("Play"), "Home");
        restartButton.addListener(new ButtonListener(playScreen.getGame().getAssetProvider()) {
            @Override
            protected void onTap() {
                playScreen.reset();
            }
        });
        adsButton.addListener(new ButtonListener(playScreen.getGame().getAssetProvider()) {
            @Override
            protected void onTap() {
                //TODO: Implement
            }
        });
        pauseButton.addListener(new ButtonListener(playScreen.getGame().getAssetProvider()) {
            @Override
            protected void onTap() {
                if (playScreen.getState() == State.PLAY) {
                    playScreen.setState(State.PAUSE);
                }
            }
        });
        playButton.addListener(new ButtonListener(playScreen.getGame().getAssetProvider()) {
            @Override
            protected void onTap() {
                playScreen.setState(State.PLAY);
            }
        });
        homeButtonPause.addListener(new ButtonListener(playScreen.getGame().getAssetProvider()) {
            @Override
            protected void onTap() {
                playScreen.getGame().setScreen(playScreen.getGame().getMenu());
            }
        });
        homeButtonOver.addListener(new ButtonListener(playScreen.getGame().getAssetProvider()) {
            @Override
            protected void onTap() {
                playScreen.getGame().setScreen(playScreen.getGame().getMenu());
            }
        });
        /* Play HUD */
        play = new Table();
        play.setFillParent(true);
        play.pad(tablePad);
        play.top();
        play.add(pauseButton).expandX().left();
        play.add(scorePlay).expandX().right();
        play.row();
        play.add(coinsPlay).expand().colspan(2).bottom().right();
        addActor(play);
        /* Pause HUD */
        pause = new Table();
        pause.setFillParent(true);
        pause.pad(tablePad);
        pause.top();
        pause.add(scorePause).colspan(2).expandX().right();
        pause.row();
        pause.add(playButton).expand();
        pause.add(homeButtonPause).expand();
        pause.row();
        pause.add(coinsPause).colspan(2).expandX().right();
        addActor(pause);
        /* Game Over HUD */
        over = new Table();
        over.setFillParent(true);
        over.pad(tablePad);
        over.add(gameOver).colspan(2).expandX();
        over.row();
        over.add(scoreOver).expandX().left();
        over.add(coinsOver).expandX().right();
        over.row();
        over.add(adsButton).colspan(2).expand();
        over.row();
        over.add(restartButton).expand();
        over.add(homeButtonOver).expand();
        addActor(over);
    }

}