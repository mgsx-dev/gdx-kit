package net.mgsx.game.plugins.camera.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.plugins.camera.components.CameraComponent;
import net.mgsx.game.plugins.camera.components.CullingComponent;
import net.mgsx.game.plugins.camera.components.RenderingComponent;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

public class CameraSystem extends IteratingSystem
{
	private ImmutableArray<Entity> renderCameras;
	private ImmutableArray<Entity> cullingCameras;

	
	public CameraSystem() {
		super(Family.all(CameraComponent.class).get(), GamePipeline.BEFORE_LOGIC);
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		
		renderCameras = engine.getEntitiesFor(Family.all(CameraComponent.class, RenderingComponent.class).get());
		cullingCameras = engine.getEntitiesFor(Family.all(CameraComponent.class, CullingComponent.class).get());
	}
	
	
	public Entity getRenderCamera(){
		return renderCameras.size() > 0 ? renderCameras.first() : null;
	}
	
	public Entity getCullingCamera(){
		return cullingCameras.size() > 0 ? cullingCameras.first() : null;
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
		
		if(camera.frustumDirty){
			camera.camera.update(true);
		}
		camera.camera.update(true); // XXX for editors
		camera.frustumDirty = false;
	}

}
