package net.mgsx.game.examples.iso;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.examples.iso.systems.GasRenderSystem;
import net.mgsx.game.examples.iso.systems.IsoSystem;
import net.mgsx.game.plugins.DefaultEditorPlugin;
import net.mgsx.game.plugins.g3d.G3DPlugin;

@PluginDef(dependencies=G3DPlugin.class)
public class IsoExamplePlugin extends EditorPlugin implements DefaultEditorPlugin
{

	@Override
	public void initialize(EditorScreen editor) {
		editor.entityEngine.addSystem(new IsoSystem(editor));
		editor.entityEngine.addSystem(new GasRenderSystem(editor));
	}

}
