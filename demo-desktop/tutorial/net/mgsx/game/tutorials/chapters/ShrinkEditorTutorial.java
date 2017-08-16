package net.mgsx.game.tutorials.chapters;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import net.mgsx.game.core.EditorApplication;
import net.mgsx.game.core.EditorConfiguration;
import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.plugins.g3d.G3DEditorPlugin;
import net.mgsx.game.tutorials.Tutorial;

/**@md

You will learn in this tutorial how to narrow KIT plugin configuration
to have a specific editor.

For illustration, we will just include what we need to preview our 3D models.
This can be helpful to analyse key frames in animations, mesh density and so on.

@md*/
@Tutorial(id="shrink-editor", title="Shrink the KIT Editor")
public class ShrinkEditorTutorial {

	/**@md
	
	First we create a new plugin and configure its dependencies.
	
	It just depends on {@link G3DEditorPlugin} which include all tools, components and systems
	we need.
	
	@md*/
	static
	//@code
	@PluginDef(dependencies={
		G3DEditorPlugin.class
	})
	public class ModelViewerPlugin extends EditorPlugin
	{
		@Override
		public void initialize(EditorScreen editor) {
			// nothing more
		}
	}
	//@code
	
	
	/**@md
	
	For sake of simplicity we don't create a cross platform game with separated launcher
	but merge all this in a desktop launcher.
	
	Here we just have one plugin.
	
	@md*/
	static
	//@code
	public class ModelViewerDesktopLauncher
	{
		public static void main(String[] args) {
			EditorConfiguration editorConfig = new EditorConfiguration();
			editorConfig.registry.registerPlugin(new ModelViewerPlugin());
			new LwjglApplication(new EditorApplication(editorConfig), new LwjglApplicationConfiguration());
		}
	}
	//@code
	
}
