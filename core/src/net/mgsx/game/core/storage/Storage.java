package net.mgsx.game.core.storage;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.glutils.FileTextureData;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializer;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entries;
import com.badlogic.gdx.utils.ObjectMap.Entry;

import net.mgsx.game.core.GameEngine;
import net.mgsx.game.core.components.OverrideProxy;
import net.mgsx.game.core.components.ProxyComponent;
import net.mgsx.game.core.exceptions.NotSupportedEntityGroupException;
import net.mgsx.game.core.plugins.Initializable;

/**
 * Storage service.
 * 
 * A component could implements Serializable in that way :
 * <pre>
 * 
 * 
	public void write(Json json) 
	{
		json.writeField(this, "life");
		json.writeField(this, "points");
	}

	@Override
	public void read(Json json, JsonValue jsonData) 
	{
		json.readField(this, "life", jsonData);
		json.readField(this, "points", jsonData);
		time = 0; // init not persisted data
	}
	</pre>
 * 
 * or you can register a serializer :
 * 
 * {@link GameEngine#addSerializer(Class, Serializer)} :
 * <pre>
 * GameEngine.addSerializer(MyType.class, new MySerializer());
 * </pre>
 * or any builtin serializers :
 * <pre>
 * GameEngine.addSerializer(MyType.class, new IgnoreSerializer<MyType>());
 * GameEngine.addSerializer(MyType.class, new AnnotationSerializer<MyType>()); TODO ?
 * </pre>
 * @author mgsx
 *
 */
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
	// TODO move this to G3D plugin
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
			// TODO dont export dirty
		}

		@Override
		public Sprite read(Json json, JsonValue jsonData, Class type) {
			Sprite sprite = new Sprite();
			json.readFields(sprite, jsonData);
			sprite.setRotation(sprite.getRotation()); // hack to clear dirty
			// TODO set dirty properly ?
			return sprite;
		}
		
	}
	
	private static Array<AssetSerializer> assetSerializers = new Array<AssetSerializer>();
	private static Array<ContextualSerializer> contextualSerializers = new Array<ContextualSerializer>();
	
	public static void register(AssetSerializer serializer){
		assetSerializers.add(serializer);
	}
	
	public static void register(ContextualSerializer serializer){
		contextualSerializers.add(serializer);
	}
	
	private static Json setup(AssetManager assets, ObjectMap<Class, Serializer> serializers)
	{
		EntityGroupSerializer entityGroupSerializer = new EntityGroupSerializer(assets);
		
		Json json = new Json();
		json.setSerializer(EntityGroup.class, entityGroupSerializer);
		
		for(Entry<String, Class> entry : classTags){
			json.addClassTag(entry.key, entry.value);
		}
		
		// TODO configure in plugins using asset serializer
		json.setSerializer(Sprite.class, new SpriteSerializer());
		json.setSerializer(Texture.class, new TextureRef(assets));
		json.setSerializer(ModelInstance.class, new ModelRef(assets));
		
		for(ContextualSerializer serializer : contextualSerializers){
			serializer.context = entityGroupSerializer;
			json.setSerializer(serializer.getType(), serializer);
		}
		
		for(AssetSerializer serializer : assetSerializers){
			serializer.assets = assets;
			json.setSerializer(serializer.getType(), serializer);
		}
		
		for(Entries<Class, Serializer> entries = serializers.iterator() ; entries.hasNext() ; ){
			Entry<Class, Serializer> entry = entries.next();
			json.setSerializer(entry.key, entry.value);
		}
		
		return json;
	}
	
	public static void save(Engine engine, AssetManager assets, FileHandle file, boolean pretty, ObjectMap<Class, Serializer> serializers) 
	{
		Writer writer = file.writer(false);
		save(engine, assets, writer, pretty, serializers);
		try {
			writer.close();
		} catch (IOException e) {
			throw new Error(e);
		};
	}
	public static void save(Engine engine, AssetManager assets, Writer writer, boolean pretty, ObjectMap<Class, Serializer> serializers) 
	{
		EntityGroup group = new EntityGroup();
		group.entities = new Array<Entity>();
		for(Entity entity : engine.getEntities()) group.entities.add(entity);
		
		Json json = setup(assets, serializers);
		if(pretty){
			try {
				writer.append(json.prettyPrint(group));
			} catch (IOException e) {
				throw new Error(e); // TODO ?
			}
		}else
			json.toJson(group, writer);
	}

	public static Array<Entity> load(Engine engine, FileHandle file, AssetManager assets, ObjectMap<Class, Serializer> serializers) 
	{
		return load(engine, file, assets, serializers, false);
	}
	public static Array<Entity> load(Engine engine, FileHandle file, AssetManager assets, ObjectMap<Class, Serializer> serializers, boolean allowOnlySingle) 
	{
		EntityGroup group = load(file.reader(), assets, serializers);
		if(allowOnlySingle && group.entities.size > 1){
			throw new NotSupportedEntityGroupException(file.path(), group);
		}
		for(Entity entity : new Array<Entity>(group.entities)){
			// post initialization
			for(Component component : entity.getComponents()){
				if(component instanceof Initializable){
					((Initializable) component).initialize(engine, entity);
				}
			}
			engine.addEntity(entity);
		}

		return group.entities;
	}
	
	private static EntityGroup load(Reader reader, AssetManager assets, ObjectMap<Class, Serializer> serializers)
	{
		Json json = setup(assets, serializers);
		
		EntityGroup group = json.fromJson(EntityGroup.class, reader);
		// first resolve proxies
		for(Entity entity : new Array<Entity>(group.entities)) // copy array to insert into it
		{
			ProxyComponent proxy = entity.getComponent(ProxyComponent.class);
			if(proxy != null){
				EntityGroup proxyGroup = load(Gdx.files.absolute(proxy.ref).reader(), assets, serializers);
				if(proxyGroup.entities.size != 1){
					throw new NotSupportedEntityGroupException(proxy.ref, group);
				}
				
				for(Entity sub : proxyGroup.entities){
					sub.add(proxy.duplicate());
					
					for(Component c : entity.getComponents()){
						if(c instanceof OverrideProxy){
							sub.add(c);
						}
					}
					
					group.entities.removeValue(entity, true);
					group.entities.add(sub);
				}
			}
		}

		return group;
	}

	public static final ObjectMap<String, Class<? extends Component>> typeMap = new ObjectMap<String, Class<? extends Component>>();
	public static final ObjectMap<Class<? extends Component>, String> nameMap = new ObjectMap<Class<? extends Component>, String>();
	
	/**
	 * Register a type to be persisted (saved and loaded within game file)
	 * @param storable type to store
	 * @param name type name in file (should be unique)
	 * throw runtime error if name conflicts within registry.
	 */
	public static void register(Class<? extends Component> storable, String name)
	{
		if(typeMap.containsKey(name)){
			if(typeMap.get(name) == storable){
				// use syout instead of GDX log because GdxLogger not enabled yet.
				System.out.println("skip type name " + name + " already registered for the same type.");
			}else{				
				throw new Error("type name " + name + " already registered for class " + typeMap.get(name).getName());
			}
		}
		typeMap.put(name, storable);
		nameMap.put(storable, name);
	}

	private static ObjectMap<String, Class> classTags = new ObjectMap<String, Class>();
	
	public static void addClassTag(String tag, Class type) {
		classTags.put(tag, type);
	}

	public static <T> T load(FileHandle file, Class<T> type) {
		Json json = setup(null, new ObjectMap<Class, Json.Serializer>()); // XXX ?
		return json.fromJson(type, file);
	}

}
