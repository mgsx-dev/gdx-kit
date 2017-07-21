package net.mgsx.game.plugins.p3d;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.plugins.p3d.tools.P3DImportTool;

@PluginDef(dependencies=Particle3DPlugin.class)
public class Particle3DEditorPlugin extends EditorPlugin
{
	@Override
	public void initialize(EditorScreen editor) {
		editor.addTool(new P3DImportTool(editor));
	}
	
}
