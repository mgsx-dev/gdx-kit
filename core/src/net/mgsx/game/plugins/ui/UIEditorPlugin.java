package net.mgsx.game.plugins.ui;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.plugins.EditorPlugin;

public class UIEditorPlugin extends EditorPlugin
{

	@Override
	public void initialize(EditorScreen editor) {
		// FIXME not in editor ...
		editor.entityEngine.addSystem(new WidgetSystem(editor));
		
		editor.addTool(new SliderTool(editor));
	}

}
