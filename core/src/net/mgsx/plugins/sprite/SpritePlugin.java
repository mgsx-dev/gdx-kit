package net.mgsx.plugins.sprite;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.mgsx.core.Editor;
import net.mgsx.core.helpers.EntityHelper.SingleComponentIteratingSystem;
import net.mgsx.core.plugins.Movable;
import net.mgsx.core.plugins.Plugin;

public class SpritePlugin extends Plugin
{
	// TODO use batch processor in editor ...
	private SpriteBatch batch;
	
	public void initialize(final Editor editor) 
	{
		batch = new SpriteBatch();
		
		// plugin for editor only (movable)
		editor.entityEngine.addEntityListener(Family.one(SpriteModel.class).get(), new EntityListener() {
			@Override
			public void entityRemoved(Entity entity) {
				
			}
			@Override
			public void entityAdded(Entity entity) {
				if(entity.getComponent(Movable.class) == null){
					Sprite sprite = entity.getComponent(SpriteModel.class).sprite;
					entity.add(new Movable(new SpriteMove(sprite)));
				}
			}
		});
		
		editor.entityEngine.addSystem(new SingleComponentIteratingSystem<SpriteModel>(SpriteModel.class) { // TODO use sorted instead
			
			@Override
			public void update(float deltaTime) {
				batch.setProjectionMatrix(editor.orthographicCamera.combined);
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
		editor.addGlobalTool(new SelectSpriteTool(editor));
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
