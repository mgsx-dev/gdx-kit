package net.mgsx.game.examples.war;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.examples.war.components.ZoneComponent;
import net.mgsx.game.examples.war.editors.PlayerEditor;
import net.mgsx.game.examples.war.editors.ZoneEditor;
import net.mgsx.game.plugins.editor.KitEditorPlugin;
import net.mgsx.game.plugins.editor.systems.EditorSystem;

@PluginDef(dependencies={WarPlugin.class, KitEditorPlugin.class})
public class WarEditorPlugin extends EditorPlugin
{
	@Override
	public void initialize(EditorScreen editor) {
		editor.registry.registerPlugin(ZoneComponent.class, new ZoneEditor(editor));
		editor.entityEngine.getSystem(EditorSystem.class)
			.addGlobalEditor("War", new PlayerEditor());
	}
}
