package com.monstrous.pixelwar;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
import com.monstrous.pixelwar.screens.GameScreen;
import com.monstrous.pixelwar.screens.SplashScreen;

public class Main extends Game {

	public static final String VERSION = "version 0.2 (23 March 2023)";
	public static final boolean RELEASE_BUILD = true;
	public static String PREFERENCES_NAME = "pixelwar";

	public Music music;
	public Preferences preferences;
	public float soundVolume;
	private float musicVolume;

	@Override
	public void create () {
		preferences = Gdx.app.getPreferences(PREFERENCES_NAME);
		soundVolume = preferences.getFloat("soundVolume", 1.0f);
		musicVolume = preferences.getFloat("musicVolume", 0.8f);

		music = Gdx.audio.newMusic(Gdx.files.internal("music/Title Screen.wav"));
		music.setLooping(true);
		music.setVolume(musicVolume);
		if(Gdx.app.getType() != Application.ApplicationType.WebGL)
			music.play();

		setScreen( new GameScreen(this, false) );
		//setScreen( new SplashScreen(this) );
	}


	
	@Override
	public void dispose () {
		stopMusic();

		// save sound settings for next time
		preferences.putFloat("soundVolume", soundVolume);   // save
		preferences.putFloat("musicVolume", musicVolume);   // save
		preferences.flush();
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

	public float getSoundVolume() {
		return soundVolume;
	}

	public void setSoundVolume(float soundVolume) {
		this.soundVolume = soundVolume;
	}

	public float getMusicVolume() {
		return musicVolume;
	}

	public void setMusicVolume(float musicVolume) {
		this.musicVolume = musicVolume;
		music.setVolume(musicVolume);
	}
}
