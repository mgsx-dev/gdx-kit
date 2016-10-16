package net.mgsx.core.storage;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.FileTextureData;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class Storage {

	// TODO implement all required serializer here !
	
	static class TextureRef implements Json.Serializer<Texture>{
		private AssetManager assets;
		
		public TextureRef(AssetManager assets) {
			super();
			this.assets = assets;
		}

		@Override
		public void write(Json json, Texture object, Class knownType) {
			// TODO get texture ref .... json.writeValue(null);
			if(object.getTextureData() instanceof FileTextureData){
				String ref = ((FileTextureData)object.getTextureData()).getFileHandle().path();
				json.writeValue(ref);
			}
		}

		@Override
		public Texture read(Json json, JsonValue jsonData, Class type) {
			String ref = jsonData.asString();
			if(ref != null){
				return assets.get(ref, Texture.class);
			}
			return null;
		}
		
	}
	static class SpriteSerializer implements Json.Serializer<Sprite>{

		@Override
		public void write(Json json, Sprite object, Class knownType) {
			json.writeObjectStart();
			json.writeValue("x", object.getX());
			json.writeValue("y", object.getY());
			json.writeField(object, "texture");
			json.writeObjectEnd();
		}

		@Override
		public Sprite read(Json json, JsonValue jsonData, Class type) {
			Sprite sprite = new Sprite();
			sprite.setX(jsonData.getFloat("x"));
			sprite.setY(jsonData.getFloat("y"));
			json.readField(sprite, "texture", jsonData);
			return sprite;
		}
		
	}
	
	private static Json setup(AssetManager assets)
	{
		Json json = new Json();
		json.setSerializer(EntityGroup.class, new EntityGroupSerializer());
		json.setSerializer(Sprite.class, new SpriteSerializer());
		json.setSerializer(Texture.class, new TextureRef(assets));
		return json;
	}
	
	public static void save(Engine engine, FileHandle file, boolean pretty) 
	{
		Writer writer = file.writer(false);
		save(engine, writer, pretty);
		try {
			writer.close();
		} catch (IOException e) {
			throw new Error(e);
		};
	}
	public static void save(Engine engine, Writer writer, boolean pretty) 
	{
		EntityGroup group = new EntityGroup();
		group.entities = new Array<Entity>();
		for(Entity entity : engine.getEntities()) group.entities.add(entity);
		
		Json json = setup(null);
		if(pretty){
			try {
				writer.append(json.prettyPrint(group));
			} catch (IOException e) {
				throw new Error(e); // TODO ?
			}
		}else
			json.toJson(group, writer);
	}

	public static void load(Engine engine, FileHandle file, AssetManager assets) 
	{
		load(engine, file.reader(), assets);
	}
	public static void load(Engine engine, Reader reader, AssetManager assets) 
	{
		Json json = setup(assets);
		EntityGroup group = json.fromJson(EntityGroup.class, reader);
		for(Entity entity : group.entities) engine.addEntity(entity);
	}

}
