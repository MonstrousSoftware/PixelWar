package com.monstrous.pixelwar.screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.monstrous.pixelwar.Main;
import com.monstrous.pixelwar.Settings;
import com.monstrous.pixelwar.screens.MenuScreen;


public class SplashScreen extends ScreenAdapter {

    static public int BUTTON_WIDTH = 300;
    static public int BUTTON_PAD = 50;

    private SpriteBatch batch;
    private Main game;
    private Texture texture;
    private float timer;
    private float width, height;
    private Stage stage;
    private Skin skin;

    public SplashScreen(Main game) {
        this.game = game;
    }

    @Override
    public  void show() {

        Gdx.app.debug("SplashScreen", "show()");
        batch = new SpriteBatch();
        texture = new Texture( Gdx.files.internal("logo-static.png"));
        timer = Settings.splashTime;

        skin = new Skin(Gdx.files.internal("sgx.skin/sgx-ui.json"));
        stage = new Stage(new ScreenViewport());
        rebuild();
        Gdx.input.setInputProcessor(stage);

    }

    private void rebuild() {

        stage.clear();

        // root table that fills the whole screen
        Table screenTable = new Table();
        stage.addActor(screenTable);
        screenTable.setFillParent(true);        // size to match stage size


        TextButton startButton = new TextButton("Click to Start", skin);

        if(Gdx.app.getType() == Application.ApplicationType.WebGL)
            screenTable.add(startButton).width(BUTTON_WIDTH).pad(BUTTON_PAD).bottom().expandY();
        screenTable.pack();

        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                //game.music.play();
                game.setScreen( new MenuScreen(game) );
            }
        });
    }

    @Override
    public  void hide() {
        Gdx.app.debug("SplashScreen", "hide()");
        dispose();
    }

    @Override
    public void dispose() {
        Gdx.app.debug("SplashScreen", "dispose()");
        batch.dispose();
        stage.dispose();
    }

    @Override
    public void render( float deltaTime )
    {
        timer -= deltaTime;
        if(timer < 0 && Gdx.app.getType() != Application.ApplicationType.WebGL) {
            game.setScreen(new MenuScreen(game));
            return;
        }
        Gdx.gl.glClearColor(0f, 0f, 0f,  1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        batch.begin();
        batch.draw(texture, 0,0, width, height );
        batch.end();

        stage.act(deltaTime);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

        Gdx.app.debug("SplashScreen", "resize("+width+", "+height+")");
        this.width = width;
        this.height = height;
        stage.getViewport().update(width, height, true);
    }

}
