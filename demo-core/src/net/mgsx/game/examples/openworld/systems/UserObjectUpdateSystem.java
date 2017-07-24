package net.mgsx.game.examples.openworld.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.examples.openworld.components.ObjectMeshComponent;

public class UserObjectUpdateSystem extends IteratingSystem
{

	public UserObjectUpdateSystem() {
		super(Family.all(ObjectMeshComponent.class).get(), GamePipeline.BEFORE_RENDER);
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		ObjectMeshComponent omc = ObjectMeshComponent.components.get(entity);
		omc.update();
	}

}
