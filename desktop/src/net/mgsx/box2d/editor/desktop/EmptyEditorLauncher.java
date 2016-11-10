package net.mgsx.box2d.editor.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;


public class EmptyEditorLauncher 
{
	public static void main (String[] args) 
	{
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		DesktopEditor editor = new DesktopEditor();
		new LwjglApplication(editor, config);
	}
}
