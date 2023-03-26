package com.monstrous.pixelwar.screens;

import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.monstrous.pixelwar.Main;
import com.monstrous.pixelwar.Settings;
import com.monstrous.pixelwar.screens.MenuScreen;


public class SplashScreen extends ScreenAdapter {


    private SpriteBatch batch;
    private Main game;
    private Texture texture;
    private float timer;
    private float width, height;

    public SplashScreen(Main game) {
        this.game = game;
    }

    @Override
    public  void show() {

        Gdx.app.debug("SplashScreen", "show()");
        batch = new SpriteBatch();
        texture = new Texture( Gdx.files.internal("logo-static.png"));
        timer = Settings.splashTime;

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
    }

    @Override
    public void render( float deltaTime )
    {
        timer -= deltaTime;
        if(timer < 0) {
            game.setScreen(new MenuScreen(game));
            return;
        }
        Gdx.gl.glClearColor(0f, 0f, 0f,  1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        batch.begin();
        batch.draw(texture, 0,0, width, height );
        batch.end();
    }

    @Override
    public void resize(int width, int height) {

        Gdx.app.debug("SplashScreen", "resize("+width+", "+height+")");
        this.width = width;
        this.height = height;
    }

}
