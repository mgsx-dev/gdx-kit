package net.mgsx.examples.platformer;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import net.mgsx.box2d.editor.desktop.DesktopNativeInterface;
import net.mgsx.game.core.GameEngine;
import net.mgsx.game.core.NativeService;
import net.mgsx.game.examples.platformer.core.PlatformerPlugin;

public class PlatformerGameLauncher 
{
	public static void main (String[] args) 
	{
		NativeService.instance = new DesktopNativeInterface(); 
		
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		GameEngine game = new GameEngine();

		// TODO how to register non editor plugin (need proper separation)
		// plugins configuration (order is important)
		//
		game.registerPlugin(new PlatformerPlugin());
		
		new LwjglApplication(game, config);
	}
}
