package com.colorflow.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.colorflow.MainGame;
import com.colorflow.ring.Ring;
import com.colorflow.utility.ButtonListener;
import com.colorflow.utility.Position;

import java.util.Observable;
import java.util.Observer;

public class ShopScreen implements Screen, Observer {

    private MainGame game;
    private Tab tab;
    private OrthographicCamera camera;
    private Stage stage;
    private InputMultiplexer multiplexer;
    /* UI */
    private String dataVersion = "";
    private Table table;
    private ImageButton homeBtn, ringBtn, bonusBtn;
    private Label title, coins;
    private ScrollPane ringScroll, bonusScroll;
    private Table ringTable, bonusTable;

    public ShopScreen(MainGame game) {
        this.game = game;
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, Position.getWidthScreen(), Position.getHeightScreen());
        this.camera.update();
        this.stage = new Stage(new ScreenViewport(this.camera));
        this.multiplexer = new InputMultiplexer();
        this.multiplexer.addProcessor(stage);
        initUI();
        loadContent();
        setTab(Tab.RINGS);
    }

    @Override
    public void show() {
        game.getDataManager().addObserver(this);
        Gdx.input.setInputProcessor(multiplexer);
        loadContent();
        updateStatus();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
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
        game.getDataManager().deleteObserver(this);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    @Override
    public void update(Observable o, Object arg) {
        updateStatus();
    }

    private void initUI() {
        table = new Table();
        table.setFillParent(true);
        table.pad(30);
        homeBtn = new ImageButton(game.getAssetProvider().getSkin("Shop"), "Home");
        homeBtn.addListener(new ButtonListener(game.getAssetProvider()) {
            @Override
            protected void onTap() {
                game.setScreen(game.getMenu());
            }
        });
        ringBtn = new ImageButton(game.getAssetProvider().getSkin("Shop"), "Ring");
        ringBtn.addListener(new ButtonListener(game.getAssetProvider()) {
            @Override
            protected void onTap() {
                if (tab != Tab.RINGS) {
                    setTab(Tab.RINGS);
                }
            }
        });
        bonusBtn = new ImageButton(game.getAssetProvider().getSkin("Shop"), "Bonus");
        bonusBtn.addListener(new ButtonListener(game.getAssetProvider()) {
            @Override
            protected void onTap() {
                if (tab != Tab.BONUS) {
                    setTab(Tab.BONUS);
                }
            }
        });
        ringTable = new Table();
        bonusTable = new Table();
        ringScroll = new ScrollPane(ringTable);
        bonusScroll = new ScrollPane(bonusTable);
        title = new Label("--title--", game.getAssetProvider().getSkin("Shop"), "Title");
        coins = new Label("--coins--", game.getAssetProvider().getSkin("Shop"), "Coins");
        table.add(homeBtn);
        table.add(title);
        table.row();
        table.add(ringBtn);
        table.add(ringScroll).expand().fill().right();
        table.row();
        table.add(bonusBtn);
        table.add(coins);
        stage.addActor(table);
    }

    private void loadContent() {
        if (dataVersion.equals(game.getDataManager().getVersion())) {
            return;
        }
        dataVersion = game.getDataManager().getVersion();
        /* Rings */
        FileHandle[] files = Gdx.files.local("rings").list();
        ringTable.clear();
        for (int i = 0; i < files.length; i++) {
            if (files[i].extension().equals("xml")) {
                final Ring ring = new Ring(files[i].name());
                ring.addAction(Actions.rotateBy(99999999, 1999999));
                Label name = new Label(ring.getName(), game.getAssetProvider().getSkin("Shop"), "ItemName");
                final TextButton cost = new TextButton(ring.getCost() + "Z", game.getAssetProvider().getSkin("Shop"), "Buy");
                cost.addListener(new ButtonListener(game.getAssetProvider()) {
                    @Override
                    protected void onTap() {
                        game.getDataManager().purchaseRing(ring.getCost(), ring.getID());
                        cost.setDisabled(true);
                        cost.setTouchable(Touchable.disabled);
                    }
                });
                ringTable.add(ring).expandX();
                ringTable.add(name).expandX();
                ringTable.add(cost).expandX();
                ringTable.row().pad(20);
            }
        }
        /* Bonus */
    }

    private void updateStatus() {
        coins.setText(String.valueOf(game.getDataManager().getCoins()) +"Z");
        /* Ring */
        Ring ring = null;
        TextButton button;
        for (Cell c : ringTable.getCells()) {
            if (c.getActor() instanceof Ring) {
                ring = (Ring) c.getActor();
            }
            if (c.getActor() instanceof TextButton) {
                button = (TextButton) c.getActor();
                if (game.getDataManager().getCoins() < ring.getCost() ||
                        game.getDataManager().getUnlockedRings().contains(ring.getID())) {
                    button.setDisabled(true);
                    button.setTouchable(Touchable.disabled);
                }
            }
        }
        /* Bonus */
    }

    private void setTab(Tab tab) {
        if (this.tab == null) {
            render(Gdx.graphics.getDeltaTime());
        }
        title.setText(tab.toString());
        switch (tab) {
            case RINGS:
                table.getCells().get(3).setActor(ringScroll);
                ringBtn.addAction(Actions.moveBy(50, 0, 0.25f));
                break;
            case BONUS:
                table.getCells().get(3).setActor(bonusScroll);
                bonusBtn.addAction(Actions.moveBy(50, 0, 0.25f));
                break;
            default:
                throw new IllegalStateException();
        }
        if (this.tab != null) {
            switch (this.tab) {
                case RINGS:
                    ringBtn.addAction(Actions.moveBy(-20, 0, 0.25f));
                    break;
                case BONUS:
                    bonusBtn.addAction(Actions.moveBy(-20, 0, 0.25f));
                    break;
                default:
                    throw new IllegalStateException();
            }
        }
        this.tab = tab;
    }

    enum Tab {
        RINGS, BONUS
    }
}