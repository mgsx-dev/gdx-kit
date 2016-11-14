package net.mgsx.game.plugins.g2d.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.Storable;
import net.mgsx.game.core.components.Duplicable;

@Storable("sprite")
public class SpriteModel implements Component, Duplicable, Serializable
{
	
	public static ComponentMapper<SpriteModel> components = ComponentMapper.getFor(SpriteModel.class);
	
	@Editable public Sprite sprite;

	@Override
	public Component duplicate() {
		SpriteModel model = new SpriteModel();
		model.sprite = new Sprite(sprite);
		return model;
	}

	@Override
	public void write(Json json) 
	{
		json.writeObjectStart("sprite");
		json.writeValue("u", sprite.getU());
		json.writeValue("v", sprite.getV());
		json.writeValue("u2", sprite.getU2());
		json.writeValue("v2", sprite.getV2());
		json.writeValue("x", sprite.getX());
		json.writeValue("y", sprite.getY());
		json.writeValue("width", sprite.getWidth());
		json.writeValue("height", sprite.getHeight());
		json.writeValue("sx", sprite.getScaleX());
		json.writeValue("sy", sprite.getScaleY());
		json.writeValue("rotation", sprite.getRotation());
		json.writeValue("texture", sprite.getTexture());
		json.writeValue("color", sprite.getColor());
		json.writeObjectEnd();
	}

	@Override
	public void read(Json json, JsonValue jsonData) 
	{
		JsonValue spriteData = jsonData.get("sprite");
		float u = json.readValue("u", float.class, 0f, spriteData);
		float v = json.readValue("v", float.class, 0f, spriteData);
		float u2 = json.readValue("u2", float.class, 1f, spriteData);
		float v2 = json.readValue("v2", float.class, 1f, spriteData);
		
		Texture texture = json.readValue("texture", Texture.class, spriteData);
		TextureRegion region = new TextureRegion(texture, u, v, u2, v2);
		
		float x = json.readValue("x", float.class, spriteData);
		float y = json.readValue("y", float.class, spriteData);
		float width = json.readValue("width", float.class, spriteData);
		float height = json.readValue("height", float.class, spriteData);
		float angle = json.readValue("angle", float.class, 0f, spriteData);
		float sx = json.readValue("sx", float.class, 1f, spriteData);
		float sy = json.readValue("sy", float.class, 1f, spriteData);

		sprite = new Sprite(region);
		sprite.setBounds(x, y, width, height);
		sprite.setRotation(angle);
		sprite.setScale(sx, sy);
		sprite.setColor(json.readValue("color", Color.class, Color.WHITE, spriteData));
	}

	
	
}
