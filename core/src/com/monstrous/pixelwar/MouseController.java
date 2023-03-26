package com.monstrous.pixelwar;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;
import com.monstrous.pixelwar.screens.GameScreen;

public class MouseController extends InputAdapter {
    private GameScreen gameScreen;
    public int pressedButton;

    public MouseController(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
    }


    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if(button != Input.Buttons.LEFT)
            return false;

        pressedButton = button;
        return gameScreen.mouseDown(screenX, screenY, button);
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
