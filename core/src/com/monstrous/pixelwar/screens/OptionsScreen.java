package com.monstrous.pixelwar.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.monstrous.pixelwar.Main;

public class OptionsScreen implements Screen {

    static public int WINDOW_WIDTH = 1200;
    static public int WINDOW_HEIGHT = 800;

    static public int BUTTON_WIDTH = 200;
    static public int BUTTON_PAD = 20;

    private Main game;
    private Stage stage;
    private Skin skin;
    private Sound flipSound;

    public OptionsScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        Gdx.app.debug("OptionsScreen", "show()");

        skin = new Skin(Gdx.files.internal("sgx.skin/sgx-ui.json"));
        stage = new Stage(new ScreenViewport());
        rebuild();
        Gdx.input.setInputProcessor(stage);

        flipSound = Gdx.audio.newSound(Gdx.files.internal("sounds/tile-flip.mp3"));
    }

    private void rebuild() {

        stage.clear();

        // root table that fills the whole screen
        Table screenTable = new Table();
        stage.addActor(screenTable);
        screenTable.setFillParent(true);        // size to match stage size

        Label labelTitle = new Label("OPTIONS", skin, "title");

        Table menuTable = new Table();
        final CheckBox fullScreenCheckBox = new CheckBox("FULL SCREEN", skin);

        final Slider soundSlider = new Slider(0,1.0f, 0.05f, false, skin);
        soundSlider.setValue((float) Math.sqrt(game.getSoundVolume()));

        final Slider musicSlider = new Slider(0,1.0f, 0.05f, false, skin);
        musicSlider.setValue( (float) Math.sqrt( game.getMusicVolume()) );

        TextButton backButton = new TextButton("BACK", skin);

        menuTable.add(new Label("Sound Volume: ", skin));
        menuTable.add(soundSlider).width(BUTTON_WIDTH).pad(BUTTON_PAD).row();
        menuTable.add(new Label("Music Volume: ", skin));
        menuTable.add(musicSlider).width(BUTTON_WIDTH).pad(BUTTON_PAD).row();
        menuTable.add(fullScreenCheckBox).colspan(2).width(BUTTON_WIDTH).pad(BUTTON_PAD).row();
        menuTable.row();
        menuTable.add(backButton).colspan(2).width(BUTTON_WIDTH).pad(BUTTON_PAD).row();


        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.DARK_GRAY);
        pixmap.fill();
        screenTable.setBackground( new TextureRegionDrawable(new TextureRegion(new Texture(pixmap))) );
        screenTable.add(labelTitle).top().pad(50).row();
        screenTable.add(menuTable);

        screenTable.pack();

        soundSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float vol = soundSlider.getValue();
                game.setSoundVolume(vol*vol);
                flipSound.play(game.getSoundVolume());
            }
        });

        musicSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float vol = musicSlider.getValue();
                game.setMusicVolume(vol*vol);           // square the slide value because the volume seems to be a log scale
            }
        });

        fullScreenCheckBox.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                Graphics.DisplayMode currentMode = Gdx.graphics.getDisplayMode();
                if(fullScreenCheckBox.isChecked())
                    Gdx.graphics.setFullscreenMode(currentMode);
                else
                    Gdx.graphics.setWindowedMode(WINDOW_WIDTH, WINDOW_HEIGHT);
            }
        });


        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                game.setScreen( new MenuScreen(game) );
            }
        });
    }


    @Override
    public void render(float delta) {
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        Gdx.app.debug("OptionsScreen", "resize()");
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
        Gdx.app.debug("OptionsScreen", "pause()");
    }

    @Override
    public void resume() {
        Gdx.app.debug("OptionsScreen", "resume()");
    }

    @Override
    public void hide() {
        Gdx.app.debug("OptionsScreen", "hide()");
        dispose();
    }

    @Override
    public void dispose() {
        Gdx.app.debug("OptionsScreen", "dispose()");
        stage.dispose();
        flipSound.dispose();
    }
}
