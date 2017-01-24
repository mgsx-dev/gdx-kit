package net.mgsx.game.examples.rts;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.examples.rts.tools.CreateRoadTool;
import net.mgsx.game.examples.rts.tools.MoveToPlanetTool;
import net.mgsx.game.examples.rts.tools.PlanetCreatorTool;
import net.mgsx.game.examples.rts.tools.PlanetSelector;
import net.mgsx.game.examples.rts.tools.RtsGameTool;
import net.mgsx.game.examples.rts.tools.SolarCreatorTool;
import net.mgsx.game.examples.rts.tools.SpawnTool;

@PluginDef(dependencies=RtsPlugin.class)
public class RtsEditorPlugin extends EditorPlugin
{

	@Override
	public void initialize(EditorScreen editor) {
		editor.addTool(new SpawnTool(editor));
		editor.selectors.add(new PlanetSelector(editor));
		editor.addTool(new RtsGameTool(editor));
		editor.addTool(new PlanetCreatorTool(editor));
		editor.addTool(new SolarCreatorTool(editor));
		editor.addTool(new MoveToPlanetTool(editor));
		editor.addTool(new CreateRoadTool(editor));
	}

}
