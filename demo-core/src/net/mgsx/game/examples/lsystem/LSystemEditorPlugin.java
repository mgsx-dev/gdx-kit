package net.mgsx.game.examples.lsystem;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.examples.lsystem.components.LSystem3D;
import net.mgsx.game.examples.lsystem.editors.LSystemEditor;
import net.mgsx.game.examples.lsystem.systems.LSystem2DRenderer;
import net.mgsx.game.examples.lsystem.systems.LSystem3DRender;
import net.mgsx.game.plugins.DefaultEditorPlugin;

public class LSystemEditorPlugin extends EditorPlugin implements DefaultEditorPlugin
{

	@Override
	public void initialize(EditorScreen editor) 
	{
		editor.entityEngine.addSystem(new LSystem2DRenderer(editor.game));
		editor.entityEngine.addSystem(new LSystem3DRender(editor.game));
		
		editor.registry.registerPlugin(LSystem3D.class, new LSystemEditor(editor));
	}

}
