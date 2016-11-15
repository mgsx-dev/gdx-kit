package net.mgsx.game.plugins.particle2d;

import com.badlogic.gdx.graphics.g2d.ParticleEffect;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.plugins.particle2d.tools.P2DBoundaryTool;
import net.mgsx.game.plugins.particle2d.tools.P2DImportTool;
import net.mgsx.game.plugins.particle2d.tools.Particle2DReloader;

@PluginDef(dependencies=Particle2DPlugin.class)
public class Particle2DEditorPlugin extends EditorPlugin
{
	@Override
	public void initialize(EditorScreen editor) 
	{
		// import tool
		editor.addTool(new P2DImportTool(editor));
		editor.addTool(new P2DBoundaryTool(editor));
		
		// reloader
		editor.assets.addReloadListener(ParticleEffect.class, new Particle2DReloader(editor, editor.registry.getPlugin(Particle2DPlugin.class)));
	}
}
