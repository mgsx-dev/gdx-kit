package net.mgsx.game.examples.circuit;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.examples.circuit.model.CircuitModel;
import net.mgsx.game.examples.circuit.systems.CircuitRenderSystem;
import net.mgsx.game.examples.circuit.tools.CircuitTool;
import net.mgsx.game.plugins.DefaultEditorPlugin;
import net.mgsx.game.plugins.g3d.systems.G3DCullingSystem;

public class CircuitEditorPlugin extends EditorPlugin implements DefaultEditorPlugin {

	@Override
	public void initialize(EditorScreen editor) {
		editor.registry.registerModel(new CircuitModel());
		editor.entityEngine.addSystem(new CircuitRenderSystem());
		editor.addTool(new CircuitTool(editor));
		
		editor.entityEngine.getSystem(G3DCullingSystem.class).culling = false;
	}

}
