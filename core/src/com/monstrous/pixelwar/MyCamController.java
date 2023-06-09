package com.monstrous.pixelwar;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Vector3;

public class MyCamController extends CameraInputController {

    private GameObject targetObject;                    // object that is being followed
    private final Vector3 prevTarget;                         // target position in previous frame
    private boolean transitionMode;                     // travelling between objects
    private final Vector3 start;                              // start of transition
    private float lerpFactor;                           // 0 .. 1
    private final Vector3 tmpV1 = new Vector3();
    private final Vector3 shakeOffset;
    private float shakeTimer;
    private World world;


    public MyCamController(Camera camera, World world) {

        super(camera);
        this.world = world;
        targetObject = null;
        prevTarget = new Vector3();
        transitionMode = false;
        start = new Vector3();
        shakeOffset = new Vector3(0,0,0);
        shakeTimer = 0;
    }

    public void followGameObject( GameObject go ) {
        if(targetObject == null) {
            camera.position.set(target.x, 10f, target.z - 20);
            camera.up.set(Vector3.Y);
            camera.lookAt(target);
            camera.update();
        }
        this.targetObject = go;
        transitionMode = true;
        start.set(target);
        lerpFactor = 0f;
    }

    // shake the camera briefly
    public void shake() {
        shakeTimer = 0.5f;
    }

    // smoothing function to slow down the movement at the start and end of the transition
    private float smooth(float a) {
        return a * a * (3 - 2 * a);
    }

    @Override
    public void update () {
        final float deltaTime = Gdx.graphics.getDeltaTime();

        if(transitionMode){
            // move in 1 second from one unit to the next unit, regardless of the distance in between
            lerpFactor += deltaTime;
            if(lerpFactor > 1f){
                lerpFactor = 1f;
                transitionMode = false;
            }
            target.set(targetObject.position).sub(start).scl(smooth(lerpFactor)).add(start);    // interpolate between start and targetObject.position
        }
        else {
            if (targetObject != null)
                target.set(targetObject.position);
        }
        if(shakeTimer > 0) {    // shake Y coordinate with a sine wave of diminishing amplitude
            float dy = 0.5f * shakeTimer * (float) Math.sin(shakeTimer * 37f);
            shakeOffset.set(0, dy, 0);
            shakeTimer -= deltaTime;
            if(shakeTimer <= 0)
                shakeOffset.set(0,0,0);
        }

        // translate camera with same vector that target has moved
        tmpV1.set(target).sub(prevTarget);      // change in target position
        camera.position.add(tmpV1);
        camera.position.add(shakeOffset);


        // prevent camera from going under terrain
        float ht = world.terrain.getHeight(camera.position.x, camera.position.z);
        while(camera.position.y < ht+5f) {
            Gdx.app.log("camera underground", ""+camera.position.y);
            tmpV1.set(camera.direction).crs(camera.up).y = 0f;
            camera.rotateAround(target, tmpV1.nor(), -0.01f * rotateAngle);
        }
        prevTarget.set(target);                 // remember for next frame
        camera.update();
    }

    @Override
    public boolean zoom (float amount) {
        if (!alwaysScroll && activateKey != 0 && !activatePressed) return false;
        float dst = tmpV1.set(camera.position).sub(target).len();
        if(dst < 10 && amount > 0)  // max zoom in
            return true;
        if(dst > 150 && amount < 0) // max zoom out
            return true;

        Gdx.app.debug("zoom", "dst: "+ dst);

        camera.translate(tmpV1.set(camera.direction).scl(Math.signum(amount)*dst*0.1f));
        if (scrollTarget) target.add(tmpV1);
        if (autoUpdate) camera.update();
        return true;
    }


}
