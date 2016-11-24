package net.mgsx.game.plugins.particle2d;

import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.utils.Pool;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.helpers.EditorAssetManager.AssetManagerListener;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.plugins.particle2d.tools.P2DBoundaryTool;
import net.mgsx.game.plugins.particle2d.tools.P2DImportTool;
import net.mgsx.game.plugins.particle2d.tools.Particle2DReloader;

@PluginDef(dependencies=Particle2DPlugin.class)
public class Particle2DEditorPlugin extends EditorPlugin
{
	@Override
	public void initialize(final EditorScreen editor) 
	{
		// import tool
		editor.addTool(new P2DImportTool(editor));
		editor.addTool(new P2DBoundaryTool(editor));
		
		// reloader
		editor.assets.addReloadListener(ParticleEffect.class, new Particle2DReloader(editor, editor.registry.getPlugin(Particle2DPlugin.class)));
	
		editor.assets.addListener(new AssetManagerListener() {
			@Override
			public void removed(String fileName) {
				changed(fileName);
			}
			@Override
			public void changed(String fileName) {
				Pool pool = editor.registry.getPlugin(Particle2DPlugin.class).pools.get(fileName);
				if(pool != null){
					pool.clear();
					editor.registry.getPlugin(Particle2DPlugin.class).pools.remove(fileName);
				}
			}
			@Override
			public void added(String fileName, Class type) {
			}
		});

	}
}
