package com.colorflow.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.colorflow.utility.Position;

public class LoadingScreen implements Screen {

    private OrthographicCamera camera;
    private Stage stage;
    private double t = -Math.PI / 2;

    public LoadingScreen() {
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, Position.getWidthScreen(), Position.getHeightScreen());
        this.camera.update();
        this.stage = new Stage(new ScreenViewport(this.camera));
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        t += Math.PI * delta;
        Gdx.gl.glClearColor((float) (Math.sin(t) / 2 + 0.5),
                (float) (Math.sin(t) / 2 + 0.5),
                (float) (Math.sin(t) / 2 + 0.5), 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
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
        stage.dispose();
    }
}
