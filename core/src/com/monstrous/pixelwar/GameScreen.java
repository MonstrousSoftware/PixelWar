package com.monstrous.pixelwar;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.ScreenUtils;

public class GameScreen extends ScreenAdapter {

    private Main game;
    private PerspectiveCamera cam;
    private int viewHeight, viewWidth;
    private ModelBatch modelBatch;
    private World world;
    private MyCamController camController;
    private Environment environment;
    private MiniMap miniMap;
    private DirectionalLight lightSource;
    private DirectionalShadowLight shadowLight;
    private ModelBatch shadowBatch;
    private GameObject selectedObject;

    public GameScreen(Main game, boolean newGame) {
        this.game = game;
        this.world = new World();
    }

    @Override
    public void show() {

        cam = new PerspectiveCamera(50, viewWidth, viewHeight);
        cam.position.set(40f, 10f, 20f);
        cam.lookAt(0, 0, 0);
        cam.near = 0.1f;
        cam.far = Settings.worldSize * .7f;
        cam.update();


        camController = new MyCamController(cam);
        //camController = new OrthographicCameraController(cam);

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(new MouseController(this));
        multiplexer.addProcessor(camController);
        Gdx.input.setInputProcessor(multiplexer);

        // define some lighting
        Vector3 lightVector = new Vector3(-.2f, -.8f, -.0f).nor();

        environment = new Environment();
        float al = Settings.ambientLightLevel;
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, al, al, al, 1f));
        float dl = Settings.directionalLightLevel;
//        lightSource = new DirectionalLight().set(new Color(dl, dl, dl, 1), lightVector);
//        environment.add( lightSource );
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

        selectedObject = null;
    }

    @Override
    public void render(float delta) {
        camController.update();
        miniMap.update(cam, world, environment);

        shadowLight.begin(Vector3.Zero, cam.direction);
        shadowBatch.begin(shadowLight.getCamera());
//        shadowBatch.render(world.sceneryInstances);
//        shadowBatch.render(world.instances);
        world.render(shadowBatch, environment, false);
        shadowBatch.end();
        shadowLight.end();

        ScreenUtils.clear(Settings.skyColour, true);

        modelBatch.begin(cam);
        world.render(modelBatch, environment, false);
//        modelBatch.render(world.sceneryInstances, environment);
//        modelBatch.render(world.instances, environment);
        modelBatch.end();

        miniMap.render();
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
    }

    public boolean mouseDown(int screenX, int screenY, int button) {
        Gdx.app.log("mouse clicked", "");
        cam.update();
        GameObject result = world.pickObject(cam, screenX, screenY);
        if(result != null) {
            Gdx.app.log("clicked on", result.type.name);
            result.modelInstance.materials.first().set(ColorAttribute.createDiffuse(Color.YELLOW));
        }
        selectedObject = result;
        return false;
    }

    public boolean pressedEscape() {
        Gdx.app.log("ESC pressed", "");
        game.setScreen(new MenuScreen(game));
        return true;
    }


}
