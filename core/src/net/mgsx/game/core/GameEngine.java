package net.mgsx.game.core;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json.Serializer;
import com.badlogic.gdx.utils.ObjectMap;

import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.core.plugins.Plugin;

// TODO
// shoulb be a screen (because of multi screen features or multi viewport ...
// it only contains high performence / no editor stuff runtime!
public class GameEngine extends ApplicationAdapter
{
	public AssetManager assets;
	public ShapeRenderer shapeRenderer;
	protected SpriteBatch batch;
	protected Array<EditorPlugin> editorPlugins = new Array<EditorPlugin>();
	protected Array<Plugin> plugins = new Array<Plugin>();
	public PooledEngine entityEngine;
	public OrthographicCamera orthographicCamera;
	public PerspectiveCamera perspectiveCamera;
	
	final protected ObjectMap<Class, Serializer> serializers = new ObjectMap<Class, Serializer>();
	
	public void registerPlugin(Plugin plugin) {
		plugins.add(plugin);
	}

	public <T> void addSerializer(Class<T> type, Serializer<T> serializer) {
		serializers.put(type, serializer);
	}
	
	@Override
	public void create() 
	{
		super.create();
		assets = new AssetManager(); // TODO resolver maybe different for game and editor ?
		Texture.setAssetManager(assets);
		entityEngine = new PooledEngine();
		orthographicCamera = new OrthographicCamera();
		shapeRenderer = new ShapeRenderer();
		batch = new SpriteBatch();
		
		perspectiveCamera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		perspectiveCamera.position.set(0, 0, 10);
		perspectiveCamera.up.set(0,1,0);
		perspectiveCamera.lookAt(0,0,0);
		perspectiveCamera.near = 1f;
		perspectiveCamera.far = 3000f;
		perspectiveCamera.update();
		
		for(Plugin plugin : plugins){
			plugin.initialize(this);
		}

	}
	
	@Override
	public void render() {
		
		perspectiveCamera.position.set(orthographicCamera.position.x, orthographicCamera.position.y, orthographicCamera.position.z); // XXX 3.8f); // TODO ortho factor ?
//		perspectiveCamera.lookAt(0,0,0);
		perspectiveCamera.update();
		super.render();
	}
	
	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		perspectiveCamera.viewportWidth = Gdx.graphics.getWidth();
		perspectiveCamera.viewportHeight = Gdx.graphics.getHeight();
		
		perspectiveCamera.update();
	}
	

}
