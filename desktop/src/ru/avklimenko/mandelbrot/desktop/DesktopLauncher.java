package ru.avklimenko.mandelbrot.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import ru.avklimenko.mandelbrot.Config;
import ru.avklimenko.mandelbrot.MandelbrotGame;


public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = Config.TITLE;
		config.width = Config.SIZE;
		config.height = Config.SIZE;
		config.resizable = false;
		config.depth = 16;
		new LwjglApplication(new MandelbrotGame(), config);
	}
}
