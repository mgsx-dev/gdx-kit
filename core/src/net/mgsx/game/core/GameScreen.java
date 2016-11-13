package net.mgsx.game.core;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Json.Serializer;

import net.mgsx.game.core.storage.Storage;

/**
 * Base screen for managed game scene.
 * Has its own Entity Engine.
 * 
 * @author mgsx
 *
 */
public class GameScreen extends ScreenAdapter
{
	public AssetManager assets;
	
	final public Engine entityEngine;
	public GameRegistry registry;
	
	public GameScreen() {
		super();
		entityEngine = new PooledEngine();
		
		init();
	}
	
	public Camera camera, gameCamera; // TODO game camera and editor camera !
	
	
	private void init()
	{
		assets = new AssetManager(); // TODO resolver maybe different for game and editor ?
		Texture.setAssetManager(assets);
		
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
	}
	
	
	// TODO remove ?
	@Override
	public void render(float delta) {
		
		camera.update(true);
		
		// TODO in systems
		Gdx.gl.glClearColor(.7f, .9f, 1f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		// run the engine
		entityEngine.update(Gdx.graphics.getDeltaTime());
	}
	
	@Override
	public void resize(int width, int height) 
	{
		super.resize(width, height);
		camera.viewportWidth = Gdx.graphics.getWidth();
		camera.viewportHeight = Gdx.graphics.getHeight();
		camera.update(true);
	}


	public void register(Class<? extends Component> type) {
		registry.register(type);
	}


	public <T> void addSerializer(Class<T> type, Serializer<T> serializer) {
		registry.addSerializer(type, serializer);
	}


	public void load(FileHandle file) 
	{
		if(file != null) Storage.load(entityEngine, file, assets, registry.serializers);
	}

}
