package com.monstrous.pixelwar;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Vector3;

public class MyCamController extends CameraInputController {

    public static final float MOVE_SPEED = 20f;

    private GameObject targetObject;
    private Vector3 prevTarget;
    private final Vector3 tmpV1 = new Vector3();
    private final Vector3 tmpV2 = new Vector3();

    public MyCamController(Camera camera) {

        super(camera);
        targetObject = null;
        prevTarget = new Vector3();
        this.rotateButton = Input.Buttons.RIGHT;
        this.translateButton = Input.Buttons.FORWARD;
    }

    public void followGameObject( GameObject go ) {
//        tmpV1.set(go.position).sub(target);
//        camera.position.add(tmpV1);
        camera.position.set(target.x, 10f, target.z - 20);
        camera.up.set(Vector3.Y);
        camera.lookAt(target);
        camera.update();
        this.targetObject = go;
    }

    @Override
    public void update () {

        if(targetObject != null)
            target.set(targetObject.position);

        tmpV1.set(target).sub(prevTarget);
        camera.position.add(tmpV1);
        prevTarget.set(target);
        camera.update();

        // WASD are modified to move across the terrain
        if (rotateRightPressed || rotateLeftPressed || forwardPressed || backwardPressed) {
            final float delta = Gdx.graphics.getDeltaTime();

            if (rotateRightPressed) {
                camera.translate(tmpV1.set(0,0, delta*MOVE_SPEED));
                if (forwardTarget) target.add(tmpV1);
            }
            if (rotateLeftPressed) {
                camera.translate(tmpV1.set(0,0,-delta*MOVE_SPEED));
                if (forwardTarget) target.add(tmpV1);
            }
            if (forwardPressed) {
                camera.translate(tmpV1.set(-delta*MOVE_SPEED, 0, 0));
                if (forwardTarget) target.add(tmpV1);
            }
            if (backwardPressed) {
                camera.translate(tmpV1.set(delta*MOVE_SPEED, 0, 0));
                if (forwardTarget) target.add(tmpV1);
            }
            if (autoUpdate) camera.update();
        }
    }



}
