package net.mgsx.benchmark;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import net.mgsx.box2d.editor.desktop.DesktopNativeInterface;
import net.mgsx.game.core.helpers.NativeService;

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