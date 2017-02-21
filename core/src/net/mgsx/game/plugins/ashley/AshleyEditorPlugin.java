package net.mgsx.game.plugins.ashley;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.plugins.ashley.editors.AshleyEntitiesEditor;
import net.mgsx.game.plugins.ashley.editors.AshleySystemsEditor;
import net.mgsx.game.plugins.ashley.systems.AshleyProfilerSystem;
import net.mgsx.game.plugins.editor.systems.EditorSystem;

public class AshleyEditorPlugin extends EditorPlugin
{
	@Override
	public void initialize(EditorScreen editor) 
	{
		editor.entityEngine.getSystem(EditorSystem.class)
			.addGlobalEditor("Entities", new AshleyEntitiesEditor());
		editor.entityEngine.getSystem(EditorSystem.class)
			.addGlobalEditor("Systems", new AshleySystemsEditor());
//		editor.registry.addGlobalEditor("Entities", new AshleyEntitiesEditor());
//		editor.registry.addGlobalEditor("Systems", new AshleySystemsEditor());
		
		editor.entityEngine.addSystem(new AshleyProfilerSystem());
	}
}
