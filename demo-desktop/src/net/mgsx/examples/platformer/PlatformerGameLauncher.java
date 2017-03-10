package net.mgsx.examples.platformer;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import net.mgsx.game.core.helpers.NativeService;
import net.mgsx.game.examples.platformer.game.PlatformerGame;
import net.mgsx.kit.files.DesktopNativeInterface;

public class PlatformerGameLauncher 
{
	public static void main (final String[] args) 
	{
		NativeService.instance = new DesktopNativeInterface(); 
		
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		// TODO cause some system instability during debug
//		DisplayMode dm = LwjglApplicationConfiguration.getDesktopDisplayMode();
//		config.width = dm.width;
//		config.height = dm.height;
//		config.fullscreen = true;
		
		new LwjglApplication(new PlatformerGame(), config);
	}
}
