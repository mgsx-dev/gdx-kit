package net.mgsx.game.plugins.sprite;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.mgsx.game.core.Editor;
import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.components.Movable;
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
		
		// plugin for editor only (movable)
		editor.entityEngine.addEntityListener(Family.one(SpriteModel.class).get(), new EntityListener() {
			@Override
			public void entityRemoved(Entity entity) {
				entity.remove(SpriteModel.class);
				entity.remove(Movable.class);
			}
			@Override
			public void entityAdded(Entity entity) {
				if(entity.getComponent(Movable.class) == null){
					Sprite sprite = entity.getComponent(SpriteModel.class).sprite;
					entity.add(new Movable(new SpriteMove(sprite)));
				}
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
/*
	@Override
	public void drawDebug() {
		// TODO Auto-generated method stub
		debug.begin(ShapeType.Line);
		// TODO debug.rect(x, y, w, h);
		debug.end();
	}

	// example of custom serialization
	@Override
	public void write(Json json, SpriteAspect object, Class knownType) {
		json.writeObjectStart();
		json.writeField(object.sprite, "x");
		json.writeObjectEnd();
		
		// Note : example of writing DTO
		// Dto dto = new Dto();
		// dto.x = object.sprite.x;
		// json.writeValue(dto);
	}

	@Override
	public SpriteAspect read(Json json, JsonValue jsonData, Class type) {
		SpriteAspect object = new SpriteAspect();
		object.sprite = new Sprite();
		json.readField(object.sprite, "x", jsonData);
		return object;

		// Note : example of reading DTO
		// Dto dto = new Dto();
		// dto.x = object.sprite.x;
		// Dto dto = json.readValue(Dto.class, jsonData);
		// SpriteAspect object = new SpriteAspect();
		// object.sprite = new Sprite();
		// object.sprite.setX(dto.x)
	}
*/

	
}
