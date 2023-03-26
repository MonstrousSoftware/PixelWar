package com.monstrous.pixelwar;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
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

    public GUI(GameScreen gameScreen) {

        this.gameScreen = gameScreen;
        skin = new Skin(Gdx.files.internal("sgx.skin/sgx-ui.json"));
        stage = new Stage(new ScreenViewport());
        rebuild();
    }

    public void resize(int width, int height) {
        Gdx.app.log("resize", "stage "+width + " x "+height);
        stage.getViewport().update(width, height, true);
        rebuild();
    }

    public void rebuild() {

        labelSelectedType = new Label("Type", skin);
        labelSelectedType.setColor(Color.BLUE);
        labelFPS = new Label("FPS: 0", skin);
        labelHealth = new Label("100 %", skin);
        labelHealth.setColor(Color.BLUE);
        labelMessage = new Label("COMMENCE BATTLE!", skin, "title");
        labelMessage.setColor(Color.RED);


        // now place all the elements
        //
        stage.clear();
        //stage.setDebugAll(true);

        // root table that fills the whole screen
        Table screenTable = new Table();
        stage.addActor(screenTable);
        screenTable.setFillParent(true);        // size to match stage size
        //screenTable.setDebug(true);

        Table stats = new Table();
        stats.add(labelSelectedType).row();
        stats.add(labelHealth);
        stats.pack();

        TextButton tkButton = new TextButton("TK", skin);
        TextButton asButton = new TextButton("AS", skin);
        TextButton aaButton = new TextButton("AA", skin);
        TextButton twButton = new TextButton("TW", skin);
        TextButton flButton = new TextButton("FL", skin);
        Table unitButtons = new Table();
        unitButtons.add(tkButton);
        unitButtons.add(asButton);
        unitButtons.add(aaButton);
        unitButtons.add(twButton);
        unitButtons.add(flButton);
        unitButtons.pack();

        TextButton backButton = new TextButton("<", skin);

        screenTable.add(backButton).top().left();
        screenTable.add(labelFPS).top().left().expandX().row();
        screenTable.add(labelMessage).colspan(2).top().center().expandY().row();
        screenTable.add(unitButtons).left().bottom();
        screenTable.add(stats).center().bottom();
        screenTable.pack();

        messageTimer = 2.0f;

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
                gameScreen.toggleUnit(GameObjectTypes.findType("Tank"));
            }
        });
        asButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                gameScreen.toggleUnit(GameObjectTypes.findType("AirShip"));
            }
        });
        aaButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                gameScreen.toggleUnit(GameObjectTypes.findType("Anti-Aircraft"));
            }
        });
        twButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                gameScreen.toggleUnit(GameObjectTypes.findType("Tower"));
            }
        });
        flButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                gameScreen.toggleUnit(GameObjectTypes.findType("Flag"));
            }
        });
    }


    private void update(float deltaTime ) {
        GameObject selected = gameScreen.getSelectedObject();
        if(selected != null) {
            labelSelectedType.setText(selected.type.name);

            labelHealth.setText(""+(int)(selected.healthPoints* 100f / selected.type.healthPoints)+ "%");
        }

        messageTimer -= deltaTime;
        if(messageTimer <= 0)
            labelMessage.setText("");

        int fps = (int)(1.0f/deltaTime);
        labelFPS.setText("FPS: "+ fps);
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
