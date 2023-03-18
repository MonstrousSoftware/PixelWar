package com.monstrous.pixelwar;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class Main extends Game {

	
	@Override
	public void create () {
		setScreen( new SplashScreen(this) );
	}


	
	@Override
	public void dispose () {
	}
}
