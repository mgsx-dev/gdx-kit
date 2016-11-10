package net.mgsx.box2d.editor.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import net.mgsx.game.plugins.g3d.G3DEditorPlugin;


public class G3DEditorLauncher 
{
	public static void main (String[] args) 
	{
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		DesktopEditor editor = new DesktopEditor();
		editor.registerPlugin(new G3DEditorPlugin());
		new LwjglApplication(editor, config);
	}
}
