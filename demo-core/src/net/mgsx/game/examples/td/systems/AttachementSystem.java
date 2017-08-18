package net.mgsx.game.examples.td.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.examples.td.components.Attachement;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

public class AttachementSystem extends IteratingSystem
{
	public AttachementSystem() {
		super(Family.all(Attachement.class, Transform2DComponent.class).get(), GamePipeline.LOGIC);
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		Attachement attachement = Attachement.components.get(entity);
		Transform2DComponent transform = Transform2DComponent.components.get(entity);
		if(attachement.parent != null)
		{
			Transform2DComponent parentTransform = Transform2DComponent.components.get(attachement.parent);
			if(parentTransform != null){
				transform.position.set(attachement.offset).rotate(parentTransform.angle).add(parentTransform.position);
			}
		}
	}
}
