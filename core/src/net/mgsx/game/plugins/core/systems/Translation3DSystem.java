package net.mgsx.game.plugins.core.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.plugins.core.components.Transform3DComponent;
import net.mgsx.game.plugins.core.components.Translation3D;

public class Translation3DSystem extends IteratingSystem
{
	public Translation3DSystem() {
		super(Family.all(Translation3D.class, Transform3DComponent.class).get(), GamePipeline.AFTER_LOGIC);
	}
	@Override
	protected void processEntity(Entity entity, float deltaTime) 
	{
		Transform3DComponent transform = Transform3DComponent.components.get(entity);
		Translation3D translation = Translation3D.components.get(entity);
		
		translation.time += deltaTime;
		
		float t = translation.interpolation.apply(MathUtils.clamp(translation.time / translation.duration, 0, 1));
		
		transform.position.set(translation.origin).scl(1-t).mulAdd(translation.target, t);
	}

}
