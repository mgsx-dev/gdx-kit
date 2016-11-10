package net.mgsx.game.plugins.g3d.systems;

import com.badlogic.ashley.core.Entity;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.helpers.systems.ComponentIteratingSystem;
import net.mgsx.game.plugins.g3d.components.G3DModel;

public class G3DAnimationSystem extends ComponentIteratingSystem<G3DModel> {
	public G3DAnimationSystem() {
		super(G3DModel.class, GamePipeline.BEFORE_RENDER);
	}

	@Override
	protected void processEntity(Entity entity, G3DModel component, float deltaTime) {
		if(component.animationController != null){
			component.animationController.update(deltaTime);
		}
	}
}