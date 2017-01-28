package net.mgsx.game.examples.procedural;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.examples.procedural.systems.LSystem2DRenderer;
import net.mgsx.game.plugins.DefaultEditorPlugin;

public class ProceduralEditorPlugin extends EditorPlugin implements DefaultEditorPlugin
{

	@Override
	public void initialize(EditorScreen editor) 
	{
		editor.entityEngine.addSystem(new LSystem2DRenderer(editor.game));
	}

}
