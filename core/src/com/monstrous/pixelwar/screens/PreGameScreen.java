package com.monstrous.pixelwar.screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.monstrous.pixelwar.Main;
import com.monstrous.pixelwar.Settings;

// this screen is called before the game screen and immediately calls the game screen
// this in case the game screen takes time to load, then at least there is something on screen


public class PreGameScreen extends ScreenAdapter {

    private SpriteBatch batch;
    private Main game;
    private Texture texture;
    private float timer;


    public PreGameScreen(Main game) {
        this.game = game;
    }

    @Override
    public  void show() {

        Gdx.app.debug("PreGameScreen", "show()");
        batch = new SpriteBatch();
        texture = new Texture( Gdx.files.internal("loading.png"));
        timer = 0.5f;
    }

    @Override
    public  void hide() {
        Gdx.app.debug("PreGameScreen", "hide()");
        dispose();
    }

    @Override
    public void dispose() {
        Gdx.app.debug("PreGameScreen", "dispose()");
        batch.dispose();
        texture.dispose();
    }

    @Override
    public void render( float deltaTime )
    {
        timer -= deltaTime;
        if(timer < 0 ) {
            game.setScreen(new GameScreen(game));   // load game screen automatically
            return;
        }

        // put loading texture centred on a black background
        ScreenUtils.clear(Color.BLACK);
        batch.begin();
        batch.draw(texture, (Gdx.graphics.getWidth()-texture.getWidth())/2f,(Gdx.graphics.getHeight()-texture.getHeight())/2f);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {

        Gdx.app.debug("PreGameScreen", "resize("+width+", "+height+")");
        batch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
    }

}
