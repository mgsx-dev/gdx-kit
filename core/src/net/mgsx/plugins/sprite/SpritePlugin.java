package net.mgsx.plugins.sprite;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import net.mgsx.fwk.editor.Editor;
import net.mgsx.fwk.editor.plugins.Plugin;
import net.mgsx.fwk.editor.plugins.RenderablePlugin;
import net.mgsx.fwk.editor.plugins.StorablePlugin;

public class SpritePlugin extends Plugin implements RenderablePlugin, StorablePlugin<SpriteAspect>
{
	private Array<Sprite> sprites;
	
	private SpriteBatch batch;
	private ShapeRenderer debug;
	
	public SpritePlugin(Editor editor) {
		super(editor);
	}

	@Override
	public void initialize() 
	{
		batch = new SpriteBatch();
		sprites = new Array<Sprite>();
		
		editor.registerPlugin(SpriteAspect.class, this);
	}

	@Override
	public void render() {
		batch.setProjectionMatrix(editor.orthographicCamera.combined);
		batch.begin();
		for(Sprite sprite : sprites)
		{
			sprite.draw(batch);
		}
		batch.end();
	}

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


	
}
