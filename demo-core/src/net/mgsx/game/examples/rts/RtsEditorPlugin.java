package net.mgsx.game.examples.rts;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.examples.rts.tools.CreateAIColonyTool;
import net.mgsx.game.examples.rts.tools.CreateColonyTool;
import net.mgsx.game.examples.rts.tools.CreateRoadTool;
import net.mgsx.game.examples.rts.tools.GalaxyCreatorTool;
import net.mgsx.game.examples.rts.tools.MoveToPlanetTool;
import net.mgsx.game.examples.rts.tools.PlanetCreatorTool;
import net.mgsx.game.examples.rts.tools.PlanetSelector;
import net.mgsx.game.examples.rts.tools.RtsGameTool;
import net.mgsx.game.examples.rts.tools.SolarCreatorTool;
import net.mgsx.game.examples.rts.tools.SpawnTool;
import net.mgsx.game.plugins.editor.KitEditorPlugin;

@PluginDef(dependencies={RtsPlugin.class, KitEditorPlugin.class})
public class RtsEditorPlugin extends EditorPlugin
{
	@Override
	public void initialize(EditorScreen editor) {
		editor.addTool(new SpawnTool(editor));
		editor.addSelector(new PlanetSelector(editor));
		editor.addTool(new RtsGameTool(editor));
		editor.addTool(new PlanetCreatorTool(editor));
		editor.addTool(new SolarCreatorTool(editor));
		editor.addTool(new MoveToPlanetTool(editor));
		editor.addTool(new CreateRoadTool(editor));
		editor.addTool(new GalaxyCreatorTool(editor));
		editor.addTool(new CreateColonyTool(editor));
		editor.addTool(new CreateAIColonyTool(editor));
	}

}
