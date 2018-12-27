package com.colorflow.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.colorflow.MainGame;
import com.colorflow.ring.Ring;
import com.colorflow.utility.Position;
import com.colorflow.utility.ButtonListener;

public class MenuScreen implements Screen {

    private MainGame game;
    private OrthographicCamera camera;
    private Stage stage;
    private Ring ring;
    private InputMultiplexer multiplexer;
    private Label record;

    public MenuScreen(MainGame game) {
        this.game = game;
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, Position.getWidthScreen(), Position.getHeightScreen());
        this.camera.update();
        this.stage = new Stage(new ScreenViewport(this.camera));
        multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(this.stage);
        initUI();
    }

    @Override
    public void show() {
        record.setText("R3C0RD: " + game.getDataManager().getRecord());
        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        ring.rotateBy(1);
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        ring.dispose();
        stage.dispose();
    }

    private void initUI() {
        Table table = new Table();
        table.setFillParent(true);
        table.center().pad(30);
        record = new Label("R3C0RD: " + game.getDataManager().getRecord(), game.getAssetProvider().getSkin("Menu"), "Title");
        ring = new Ring(game.getDataManager().getUsedRing());
        stage.addActor(this.ring);
        Label title = new Label("COLOR FLOW", game.getAssetProvider().getSkin("Menu"), "Title");
        ImageButton playButton, shopButton, rateButton, creditsButton, settingsButton;
        playButton = new ImageButton(game.getAssetProvider().getSkin("Menu"), "Play");
        shopButton = new ImageButton(game.getAssetProvider().getSkin("Menu"), "Shop");
        rateButton = new ImageButton(game.getAssetProvider().getSkin("Menu"), "Rate");
        creditsButton = new ImageButton(game.getAssetProvider().getSkin("Menu"), "Credits");
        settingsButton = new ImageButton(game.getAssetProvider().getSkin("Menu"), "Settings");
        playButton.addListener(new ButtonListener(game.getAssetProvider()) {
            @Override
            protected void onTap() {
                game.setScreen(game.getPlay());
            }
        });
        shopButton.addListener(new ButtonListener(game.getAssetProvider()) {
            @Override
            protected void onTap() {
                game.setScreen(game.getShop());
            }
        });
        rateButton.addListener(new ButtonListener(game.getAssetProvider()) {
            @Override
            protected void onTap() {
                game.setScreen(game.getSocial());
            }
        });
        creditsButton.addListener(new ButtonListener(game.getAssetProvider()) {
            @Override
            protected void onTap() {
                game.setScreen(game.getCredits());
            }
        });
        settingsButton.addListener(new ButtonListener(game.getAssetProvider()) {
            @Override
            protected void onTap() {
                game.setScreen(game.getSettings());
            }
        });
        table.add(rateButton).left();
        table.add(title).expandX();
        table.add(shopButton).right();
        table.row();
        table.add(playButton).colspan(3).expand();
        table.row();
        table.add(settingsButton).left();
        table.add(record);
        table.add(creditsButton).right();
        stage.addActor(table);
    }
}
