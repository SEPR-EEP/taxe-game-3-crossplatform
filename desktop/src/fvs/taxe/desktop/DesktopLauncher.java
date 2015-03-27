package fvs.taxe.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import fvs.taxe.TaxeGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		//Set window size
		config.height = TaxeGame.HEIGHT;
		config.width = TaxeGame.WIDTH;
		config.title = "TaxE";
//		config.resizable = false;
		//config.fullscreen = true;
		new LwjglApplication(new TaxeGame(), config);
	}
}
