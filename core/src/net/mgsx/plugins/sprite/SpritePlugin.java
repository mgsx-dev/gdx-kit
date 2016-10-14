package net.mgsx.plugins.sprite;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import net.mgsx.core.Editor;
import net.mgsx.core.plugins.Plugin;

public class SpritePlugin extends Plugin
{
	private SpriteBatch batch;
	private ShapeRenderer debug;
	
	public void initialize(final Editor editor) 
	{
		batch = new SpriteBatch();
		editor.entityEngine.addSystem(new IteratingSystem(Family.one(SpriteModel.class).get()) { // TODO use sorted instead
			
			@Override
			public void update(float deltaTime) {
				batch.setProjectionMatrix(editor.orthographicCamera.combined);
				batch.begin();
				super.update(deltaTime);
				batch.end();
			}
			@Override
			protected void processEntity(Entity entity, float deltaTime) {
				Sprite sprite = entity.getComponent(SpriteModel.class).sprite;
				sprite.draw(batch);
			}
		});
		
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
