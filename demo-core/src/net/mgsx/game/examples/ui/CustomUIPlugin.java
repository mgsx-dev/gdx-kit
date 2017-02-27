package net.mgsx.game.examples.ui;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.plugins.ui.WidgetTool;

// TODO put in tutorials !
public class CustomUIPlugin extends EditorPlugin
{

	@Override
	public void initialize(EditorScreen editor) {
		editor.addTool(new WidgetTool("Custom UI", editor, new MySliderFactory()){});
	}

}
