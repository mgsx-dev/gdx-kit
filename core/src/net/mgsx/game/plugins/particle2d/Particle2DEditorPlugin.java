package net.mgsx.game.plugins.particle2d;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.math.Vector2;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.helpers.NativeService;
import net.mgsx.game.core.helpers.NativeService.DefaultCallback;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.core.tools.ClickTool;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

public class Particle2DEditorPlugin extends EditorPlugin
{

	@Override
	public void initialize(EditorScreen editor) 
	{
		editor.assets.addReloadListener(Particle2DComponent.class, new Particle2DReloader(editor.entityEngine));
		
		editor.addTool(new ClickTool("Add particles", editor){
			String fileName;
			@Override
			protected void activate() {
				NativeService.instance.openLoadDialog(new DefaultCallback() {
					@Override
					public void selected(FileHandle file) {
						fileName = file.path();
						editor.loadAssetNow(fileName, ParticleEffect.class);
					}
					@Override
					public boolean match(FileHandle file) {
						return file.extension().equals("p");
					}
					@Override
					public String description() {
						return "Particle files (p)";
					}
				});
			}

			@Override
			protected void create(Vector2 position)
			{
				Particle2DComponent model = new Particle2DComponent();
				model.reference = fileName;
				
				Entity entity = editor.currentEntity();
				Transform2DComponent t = entity.getComponent(Transform2DComponent.class);
				if(t == null){
					t = new Transform2DComponent();
					t.position.set(position);
					entity.add(t);
				}
				
				entity.add(model);
			}
		});
	}
}
