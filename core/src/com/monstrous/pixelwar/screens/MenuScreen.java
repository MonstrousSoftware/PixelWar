package com.monstrous.pixelwar.screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.monstrous.pixelwar.Main;
import com.monstrous.pixelwar.Settings;

public class MenuScreen extends ScreenAdapter {

    static public int BUTTON_WIDTH = 300;
    static public int BUTTON_HEIGHT = 50;
    static public int BUTTON_PAD = 15;

    private Main game;
    private Stage stage;
    private Skin skin;

    public MenuScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        Gdx.app.debug("MenuScreen", "show()");

        skin = new Skin(Gdx.files.internal("blue-pixel-skin/blue-pixel.json"));
       // skin = new Skin(Gdx.files.internal("sgx.skin/sgx-ui.json"));
        stage = new Stage(new ScreenViewport());
        rebuild();
        Gdx.input.setInputProcessor(stage);

        // allow to continue music that is already playing if we come from splash screen
        game.startMusic("music/Title Screen.wav");
        game.music.play();

    }

    private void rebuild() {

        stage.clear();

        // root table that fills the whole screen
        Table screenTable = new Table();

        stage.addActor(screenTable);
        screenTable.setFillParent(true);        // size to match stage size
        screenTable.setColor(Color.BLUE);

        Label labelTitle = new Label(Settings.title, skin, "title");

        String buttonStyle = "big";

        Table menuTable = new Table();
        TextButton newCampaignButton = new TextButton("START", skin, buttonStyle);
        TextButton instructionsButton = new TextButton("INSTRUCTIONS", skin, buttonStyle);
        TextButton optionsButton = new TextButton("OPTIONS", skin, buttonStyle);
        TextButton creditsButton = new TextButton("CREDITS", skin, buttonStyle);
        TextButton quitButton = new TextButton("QUIT", skin, buttonStyle);

        Label labelVersion = new Label( game.VERSION, skin);

        menuTable.add(newCampaignButton).width(BUTTON_WIDTH).height(BUTTON_HEIGHT).pad(BUTTON_PAD).row();
        menuTable.add(instructionsButton).width(BUTTON_WIDTH).height(BUTTON_HEIGHT).pad(BUTTON_PAD).row();
        menuTable.add(optionsButton).width(BUTTON_WIDTH).height(BUTTON_HEIGHT).pad(BUTTON_PAD).row();
        menuTable.add(creditsButton).width(BUTTON_WIDTH).height(BUTTON_HEIGHT).pad(BUTTON_PAD).row();

        if( Gdx.app.getType() != Application.ApplicationType.WebGL)
            menuTable.add(quitButton).width(BUTTON_WIDTH).height(BUTTON_HEIGHT).pad(BUTTON_PAD).row();            // omit for HTML version


        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.DARK_GRAY);
        pixmap.fill();
        screenTable.setBackground( new TextureRegionDrawable(new TextureRegion(new Texture(pixmap))) );

        screenTable.add(labelTitle).top().pad(50).row();    // title
        screenTable.add(menuTable).row();                   // menu buttons
        screenTable.add(labelVersion).right().bottom().expandX().expandY(); // version string in the bottom right corner of the screen

        screenTable.pack();

        newCampaignButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                game.setScreen( new PreGameScreen(game) );
            }
        });

        instructionsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                game.setScreen( new InstructionsScreen(game) );
            }
        });

        optionsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                game.setScreen( new OptionsScreen(game) );
            }
        });

        creditsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                game.setScreen( new CreditsScreen(game) );
            }
        });

        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                Gdx.app.exit();
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
        Gdx.app.debug("MenuScreen", "resize()");
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void hide() {
        Gdx.app.debug("MenuScreen", "hide()");
        dispose();
    }

    @Override
    public void dispose() {
        Gdx.app.debug("MenuScreen", "dispose()");
        stage.dispose();
        // don't stop the music in case we go to credits or instructions, etc.
    }
}
