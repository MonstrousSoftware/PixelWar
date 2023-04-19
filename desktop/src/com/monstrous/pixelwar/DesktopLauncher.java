package com.monstrous.pixelwar;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.monstrous.pixelwar.Main;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		//config.setOpenGLEmulation(Lwjgl3ApplicationConfiguration.GLEmulation.GL30, 3, 3);

		config.setForegroundFPS(60);
		config.setTitle(Settings.title);
		config.setWindowedMode(1200, 600);
		config.setWindowIcon("icons/tank.png");
		config.setBackBufferConfig(8, 8, 8, 8,16, 0, 4);		// anti-aliasing 4 samples

		new Lwjgl3Application(new Main(), config);
	}
}
