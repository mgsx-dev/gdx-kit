package net.mgsx.game.examples.war;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.examples.war.components.ZoneComponent;
import net.mgsx.game.examples.war.editors.PlayerEditor;
import net.mgsx.game.examples.war.editors.ZoneEditor;
import net.mgsx.game.plugins.DefaultEditorPlugin;

@PluginDef(dependencies=WarPlugin.class)
public class WarEditorPlugin extends EditorPlugin implements DefaultEditorPlugin
{

	@Override
	public void initialize(EditorScreen editor) {
		editor.registry.registerPlugin(ZoneComponent.class, new ZoneEditor(editor));
		editor.registry.addGlobalEditor("War", new PlayerEditor(editor));
	}

}
