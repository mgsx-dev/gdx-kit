package net.mgsx.game.examples.quantum;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.examples.quantum.systems.LinkRenderer;
import net.mgsx.game.examples.quantum.systems.PlanetRenderer;
import net.mgsx.game.examples.quantum.systems.QuantumSimulation;
import net.mgsx.game.examples.quantum.tools.LinkTool;
import net.mgsx.game.examples.quantum.tools.MapExportTool;
import net.mgsx.game.examples.quantum.tools.MapImportTool;
import net.mgsx.game.plugins.DefaultEditorPlugin;

public class QuantumEditorPlugin extends EditorPlugin implements DefaultEditorPlugin
{

	@Override
	public void initialize(EditorScreen editor) 
	{
		editor.addTool(new MapImportTool(editor));
		editor.addTool(new MapExportTool(editor));
		editor.addTool(new LinkTool(editor));
		
		editor.entityEngine.addSystem(new QuantumSimulation());
		
		editor.entityEngine.addSystem(new PlanetRenderer(editor));
		editor.entityEngine.addSystem(new LinkRenderer(editor));
	}

}
