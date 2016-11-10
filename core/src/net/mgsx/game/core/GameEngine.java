package net.mgsx.game.core;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializer;
import com.badlogic.gdx.utils.ObjectMap;

import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.annotations.Storable;
import net.mgsx.game.core.components.ProxyComponent;
import net.mgsx.game.core.components.Transform2DComponent;
import net.mgsx.game.core.helpers.ReflectionHelper;
import net.mgsx.game.core.helpers.TypeMap;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.core.plugins.Plugin;
import net.mgsx.game.core.storage.Storage;

// TODO
// shoulb be a screen (because of multi screen features or multi viewport ...
// it only contains high performence / no editor stuff runtime!
public class GameEngine extends ApplicationAdapter
{
	public AssetManager assets;
	public ShapeRenderer shapeRenderer;
	public SpriteBatch batch;
	protected TypeMap<EditorPlugin> editorPlugins = new TypeMap<EditorPlugin>();
	protected TypeMap<Plugin> plugins = new TypeMap<Plugin>();
	public PooledEngine entityEngine;
	
	// TODO not really cool ...
	public void remove(Entity entity, Class<? extends Component> type){
		Component component = entity.remove(type);
		if(component instanceof Disposable){
			((Disposable) component).dispose();
		}
	}
	
	public Camera camera, gameCamera; // TODO game camera and editor camera !
	
	public final ObjectMap<Class, Serializer> serializers = new ObjectMap<Class, Serializer>();
	
	public void registerPlugin(Plugin plugin) {
		PluginDef def = plugin.getClass().getAnnotation(PluginDef.class);
		if(def != null){
			for(Class<? extends Plugin> dependency : def.dependencies()){
				registerPlugin(ReflectionHelper.newInstance(dependency));
			}
		}
		plugins.put(plugin.getClass(), plugin);
	}

	public <T> void addSerializer(Class<T> type, Json.Serializer<T> serializer) {
		serializers.put(type, serializer);
	}
	
	public void register(Class<? extends Component> type){
		
		Storable storable = type.getAnnotation(Storable.class);
		if(storable != null){
			Storage.register(type, storable.tag());
		}
		
	}
	
	@Override
	public void create() 
	{
		super.create();
		
		// register some core components
		Storage.register(Transform2DComponent.class, "2d");
		Storage.register(ProxyComponent.class, "proxy");
		
		assets = new AssetManager(); // TODO resolver maybe different for game and editor ?
		Texture.setAssetManager(assets);
		entityEngine = new PooledEngine();
		shapeRenderer = new ShapeRenderer();
		batch = new SpriteBatch();
		
		camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.position.set(0, 0, 10);
		camera.up.set(0,1,0);
		camera.lookAt(0,0,0);
		camera.near = 1f;
		camera.far = 3000f;
		camera.update();
		
		gameCamera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		gameCamera.position.set(0, 0, 10);
		gameCamera.up.set(0,1,0);
		gameCamera.lookAt(0,0,0);
		gameCamera.near = 1f;
		gameCamera.far = 3000f;
		gameCamera.update();
		
		for(Plugin plugin : plugins.values()){
			plugin.initialize(this);
		}

	}
	
	@Override
	public void render() {
		
		camera.update(true);
		super.render();
	}
	
	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		camera.viewportWidth = Gdx.graphics.getWidth();
		camera.viewportHeight = Gdx.graphics.getHeight();
		
		camera.update(true);
	}

	public <T extends Plugin> T getPlugin(Class<T> type) {
		return (T)plugins.get(type);
	}
	

}
