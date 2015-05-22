package com.theodore.endlessgame.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.theodore.endlessgame.MyGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

        config.title = "Endless car game v.01";
        config.width = 1280;
        config.height = 720;
        config.resizable = false;

		new LwjglApplication(new MyGame(), config);
	}
}
