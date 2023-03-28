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


    public MyCamController(Camera camera) {

        super(camera);
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
        if(shakeTimer > 0) {
            float dx = 0; //shakeTimer * (float)Math.cos(shakeTimer*8f);
            float dy = 0.5f * shakeTimer * (float) Math.sin(shakeTimer * 37f);
            shakeOffset.set(dx, dy, dx);
            shakeTimer -= deltaTime;
            if(shakeTimer <= 0)
                shakeOffset.set(0,0,0);
        }

        // translate camera with same vector that target has moved
        tmpV1.set(target).sub(prevTarget);      // change in target position
        camera.position.add(tmpV1);
        camera.position.add(shakeOffset);
        prevTarget.set(target);                 // remember for next frame
        camera.update();
    }



}
