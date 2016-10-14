package net.mgsx.box2d.editor.desktop;

import java.io.File;

import javax.swing.JApplet;
import javax.swing.JFileChooser;

import net.mgsx.core.NativeService;
import net.mgsx.core.NativeService.DialogCallback;
import net.mgsx.core.NativeService.NativeServiceInterface;
import net.mgsx.plugins.box2d.Box2DEditor;
import net.mgsx.plugins.sprite.SpritePlugin;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopLauncher 
{
	public static class DesktopNativeInterface implements NativeServiceInterface
	{
		// TODO replace this native interface either by attaching to LibGDX applet or pure LibGDX browser ...

		public String path = ".";
		
		private void openDialog(final DialogCallback callback, boolean save){
			JApplet applet = new JApplet(); // TODO fail safe
			final JFileChooser fc = new JFileChooser(new File(path));
			int r = save ? fc.showSaveDialog(applet) : fc.showOpenDialog(applet);
			if(r == JFileChooser.APPROVE_OPTION){
				final File file = fc.getSelectedFile();
				path = file.getParent();
				Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
						callback.selected(Gdx.files.absolute(file.getAbsolutePath()));
					}
				});
			}
			applet.destroy();
		}
		@Override
		public void openSaveDialog(final DialogCallback callback) {
			openDialog(callback, true);
		}
		
		@Override
		public void openLoadDialog(final DialogCallback callback) {
			openDialog(callback, false);
		}
	};
	
	public static void main (String[] args) {
		
		DesktopNativeInterface nativeInsterface = new DesktopNativeInterface();
		NativeService.instance = nativeInsterface; 
		
		File file = null;
		if(args.length > 0){
			file = new File(args[0]);
			
			if(file.exists())
			{
				nativeInsterface.path = file.getParent();
			}
			else
			{
				nativeInsterface.path = new File(".").getPath();
				System.err.println("warning : file not found " + file.getPath());
			}
		}
		
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		Box2DEditor editor = new Box2DEditor(file.getAbsolutePath());
		editor.registerPlugin(new SpritePlugin());
		new LwjglApplication(editor, config);
	}
}
