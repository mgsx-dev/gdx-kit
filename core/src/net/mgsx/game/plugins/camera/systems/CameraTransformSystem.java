package net.mgsx.game.plugins.camera.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.plugins.camera.components.CameraComponent;
import net.mgsx.game.plugins.core.components.Transform3DComponent;

public class CameraTransformSystem extends IteratingSystem
{
	public CameraTransformSystem() {
		super(Family.all(CameraComponent.class, Transform3DComponent.class).get(), GamePipeline.BEFORE_LOGIC);
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) 
	{
		CameraComponent camera = CameraComponent.components.get(entity);
		Transform3DComponent transform = Transform3DComponent.components.get(entity);
		camera.camera.position.set(transform.position);
		// TODO quaternion ?
	}

}
