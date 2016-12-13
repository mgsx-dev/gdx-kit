package net.mgsx.game.core;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.storage.EntityGroupStorage;
import net.mgsx.game.core.storage.LoadConfiguration;
import net.mgsx.game.plugins.camera.components.CameraComponent;
import net.mgsx.game.plugins.camera.components.CullingComponent;
import net.mgsx.game.plugins.camera.components.RenderingComponent;
import net.mgsx.game.plugins.camera.systems.CameraSystem;

/**
 * Base screen for managed game scene.
 * Has its own Entity Engine.
 * 
 * @author mgsx
 *
 */
public class GameScreen extends ScreenAdapter
{
	final public AssetManager assets;
	
	final private Array<String> pendingGroups = new Array<String>();
	
	final public Engine entityEngine;
	public GameRegistry registry;
	private Entity camera;
	
	private CameraSystem cameraSystem;
	
	public GameScreen(AssetManager assets, Engine engine) {
		super();
		this.assets = assets;
		this.entityEngine = engine;
		init();
	}
	
	
	protected <T extends Component> T addComponent(Entity entity, Class<T> type){
		T component = entityEngine.createComponent(type);
		entity.add(component);
		return component;
	}
	
	private void init()
	{
		createCamera();
	}
	
	@Override
	public void show() {
		super.show();
		cameraSystem = entityEngine.getSystem(CameraSystem.class);
	}
	
	public Camera getRenderCamera()
	{
		Entity cameraEntity = cameraSystem.getRenderCamera();
		CameraComponent camera = CameraComponent.components.get(cameraEntity);
		return camera.camera;
	}
	
	void createCamera() 
	{
		Camera gameCamera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		gameCamera.position.set(0, 0, 10);
		gameCamera.up.set(0,1,0);
		gameCamera.lookAt(0,0,0);
		gameCamera.near = 1f;
		gameCamera.far = 3000f;
		gameCamera.update(true);
		
		camera = entityEngine.createEntity();
		addComponent(camera, CameraComponent.class).camera = gameCamera;
		addComponent(camera, RenderingComponent.class);
		addComponent(camera, CullingComponent.class);
		entityEngine.addEntity(camera);
	}


	public Camera getCullingCamera()
	{
		Entity cameraEntity = cameraSystem.getCullingCamera();
		CameraComponent camera = CameraComponent.components.get(cameraEntity);
		return camera.camera;
	}
	
	
	// TODO remove ?
	@Override
	public void render(float delta) 
	{
		// load pending entity groups in engine.
		while(pendingGroups.size > 0){
			EntityGroupStorage.get(assets, entityEngine, pendingGroups.removeIndex(0));
		}
		
		// TODO in systems
		Gdx.gl.glClearColor(.2f, .2f, .2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		// run the engine
		entityEngine.update(Gdx.graphics.getDeltaTime());
	}
	
	@Override
	public void resize(int width, int height) 
	{
		super.resize(width, height);
		
		CameraComponent c = CameraComponent.components.get(camera);
		c.camera.viewportWidth = Gdx.graphics.getWidth();
		c.camera.viewportHeight = Gdx.graphics.getHeight();
		c.camera.update(true);
	}

	public void load(FileHandle file) 
	{
		if(file != null){
			String name = file.path();
			pendingGroups.add(name);
			
			LoadConfiguration config = new LoadConfiguration();
			config.assets = assets;
			config.registry = registry;
			config.engine = entityEngine;
			
			// TODO load settings as well !
			
			EntityGroupStorage.load(name, config);
		}
	}

}
