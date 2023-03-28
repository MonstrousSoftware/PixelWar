package com.monstrous.pixelwar.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.monstrous.pixelwar.*;
import com.monstrous.pixelwar.screens.MenuScreen;

public class GameScreen extends ScreenAdapter {

    private Main game;
    private PerspectiveCamera cam;
    private int viewHeight, viewWidth;
    private ModelBatch modelBatch;
    public World world;
    private MyCamController camController;
    private Environment environment;
    private MiniMap miniMap;
    private DirectionalShadowLight shadowLight;
    private ModelBatch shadowBatch;
    private GameObject selectedObject;
    private GUI gui;
    private boolean isGameOver;


    public GameScreen(Main game, boolean newGame) {
        this.game = game;
    }

    @Override
    public void show() {

        gui = new GUI(this);


        cam = new PerspectiveCamera(50, viewWidth, viewHeight);
        cam.position.set(30f, 10f, 10f);
        cam.lookAt(0, 0, 0);
        cam.near = 0.1f;
        cam.far = Settings.worldSize * .7f;
        cam.update();

        world = new World(cam);

        camController = new MyCamController(cam);
        //camController = new OrthographicCameraController(cam);

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(gui.stage);
        multiplexer.addProcessor(new MouseController(this));
        multiplexer.addProcessor(camController);
        Gdx.input.setInputProcessor(multiplexer);

        // define some lighting
        Vector3 lightVector = new Vector3(-.2f, -.8f, -.0f).nor();

        environment = new Environment();
        float al = Settings.ambientLightLevel;
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, al, al, al, 1f));
        float dl = Settings.directionalLightLevel;
        environment.set(new ColorAttribute(ColorAttribute.Fog, Settings.skyColour));            // fog

        shadowLight = new DirectionalShadowLight(2048, 2048, 256, 256, 1f, 256);
        // tweak these values so that all the scene is covered and the shadows are not too blocky
        shadowLight.set(new Color(dl, dl, dl, 1), lightVector);
        environment.add(shadowLight);
        environment.shadowMap = shadowLight;
        shadowBatch = new ModelBatch(new DepthShaderProvider());


        modelBatch = new ModelBatch();

        miniMap = new MiniMap(Settings.worldSize, Settings.worldSize, -500);

        game.stopMusic();
        if(Settings.musicEnabled)
            game.startMusic("music/Level 1.wav");

        selectObject( world.selectRandomUnit() );

        gui.setMessage("COMMENCE BATTLE!");
        Sounds.playSound(Sounds.COMMENCE);

        isGameOver = false;

    }

    @Override
    public void render(float delta) {
        if(World.isShaking())
            camController.shake();
        camController.update();

        world.update(delta);
        if(world.isFlagUnderAttack()){
            Sounds.playSound(Sounds.FLAG);
        }
        if(!isGameOver && world.gameOver()) {
            if(world.haveWon()) {
                gui.setMessage("YOU ARE VICTORIOUS!");
                Sounds.playSound(Sounds.VICTORIOUS);
            } else {
                gui.setMessage("YOU WERE DEFEATED!");
                Sounds.playSound(Sounds.DEFEATED);
            }
            isGameOver = true;
        }
        if(selectedObject == null || selectedObject.toRemove)
            selectObject( world.selectRandomUnit() );
        miniMap.update(cam, world, environment);


        // prepare shadow buffer
        shadowLight.begin(Vector3.Zero, cam.direction);
        shadowBatch.begin(shadowLight.getCamera());
        world.render(shadowBatch, environment, false);
        shadowBatch.end();
        shadowLight.end();

        ScreenUtils.clear(Settings.skyColour, true);

        modelBatch.begin(cam);
        world.render(modelBatch, environment, false);
        modelBatch.end();

        miniMap.render();
        gui.render(delta);
    }

    @Override
    public void resize(int width, int height) {
        Gdx.app.debug("GameScreen", "resize("+width+", "+height+")");

        // adjust aspect ratio after a windows resize
        viewWidth = width;
        viewHeight = height;
        cam.viewportWidth = width;
        cam.viewportHeight = height;
        cam.update();

        miniMap.resize(width, height);
        gui.resize(width, height);
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        world.dispose();
        modelBatch.dispose();
        game.stopMusic();
        gui.dispose();
    }

    private Vector3 tmpPos = new Vector3();

    public boolean mouseDown(float screenX, float screenY) {
        //Gdx.app.log("mouse clicked", "");
        cam.update();

        if(world.gameOver())
            return false;

        GameObject result = world.pickObject(cam, (int)screenX, (int)screenY);

        if(result != null) {
            Gdx.app.log("clicked on", result.type.name);
            selectObject(result);
        }
        else if(selectedObject != null && selectedObject.type.isMobile && !selectedObject.isDying){
            boolean hit = world.pickLocation(cam, (int)screenX, (int)screenY, tmpPos);
            if(hit) {
                selectedObject.setDestination(tmpPos);
                if(selectedObject.type.isAirship)
                    Sounds.playSound(Sounds.ROGER_THAT);
                else
                    Sounds.playSound(Sounds.AFFIRMATIVE);
            }
        }
        return false;
    }

    private void selectObject(GameObject go ) {
        selectedObject = go;
        camController.followGameObject(selectedObject);
    }

    public void toggleUnit(GameObjectType type) {
        GameObject next = world.selectNextUnit(type, selectedObject);
        if(next != null)
            selectObject(next);
    }

    public void cameraShake() {
        camController.shake();
    }

    public boolean pressedEscape() {
        Gdx.app.debug("ESC pressed", "");

        game.setScreen(new MenuScreen(game));               // back to menu
        return true;
    }

    public boolean pressedTab() {
        Gdx.app.debug("TAB pressed", "");
        GameObject next = world.selectNextUnitOfType(selectedObject.type, selectedObject);
        selectObject(next);
        return true;
    }

    public GameObject getSelectedObject() {
        return selectedObject;
    }

}
