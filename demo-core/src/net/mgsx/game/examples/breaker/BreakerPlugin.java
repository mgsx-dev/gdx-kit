package net.mgsx.game.examples.breaker;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.examples.breaker.system.BreakerGameSystem;
import net.mgsx.game.examples.breaker.tools.BrickTool;
import net.mgsx.game.examples.breaker.tools.PlayTool;
import net.mgsx.game.plugins.DefaultEditorPlugin;
import net.mgsx.game.plugins.box2d.Box2DEditorPlugin;

@PluginDef(dependencies={
		Box2DEditorPlugin.class
	})
public class BreakerPlugin extends EditorPlugin implements DefaultEditorPlugin {

	@Override
	public void initialize(EditorScreen editor) 
	{
		
		editor.entityEngine.addSystem(new BreakerGameSystem());
		
		editor.addTool(new PlayTool());
		editor.addTool(new BrickTool());
	}

}
