package net.mgsx.g3d;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import net.mgsx.game.core.EditorApplication;
import net.mgsx.game.core.EditorConfiguration;
import net.mgsx.game.plugins.ashley.AshleyEditorPlugin;
import net.mgsx.game.plugins.g3d.G3DEditorPlugin;
import net.mgsx.game.plugins.graphics.GraphicsEditorPlugin;

public class G3DEditorLauncher {

	public static void main (String[] args) 
	{
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		EditorConfiguration editConfig = new EditorConfiguration();
		
		editConfig.plugins.add(new AshleyEditorPlugin());
		editConfig.plugins.add(new GraphicsEditorPlugin());
		editConfig.plugins.add(new G3DEditorPlugin());
		
		editConfig.path = args.length > 0 ? args[0] : null;
		
		new LwjglApplication(new EditorApplication(editConfig), config);
	}
}
