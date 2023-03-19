package com.monstrous.pixelwar;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Vector3;

public class MyCamController extends CameraInputController {

    public static final float MOVE_SPEED = 20f;

    private final Vector3 tmpV1 = new Vector3();
    private final Vector3 tmpV2 = new Vector3();

    public MyCamController(Camera camera) {
        super(camera);
    }

    @Override
    public void update () {
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
