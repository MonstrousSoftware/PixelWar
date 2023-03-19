package com.monstrous.pixelwar;

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
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;

public class GameScreen extends ScreenAdapter {

    private Main game;
    private PerspectiveCamera cam;
    private int viewHeight, viewWidth;
    private ModelBatch modelBatch;
    private World world;
    private MyCamController camController;
    private Environment environment;

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
        cam.far = Settings.worldSize*.7f;
        cam.update();


        camController = new MyCamController(cam);
        //camController = new OrthographicCameraController(cam);

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(camController);
        Gdx.input.setInputProcessor(multiplexer);

        // define some lighting
        Vector3 lightVector = new Vector3(-.2f, -.8f, -.2f).nor();

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.5f, 0.5f, 0.5f, 1f));
        float dl = Settings.directionalLightLevel;
        environment.add(new DirectionalLight().set(new Color(dl, dl, dl, 1), lightVector));
        environment.set(new ColorAttribute(ColorAttribute.Fog, Settings.skyColour));			// fog

        modelBatch = new ModelBatch();
    }

    @Override
    public void render(float delta) {
        camController.update();

        ScreenUtils.clear(Settings.skyColour, true);

        modelBatch.begin(cam);
        modelBatch.render(world.instances, environment);
        modelBatch.end();
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
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        world.dispose();
        modelBatch.dispose();
    }
}
