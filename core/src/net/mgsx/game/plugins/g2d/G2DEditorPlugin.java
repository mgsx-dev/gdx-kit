package net.mgsx.game.plugins.g2d;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.plugins.g2d.tools.AddSpriteTool;
import net.mgsx.game.plugins.g2d.tools.SpriteSelector;

@PluginDef(dependencies={G2DPlugin.class})
public class G2DEditorPlugin extends EditorPlugin
{
	@Override
	public void initialize(EditorScreen editor) 
	{
		editor.addTool(new AddSpriteTool(editor));
		editor.addSelector(new SpriteSelector(editor));
	}
}
