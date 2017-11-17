package net.mgsx.game.examples.openworld;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.examples.openworld.systems.OpenWorldDebugSystem;
import net.mgsx.game.examples.openworld.systems.OpenWorldMapSystem;
import net.mgsx.game.examples.openworld.tools.AddElementTool;
import net.mgsx.game.examples.openworld.tools.AlignMeshTool;
import net.mgsx.game.examples.openworld.tools.CraftTransformTool;
import net.mgsx.game.examples.openworld.tools.MoveElementTool;
import net.mgsx.game.examples.openworld.tools.RemoveElementTool;
import net.mgsx.game.plugins.DefaultEditorPlugin;
import net.mgsx.game.plugins.bullet.system.BulletWorldDebugSystem;
import net.mgsx.game.plugins.editor.systems.SelectionSystem;
import net.mgsx.game.plugins.procedural.systems.HeightFieldDebugSystem;

@PluginDef(
	dependencies={
		OpenWorldPlugin.class
	})
public class OpenWorldEditorPlugin extends EditorPlugin implements DefaultEditorPlugin
{
	@Override
	public void initialize(EditorScreen editor) 
	{
		editor.entityEngine.addSystem(new OpenWorldDebugSystem());
		
		editor.addTool(new AlignMeshTool());
		editor.addTool(new AddElementTool());
		editor.addTool(new RemoveElementTool());
		editor.addTool(new MoveElementTool());
		editor.addTool(new CraftTransformTool());
		
		// XXX
		editor.entityEngine.getSystem(BulletWorldDebugSystem.class).setProcessing(false);
		editor.entityEngine.getSystem(HeightFieldDebugSystem.class).setProcessing(false);
		editor.entityEngine.getSystem(OpenWorldMapSystem.class).setProcessing(false);

		// XXX disable default selectors !
		// best way would be to disable select tool instead ...
		editor.entityEngine.getSystem(SelectionSystem.class).selectors.clear();
		
		editor.getEditorCamera().disable();
	}

}
