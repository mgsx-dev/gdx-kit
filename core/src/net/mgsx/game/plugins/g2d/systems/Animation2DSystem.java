package net.mgsx.game.plugins.g2d.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.plugins.g2d.components.Animation2DComponent;

public class Animation2DSystem extends IteratingSystem
{
	private static ComponentMapper<Animation2DComponent> mapper = ComponentMapper.getFor(Animation2DComponent.class);

	public Animation2DSystem() {
		super(Family.one(Animation2DComponent.class).get(), GamePipeline.BEFORE_RENDER);
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		Animation2DComponent component = mapper.get(entity);
		component.time += deltaTime;
	}

}
