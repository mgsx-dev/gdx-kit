package net.mgsx.game.examples.iso;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.examples.iso.systems.IsoSystem;
import net.mgsx.game.plugins.DefaultEditorPlugin;

public class IsoExamplePlugin extends EditorPlugin implements DefaultEditorPlugin
{

	@Override
	public void initialize(EditorScreen editor) {
		// TODO Auto-generated method stub
		editor.entityEngine.addSystem(new IsoSystem(editor));
	}

}
