package net.mgsx.game.examples.gpu;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.examples.gpu.systems.GPUFoliageSystem;
import net.mgsx.game.examples.gpu.systems.GPULandscapeSystem;
import net.mgsx.game.examples.gpu.systems.GPUParticlesSystem;
import net.mgsx.game.examples.gpu.systems.GPUQuickFoliageSystem;
import net.mgsx.game.examples.gpu.systems.GPURevolutionSystem;
import net.mgsx.game.examples.gpu.systems.GPUSpikeSystem;
import net.mgsx.game.examples.gpu.systems.GPUTesslatorSystem;
import net.mgsx.game.plugins.DefaultEditorPlugin;
import net.mgsx.game.plugins.ashley.AshleyEditorPlugin;
import net.mgsx.game.plugins.core.CoreEditorPlugin;
import net.mgsx.game.plugins.editor.KitEditorPlugin;

@PluginDef(dependencies={KitEditorPlugin.class, AshleyEditorPlugin.class, CoreEditorPlugin.class})
public class GpuExamplePlugin extends EditorPlugin implements DefaultEditorPlugin // XXX
{
	@Override
	public void initialize(EditorScreen editor) {
		editor.entityEngine.addSystem(new GPUParticlesSystem(editor));
		editor.entityEngine.addSystem(new GPUQuickFoliageSystem(editor));
		editor.entityEngine.addSystem(new GPURevolutionSystem(editor));
		editor.entityEngine.addSystem(new GPUSpikeSystem(editor));
		editor.entityEngine.addSystem(new GPUTesslatorSystem(editor));
		editor.entityEngine.addSystem(new GPUFoliageSystem(editor));
		editor.entityEngine.addSystem(new GPULandscapeSystem(editor));
		
		// XXX
		editor.entityEngine.getSystem(GPUParticlesSystem.class).setProcessing(false);;
		editor.entityEngine.getSystem(GPUQuickFoliageSystem.class).setProcessing(false);;
		editor.entityEngine.getSystem(GPURevolutionSystem.class).setProcessing(false);;
		editor.entityEngine.getSystem(GPULandscapeSystem.class).setProcessing(false);;
	}
}
