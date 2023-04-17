package com.monstrous.pixelwar;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.monstrous.pixelwar.screens.GameScreen;

public class GUI implements Disposable {

    public Stage stage;
    private Skin skin;
    private GameScreen gameScreen;
    private Label labelSelectedType;
    private Label labelFPS;
    private Label labelHealth;
    private Label labelMessage; // big message, centre screen
    private float messageTimer; // counting down while message is displayed
    private Label nTK, nAS, nAA, nTW, nFL;

    public GUI(GameScreen gameScreen) {

        this.gameScreen = gameScreen;
        skin = new Skin(Gdx.files.internal("blue-pixel-skin/blue-pixel.json"));
        //skin = new Skin(Gdx.files.internal("sgx.skin/sgx-ui.json"));
        stage = new Stage(new ScreenViewport());

        // set message outside of resize/rebuild, so that resizing doesn't affect it
        messageTimer = 0;
        labelMessage = new Label("", skin, "title");

        // don't rebuild the gui now, do it in resize()
    }

    public void resize(int width, int height) {
        Gdx.app.log("resize", "stage "+width + " x "+height);
        stage.getViewport().update(width, height, true);
        rebuild();
    }

    public void rebuild() {

        labelSelectedType = new Label("Type", skin);
        labelSelectedType.setColor(Color.BLUE);
        labelFPS = new Label("", skin);
        labelHealth = new Label("100 %", skin);
        labelHealth.setColor(Color.BLUE);
        labelMessage.setColor(Color.RED);


        // now place all the elements
        //
        stage.clear();
        //stage.setDebugAll(true);

        // root table that fills the whole screen
        Table screenTable = new Table();
        stage.addActor(screenTable);
        screenTable.setFillParent(true);        // size to match stage size


        Table stats = new Table();
        stats.add(labelSelectedType).row();
        stats.add(labelHealth);
        stats.pack();



        ImageButton tkButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("icons/b_tank.png")))));
        ImageButton asButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("icons/b_airship.png")))));
        ImageButton aaButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("icons/b_anti-aircraft.png")))));
        ImageButton twButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("icons/b_tower.png")))));
        ImageButton flButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("icons/b_flag.png")))));

        nTK = new Label("4", skin, "small-font", Color.BLUE);
        nAS = new Label("2", skin, "small-font", Color.BLUE);
        nAA = new Label("3", skin, "small-font", Color.BLUE);
        nTW = new Label("1", skin, "small-font", Color.BLUE);
        nFL = new Label("1", skin, "small-font", Color.BLUE);


        Table unitButtons = new Table();
        unitButtons.add(tkButton);
        unitButtons.add(asButton);
        unitButtons.add(aaButton);
        unitButtons.add(twButton);
        unitButtons.add(flButton);
        unitButtons.row();
        unitButtons.add(nTK);
        unitButtons.add(nAS);
        unitButtons.add(nAA);
        unitButtons.add(nTW);
        unitButtons.add(nFL);
        unitButtons.pack();

        ImageButton backButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("icons/b_exit.png")))));

        //TextButton backButton = new TextButton("<", skin);

        screenTable.add(backButton).top().left().expandX().row();
        screenTable.add(labelMessage).colspan(3).top().center().expandY().row();
        screenTable.add(unitButtons).left().bottom();
        screenTable.add(stats).center().bottom();
        screenTable.add(labelFPS).width(100f).right().bottom().expandX();
        screenTable.pack();

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                gameScreen.pressedEscape();
            }
        });

        tkButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                gameScreen.toggleUnit("Tank");
            }
        });
        asButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                gameScreen.toggleUnit("AirShip");
            }
        });
        aaButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                gameScreen.toggleUnit("Anti-Aircraft");
            }
        });
        twButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                gameScreen.toggleUnit("Tower");
            }
        });
        flButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                gameScreen.toggleUnit("Flag");
            }
        });
    }


    private void update(float deltaTime ) {
        GameObject selected = gameScreen.getSelectedObject();
        if(selected != null) {
            labelSelectedType.setText(selected.type.name);

            labelHealth.setText("Health "+(int)(selected.healthPoints* 100f / selected.type.healthPoints)+ "%");
        }

        messageTimer -= deltaTime;
        if(messageTimer <= 0)
            labelMessage.setText("");

        if(Settings.showFPS) {
            int fps = (int) (1.0f / deltaTime);
            labelFPS.setText("FPS: " + fps);
        }
        nTK.setText(gameScreen.world.getTypeCount(0));
        nAS.setText(gameScreen.world.getTypeCount(1));
        nAA.setText(gameScreen.world.getTypeCount(2));
        nTW.setText(gameScreen.world.getTypeCount(3));
        nFL.setText(gameScreen.world.getTypeCount(4));

    }

    public void render( float deltaTime ) {
        update(deltaTime);

        stage.act(deltaTime);
        stage.draw();
    }

    public void setMessage(String text) {
        labelMessage.setText(text);
        messageTimer = 2f;
    }

    @Override
    public void dispose () {
        stage.dispose();
        skin.dispose();
    }

}
