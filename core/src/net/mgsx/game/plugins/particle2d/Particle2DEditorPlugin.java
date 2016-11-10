package net.mgsx.game.plugins.particle2d;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.math.Vector2;

import net.mgsx.game.core.Editor;
import net.mgsx.game.core.NativeService;
import net.mgsx.game.core.NativeService.DialogCallback;
import net.mgsx.game.core.components.Transform2DComponent;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.core.tools.ClickTool;

public class Particle2DEditorPlugin extends EditorPlugin
{

	@Override
	public void initialize(Editor editor) 
	{
		editor.addTool(new ClickTool("Add particles", editor){
			String fileName;
			@Override
			protected void activate() {
				NativeService.instance.openLoadDialog(new DialogCallback() {
					@Override
					public void selected(FileHandle file) {
						fileName = file.path();
						editor.loadAssetNow(fileName, ParticleEffect.class);
					}
					@Override
					public void cancel() {
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
