package net.mgsx.game.plugins.sprite;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.mgsx.game.core.Editor;
import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.components.Transform2DComponent;
import net.mgsx.game.core.helpers.systems.ComponentIteratingSystem;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.core.storage.Storage;

public class SpritePlugin extends EditorPlugin
{
	// TODO use batch processor in editor ...
	private SpriteBatch batch;
	
	public void initialize(final Editor editor) 
	{
		batch = new SpriteBatch();
		
		Storage.register(SpriteModel.class, "sprite");
		
		editor.entityEngine.addSystem(new IteratingSystem(Family.all(SpriteModel.class, Transform2DComponent.class).get(), GamePipeline.BEFORE_RENDER) { // TODO use sorted instead
			
			@Override
			protected void processEntity(Entity entity, float deltaTime) {
				SpriteModel sprite = SpriteModel.components.get(entity);
				Transform2DComponent transform = Transform2DComponent.components.get(entity);
				// TODO enabled
				sprite.sprite.setPosition(transform.position.x, transform.position.y);
				sprite.sprite.setRotation(transform.angle);
				sprite.sprite.setOrigin(transform.origin.x, transform.origin.y);
			}
		});
		
		editor.entityEngine.addSystem(new ComponentIteratingSystem<SpriteModel>(SpriteModel.class, GamePipeline.RENDER) { // TODO use sorted instead
			
			@Override
			public void update(float deltaTime) {
				batch.setProjectionMatrix(editor.camera.combined);
				batch.begin();
				super.update(deltaTime);
				batch.end();
			}
			@Override
			protected void processEntity(Entity entity, SpriteModel model, float deltaTime) {
				model.sprite.draw(batch);
			}
		});
		
		editor.addTool(new AddSpriteTool(editor));
		editor.addSelector(new SpriteSelector(editor));
	}
	
}
