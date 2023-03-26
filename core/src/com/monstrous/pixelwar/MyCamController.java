package com.monstrous.pixelwar;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Vector2;
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
//        this.rotateButton = Input.Buttons.RIGHT;
//        this.translateButton = Input.Buttons.FORWARD;
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

        // move camera along with the target
        tmpV1.set(target).sub(prevTarget);      // change in target position
        camera.position.add(tmpV1);
        prevTarget.set(target);
        camera.update();

    }



}
