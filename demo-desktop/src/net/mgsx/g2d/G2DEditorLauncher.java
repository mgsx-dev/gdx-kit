package net.mgsx.g2d;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import net.mgsx.box2d.editor.desktop.DesktopNativeInterface;
import net.mgsx.game.core.EditorApplication;
import net.mgsx.game.core.EditorConfiguration;
import net.mgsx.game.core.helpers.NativeService;
import net.mgsx.game.plugins.ashley.AshleyEditorPlugin;
import net.mgsx.game.plugins.core.CoreEditorPlugin;
import net.mgsx.game.plugins.g2d.G2DEditorPlugin;

public class G2DEditorLauncher {

	public static void main (String[] args) 
	{
		NativeService.instance = new DesktopNativeInterface(); 
		
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		EditorConfiguration editConfig = new EditorConfiguration();
		
		editConfig.plugins.add(new CoreEditorPlugin());
		editConfig.plugins.add(new AshleyEditorPlugin());
		editConfig.plugins.add(new G2DEditorPlugin());
		
		editConfig.root = args.length > 0 ? args[0] : null;
		editConfig.path = args.length > 1 ? args[1] : null;
		
		new LwjglApplication(new EditorApplication(editConfig), config);
	}
}
