package net.mgsx.benchmark;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import net.mgsx.game.core.helpers.NativeService;
import net.mgsx.kit.files.DesktopNativeInterface;

public class BenchmarkBase {
	public static void main (String[] args) 
	{
		NativeService.instance = new DesktopNativeInterface(); 
		
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		new LwjglApplication(new Game(){

			@Override
			public void create() {
				
			}}, config);
	}
}