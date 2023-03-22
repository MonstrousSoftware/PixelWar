package com.monstrous.pixelwar;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;

public class MouseController extends InputAdapter {
    private GameScreen gameScreen;
    public boolean controlIsDown;       // still needed?
    public boolean shiftIsDown;         // e.g. to shift-click to add to selection
    public Vector2 corner1;
    public Vector2 corner2;
    public int pressedButton;

    public MouseController(GameScreen gameScreen) {

        this.gameScreen = gameScreen;
        controlIsDown = false;
        shiftIsDown = false;
        corner1 = new Vector2();
        corner2 = new Vector2();
    }


    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
       // Gdx.app.log("touchDown", "button: "+button + "x:"+ screenX+" y:"+screenY);
        corner1.set(screenX, screenY);
        pressedButton = button;
        return gameScreen.mouseDown(screenX, screenY, button);
    }

//    @Override
//    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
//        pressedButton = -1;
//        return gameScreen.mouseUp(screenX, screenY, button);
//    }

//    @Override
//    public boolean touchDragged(int screenX, int screenY, int pointer) {
//        //Gdx.app.log("touchDragged", "ptr: "+pointer + "x:"+ screenX+" y:"+screenY);
//
//        if(controlIsDown)   // if control is pressed, dragging moves the camera
//            return false;
//        if(pressedButton == Input.Buttons.RIGHT)        // right-drag is for camera control
//            return false;
//
//        // if control is not pressed, dragging is used for rectangle selection
//        corner2.set(screenX, screenY);
//        return gameScreen.mouseDrag(corner1, corner2);
//    }
//
//
//    @Override
//    public boolean mouseMoved(int screenX, int screenY) {
//        // note: mouseMoved only works when no buttons are pressed
//        gameScreen.mouseMoved(screenX, screenY);
//        return true;
//    }

    @Override
    public boolean keyDown(int keycode) {
        boolean ok = super.keyDown(keycode);
        if(keycode == Input.Keys.CONTROL_LEFT)
            controlIsDown = true;
        if(keycode == Input.Keys.SHIFT_LEFT || keycode == Input.Keys.SHIFT_RIGHT)
            shiftIsDown = true;
        if(keycode == Input.Keys.ESCAPE)
            gameScreen.pressedEscape();
        return ok;
    }

    @Override
    public boolean keyUp(int keycode) {
        boolean ok = super.keyDown(keycode);
        if(keycode == Input.Keys.CONTROL_LEFT)
            controlIsDown = false;
        if(keycode == Input.Keys.SHIFT_LEFT || keycode == Input.Keys.SHIFT_RIGHT)
            shiftIsDown = false;
        return ok;
    }


}
