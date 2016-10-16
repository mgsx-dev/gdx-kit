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
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.glutils.FileTextureData;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class Storage 
{
	// TODO implement all required serializer here !
	
	// TODO handle by texture plugin (core plugin)
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
	
	// TODO it's a model serializer not model instance !
	static class ModelRef implements Json.Serializer<ModelInstance>{
		private AssetManager assets;
		
		public ModelRef(AssetManager assets) {
			super();
			this.assets = assets;
		}

		@Override
		public void write(Json json, ModelInstance object, Class knownType) {
			String ref = assets.getAssetFileName(object.model);
			if(ref != null){
				json.writeValue(ref);
			}
		}

		@Override
		public ModelInstance read(Json json, JsonValue jsonData, Class type) {
			String ref = jsonData.asString();
			if(ref != null){
				Model model = assets.get(ref, Model.class);
				ModelInstance i = new ModelInstance(model);
				// TODO more properties ....
				return i;
			}
			return null;
		}
		
	}
	static class SpriteSerializer implements Json.Serializer<Sprite>{

		@Override
		public void write(Json json, Sprite object, Class knownType) {
			json.writeObjectStart();
			json.writeFields(object);
			json.writeObjectEnd();
		}

		@Override
		public Sprite read(Json json, JsonValue jsonData, Class type) {
			Sprite sprite = new Sprite();
			json.readFields(sprite, jsonData);
			sprite.setRotation(sprite.getRotation()); // hack to clear dirty
			return sprite;
		}
		
	}
	
	private static Json setup(AssetManager assets)
	{
		Json json = new Json();
		json.setSerializer(EntityGroup.class, new EntityGroupSerializer(assets));
		json.setSerializer(Sprite.class, new SpriteSerializer());
		json.setSerializer(Texture.class, new TextureRef(assets));
		json.setSerializer(ModelInstance.class, new ModelRef(assets));
		
		// XXX temporarily box 2D
		json.setSerializer(Shape.class, new IgnoreSerializer<Shape>()); 
		json.setSerializer(ChainShape.class, new IgnoreSerializer<ChainShape>()); 
		json.setSerializer(CircleShape.class, new IgnoreSerializer<CircleShape>()); 
		json.setSerializer(EdgeShape.class, new IgnoreSerializer<EdgeShape>()); 
		json.setSerializer(PolygonShape.class, new IgnoreSerializer<PolygonShape>());
		
		
		return json;
	}
	
	public static void save(Engine engine, AssetManager assets, FileHandle file, boolean pretty) 
	{
		Writer writer = file.writer(false);
		save(engine, assets, writer, pretty);
		try {
			writer.close();
		} catch (IOException e) {
			throw new Error(e);
		};
	}
	public static void save(Engine engine, AssetManager assets, Writer writer, boolean pretty) 
	{
		EntityGroup group = new EntityGroup();
		group.entities = new Array<Entity>();
		for(Entity entity : engine.getEntities()) group.entities.add(entity);
		
		Json json = setup(assets);
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
