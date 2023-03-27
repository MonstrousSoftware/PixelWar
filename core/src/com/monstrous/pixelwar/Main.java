package com.monstrous.pixelwar;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
import com.monstrous.pixelwar.screens.GameScreen;
import com.monstrous.pixelwar.screens.SplashScreen;

public class Main extends Game {

	public static final boolean RELEASE_BUILD = true;

	public static final String VERSION = "version 1.1 (27 March 2023)";
	public static String PREFERENCES_NAME = "pixelwar";

	public Music music;
	public Preferences preferences;
	private float musicVolume;
	private Sounds sounds;

	@Override
	public void create () {
		if(RELEASE_BUILD)
			Gdx.app.setLogLevel(Application.LOG_ERROR);
		else
			Gdx.app.setLogLevel(Application.LOG_DEBUG);

		preferences = Gdx.app.getPreferences(PREFERENCES_NAME);
		musicVolume = preferences.getFloat("musicVolume", 0.8f);
		sounds = new Sounds();

		music = Gdx.audio.newMusic(Gdx.files.internal("music/Title Screen.wav"));
		music.setLooping(true);
		music.setVolume(musicVolume);
		if(Gdx.app.getType() != Application.ApplicationType.WebGL)
			music.play();

		if(RELEASE_BUILD)
			setScreen( new SplashScreen(this) );
		else
			setScreen( new GameScreen(this, false) );
	}


	
	@Override
	public void dispose () {
		stopMusic();

		// save sound settings for next time
		preferences.putFloat("musicVolume", musicVolume);   // save
		preferences.flush();
		sounds.dispose();
	}

	public void startMusic(String name) {
		if(music != null)
			return;
		music = Gdx.audio.newMusic(Gdx.files.internal(name));
		music.setLooping(true);
		music.setVolume(musicVolume);
		music.play();
	}

	public void stopMusic() {
		if(music == null)
			return;
		music.stop();
		music.dispose();
		music = null;
	}



	public float getMusicVolume() {
		return musicVolume;
	}

	public void setMusicVolume(float musicVolume) {
		this.musicVolume = musicVolume;
		music.setVolume(musicVolume);
	}
}
