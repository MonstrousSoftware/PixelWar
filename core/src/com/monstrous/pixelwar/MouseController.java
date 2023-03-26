package com.monstrous.pixelwar;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.monstrous.pixelwar.screens.GameScreen;

public class MouseController extends GestureDetector {
    private static GameScreen gameScreen;


    protected static class MyGestureListener extends GestureDetector.GestureAdapter {
        @Override
        public boolean tap (float x, float y, int count, int button) {

            return gameScreen.mouseDown(x, y);
        }

    };

    public MouseController(GameScreen gameScreen) {
        super(new MyGestureListener());
        this.gameScreen = gameScreen;
    }


    @Override
    public boolean keyDown(int keycode) {
        boolean ok = super.keyDown(keycode);
        if(keycode == Input.Keys.ESCAPE)
            gameScreen.pressedEscape();
        return ok;
    }

    @Override
    public boolean keyTyped(char character) {
        if(character == '\t')
            gameScreen.pressedTab();
        return super.keyTyped(character);
    }
}
