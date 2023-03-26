package com.monstrous.pixelwar;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Disposable;

public class MiniMap implements Disposable {
    private int mapSize;       // in pixels
    private int border;       // in pixels

    private OrthographicCamera orthoCam;
    private ModelBatch modelBatch;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private FrameBuffer fboMiniMap;
    private Texture mapFrame;
    private Rectangle miniMapRect;
    private Rectangle mapFrameRect;
    private int viewWidth;
    private int viewHeight;
    private Vector3 screenCorners[];		// XZ of screen corners in world coordinates
    private Texture heightMap;

    public MiniMap(float worldWidth, float worldDepth, float waterLevel) {

        mapSize = 200;  // pixels
        border = 10;    // pixels
        fboMiniMap = new FrameBuffer(Pixmap.Format.RGBA8888, (int)mapSize, (int)mapSize, true);
        mapFrame = new Texture(Gdx.files.internal("frame220.png"));
        miniMapRect = new Rectangle();
        mapFrameRect = new Rectangle();
        modelBatch = new ModelBatch();
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        // ortho cam used for mini map
        float orthoCamHeight = 100f;
        // todo how to keep square minimap when terrain is not square. E.g. black bars....
        //  scale the ortho cam so the largest terrain dimension fits on`
        float max = worldWidth;
        if(worldDepth > max)
            max = worldDepth;
        orthoCam = new OrthographicCamera( max, max );
        orthoCam.position.set(0,orthoCamHeight,0);
        orthoCam.lookAt(0,0,0);
        orthoCam.up.set(0,0,1);
        orthoCam.near = 1;
        // use far clipping plane to clip terrain below water level
        orthoCam.far = 500; //orthoCamHeight-waterLevel;
        orthoCam.update();

        screenCorners = new Vector3[4];
        for(int i = 0; i < 4; i++)
            screenCorners[i] = new Vector3();

        heightMap = new Texture(Gdx.files.internal("noiseTexture.png"));
    }

    public void resize(int viewWidth, int viewHeight) {

        this.viewWidth = viewWidth;
        this.viewHeight = viewHeight;

        miniMapRect = new Rectangle();
        miniMapRect.width = mapSize;
        miniMapRect.height = mapSize;
        miniMapRect.x = viewWidth-(miniMapRect.width+border);
        miniMapRect.y = viewHeight-(miniMapRect.height+border);

        float wf = mapSize + 2*border;
        mapFrameRect = new Rectangle();
        mapFrameRect.width = wf;
        mapFrameRect.height = wf;
        mapFrameRect.x = viewWidth-wf;
        mapFrameRect.y = viewHeight-wf;

        batch.getProjectionMatrix().setToOrtho2D(0, 0, viewWidth, viewHeight);
    }


    public void update( Camera cam , World world, Environment environment) {

        //setScreenCorners(cam);

        fboMiniMap.begin();
            Gdx.gl.glClearColor(.2f, .5f, .8f, 1);		// water colour
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

            batch.begin();
            batch.draw(heightMap,0,0, viewWidth, viewHeight);
            batch.end();
            modelBatch.begin(orthoCam);
            world.render(modelBatch, environment, true);
            modelBatch.end();


            // render fog of war over the minimap with default blending

            // show view frustum as a trapezium shape overlay on the mini map
            Vector3 planePoints[] = cam.frustum.planePoints;
            screenCorners[0].set(planePoints[0]);
            screenCorners[1].set(planePoints[1]);
            screenCorners[2].set(planePoints[4]);
            screenCorners[3].set(planePoints[5]);

            for(int i = 0 ; i < 4; i++)
                orthoCam.project(screenCorners[i]);

            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(Color.GREEN);
            for (int i = 0; i < 3; i++)
                shapeRenderer.line(screenCorners[i].x, screenCorners[i].y, screenCorners[i+1].x, screenCorners[i+1].y);
            shapeRenderer.line(screenCorners[3].x, screenCorners[3].y, screenCorners[0].x, screenCorners[0].y);
            shapeRenderer.end();

        fboMiniMap.end();
    }

    public void render() {
        batch.begin();
		Sprite s = new Sprite(fboMiniMap.getColorBufferTexture());
		s.flip(true, false); // coordinate system in buffer differs from screen

		batch.draw(mapFrame, mapFrameRect.x,  mapFrameRect.y,  mapFrameRect.width,  mapFrameRect.height);
		batch.draw(s, miniMapRect.x,  miniMapRect.y,  miniMapRect.width,  miniMapRect.height);

		batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        modelBatch.dispose();
        mapFrame.dispose();
    }
}
