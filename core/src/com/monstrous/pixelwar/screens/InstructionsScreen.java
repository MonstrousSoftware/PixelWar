package com.monstrous.pixelwar.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
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

public class InstructionsScreen implements Screen {

    static public int TEXT_WIDTH = 400;
    static public int BUTTON_WIDTH = 200;
    static public int BUTTON_PAD = 20;

    private Main game;
    private Stage stage;
    private Skin skin;
    private String texts[];
    private int pageIndex = 0;

    public InstructionsScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        Gdx.app.debug("InstructionsScreen", "show()");

        skin = new Skin(Gdx.files.internal("sgx.skin/sgx-ui.json"));
        stage = new Stage(new ScreenViewport());
        rebuild();
        Gdx.input.setInputProcessor(stage);
    }

    private void rebuild() {
        String part1 = "Few remembered the beginning of the war. Red allied with Blue as the purple alliance. " +
                "Together they destroyed Green in a protracted campaign of attrition. \n\n" +
                "Now the former allies turned on each other and faced each other across a field of 256 by 256 pixels for what would be remembered as the Battle of Perlin Hills.\n\n" +
                "Red occupied the North and Blue held the South. Whoever would take this last territory would win the war of the pixels.";

        String part2 = "Protect your flag at all costs. Try to destroy the enemy flag.\n\n" +
                "Use tanks to destroy enemy structure and units.\n\n"+
                "Airships can drop a bomb over an enemy target. Return to a reloading tower to pick up a new bomb. Watch out for anti-aircraft guns\n\n " +
                "";

        String part3 = "Left click on a unit to select it. Left click on the terrain to direct the unit.\n\n" +
                "Units will fire automatically if an enemy comes in range.\n\n" +
                "Switch between units with the type selection buttons. Use TAB to switch between units of the same type.\n\n" +
                "Use right mouse button to turn the camera. Scroll wheel to zoom.\n\n"+
                "Press ESC to exit the game and return to menu.";
        texts = new String[3];
        texts[0] = part1;
        texts[1] = part2;
        texts[2] = part3;

        stage.clear();

        // root table that fills the whole screen
        Table screenTable = new Table();
        screenTable.setColor(Color.GREEN);
        stage.addActor(screenTable);
        screenTable.setFillParent(true);        // size to match stage size

        Label labelTitle = new Label("INSTRUCTIONS", skin, "title");

        final Label labelText = new Label(texts[pageIndex], skin);
        labelText.setWrap(true);

        Table menuTable = new Table();


        TextButton backButton = new TextButton("DONE", skin);
        TextButton prevButton = new TextButton("PREVIOUS", skin);
        TextButton nextButton = new TextButton("NEXT", skin);

        Table buttons = new Table();
        buttons.add(backButton).width(BUTTON_WIDTH).pad(BUTTON_PAD);
        buttons.add(prevButton).width(BUTTON_WIDTH).pad(BUTTON_PAD);
        buttons.add(nextButton).width(BUTTON_WIDTH).pad(BUTTON_PAD);
        buttons.pack();

        menuTable.add(labelText).width(TEXT_WIDTH).center().row();
        menuTable.add(buttons);
        menuTable.pack();



        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.DARK_GRAY);
        pixmap.fill();
        screenTable.setBackground( new TextureRegionDrawable(new TextureRegion(new Texture(pixmap))) );
        screenTable.add(labelTitle).top().pad(50).row();
        screenTable.add(menuTable);

        screenTable.pack();

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                game.setScreen( new MenuScreen(game) );
            }
        });
        prevButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if(pageIndex > 0)
                    pageIndex--;
                labelText.setText(texts[pageIndex]);
            }
        });
        nextButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if(pageIndex < 2)
                    pageIndex++;
                labelText.setText(texts[pageIndex]);
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
        Gdx.app.debug("InstructionsScreen", "resize()");
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
        Gdx.app.debug("InstructionsScreen", "pause()");
    }

    @Override
    public void resume() {
        Gdx.app.debug("InstructionsScreen", "resume()");
    }

    @Override
    public void hide() {
        Gdx.app.debug("InstructionsScreen", "hide()");
        dispose();
    }

    @Override
    public void dispose() {
        Gdx.app.debug("InstructionsScreen", "dispose()");
        stage.dispose();
    }
}
