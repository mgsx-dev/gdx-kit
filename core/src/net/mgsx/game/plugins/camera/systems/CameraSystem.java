package net.mgsx.game.plugins.camera.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.GameScreen;
import net.mgsx.game.plugins.camera.components.ActiveCamera;
import net.mgsx.game.plugins.camera.components.CameraComponent;
import net.mgsx.game.plugins.camera.components.ViewportComponent;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

public class CameraSystem extends IteratingSystem
{
	private GameScreen game;
	private ImmutableArray<Entity> activeCameras;
	
	public CameraSystem(GameScreen game) {
		super(Family.all(CameraComponent.class).get(), GamePipeline.AFTER_LOGIC);
		this.game = game;
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		
		activeCameras = engine.getEntitiesFor(Family.all(CameraComponent.class, ActiveCamera.class).get());
	}
	
	@Override
	public void update(float deltaTime) 
	{
		// first update all active cameras
		super.update(deltaTime);
		
		// then copy first one to current camera matrices
		if(activeCameras.size() > 0)
		{
			Entity activeCamera = activeCameras.first();
			
			CameraComponent camera = CameraComponent.components.get(activeCamera);
			
			ViewportComponent viewport = ViewportComponent.components.get(activeCamera);
			if(viewport != null)
			{
				viewport.viewport.setCamera(camera.camera);
				viewport.viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
			}
			
			// copy camera settings to game
			game.camera.position.set(camera.camera.position);
			game.camera.direction.set(camera.camera.direction);
			game.camera.combined.set(camera.camera.combined);
			game.camera.invProjectionView.set(camera.camera.invProjectionView);
			game.camera.projection.set(camera.camera.projection);
			game.camera.up.set(camera.camera.up);
			game.camera.view.set(camera.camera.view);
			game.camera.viewportHeight = camera.camera.viewportHeight;
			game.camera.far = camera.camera.far;
			game.camera.near = camera.camera.near;
			game.camera.viewportWidth = camera.camera.viewportWidth;
			game.camera.frustum.update(camera.camera.invProjectionView);
		}
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) 
	{
		CameraComponent camera = CameraComponent.components.get(entity);
		
		Transform2DComponent transform = Transform2DComponent.components.get(entity);
		if(transform != null){
			camera.camera.position.x = transform.position.x;
			camera.camera.position.y = transform.position.y;
		}
		// always update cameras (for hidden ones ?)
		camera.camera.update(true);
	}

}
