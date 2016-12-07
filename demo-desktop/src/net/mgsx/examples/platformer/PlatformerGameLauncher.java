package net.mgsx.examples.platformer;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import net.mgsx.box2d.editor.desktop.DesktopNativeInterface;
import net.mgsx.game.core.helpers.NativeService;
import net.mgsx.game.examples.platformer.game.PlatformerGame;
import net.mgsx.gdx.pd.PdAudioDesktop;
import net.mgsx.pd.Pd;
import net.mgsx.pd.PdConfiguration;

public class PlatformerGameLauncher 
{
	public static void main (final String[] args) 
	{
		NativeService.instance = new DesktopNativeInterface(); 
		
		Pd.audio = new PdAudioDesktop();
		Pd.audio.create(new PdConfiguration());
		
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		// TODO cause some system instability during debug
//		DisplayMode dm = LwjglApplicationConfiguration.getDesktopDisplayMode();
//		config.width = dm.width;
//		config.height = dm.height;
//		config.fullscreen = true;
		
		new LwjglApplication(new PlatformerGame(), config);
	}
}
