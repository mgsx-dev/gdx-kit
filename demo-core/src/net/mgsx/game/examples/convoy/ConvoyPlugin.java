package net.mgsx.game.examples.convoy;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.examples.convoy.model.Universe;
import net.mgsx.game.examples.convoy.systems.ConveyorLogicSystem;
import net.mgsx.game.examples.convoy.systems.ConveyorRenderSystem;
import net.mgsx.game.examples.convoy.systems.ConvoyHUDSystem;
import net.mgsx.game.examples.convoy.systems.PlanetLogicRandomSystem;
import net.mgsx.game.examples.convoy.systems.PlanetRenderSystem;
import net.mgsx.game.examples.convoy.systems.TransitSystem;
import net.mgsx.game.plugins.ashley.AshleyEditorPlugin;
import net.mgsx.game.plugins.assets.AssetsEditorPlugin;
import net.mgsx.game.plugins.camera.CameraEditorPlugin;
import net.mgsx.game.plugins.core.CoreEditorPlugin;
import net.mgsx.game.plugins.editor.KitEditorPlugin;
import net.mgsx.game.plugins.graphics.GraphicsEditorPlugin;

@PluginDef(dependencies={
		KitEditorPlugin.class,
		AshleyEditorPlugin.class,
		CoreEditorPlugin.class,
		GraphicsEditorPlugin.class,
		CameraEditorPlugin.class,
		AssetsEditorPlugin.class,
	})
public class ConvoyPlugin extends EditorPlugin
{

	@Override
	public void initialize(EditorScreen editor) {
		
		editor.registry.registerModel(Universe.init(editor.entityEngine));
		
//		editor.entityEngine.addSystem(new PlanetLogicDeterministicSystem());
		editor.entityEngine.addSystem(new PlanetLogicRandomSystem());
		editor.entityEngine.addSystem(new ConveyorLogicSystem());
		
		editor.entityEngine.addSystem(new PlanetRenderSystem());
		editor.entityEngine.addSystem(new TransitSystem());
		editor.entityEngine.addSystem(new ConveyorRenderSystem());
		editor.entityEngine.addSystem(new ConvoyHUDSystem(editor));
		
		// editor.addTool(new GameTool());
	}

}
