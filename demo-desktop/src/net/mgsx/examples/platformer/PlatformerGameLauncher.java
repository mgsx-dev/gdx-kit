package net.mgsx.examples.platformer;

import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import net.mgsx.box2d.editor.desktop.DesktopNativeInterface;
import net.mgsx.game.core.helpers.NativeService;
import net.mgsx.game.examples.platformer.PlatformerGame;

public class PlatformerGameLauncher 
{
	public static void main (final String[] args) 
	{
		NativeService.instance = new DesktopNativeInterface(); 
		
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		DisplayMode dm = LwjglApplicationConfiguration.getDesktopDisplayMode();
		config.width = dm.width;
		config.height = dm.height;
		config.fullscreen = true;
		
		new LwjglApplication(new PlatformerGame(), config);
	}
}