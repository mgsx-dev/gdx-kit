package net.mgsx.game.plugins.g2d.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.plugins.core.components.Transform2DComponent;
import net.mgsx.game.plugins.g2d.components.SpriteModel;

public class G2DTransformSystem extends IteratingSystem {
	public G2DTransformSystem() {
		super(Family.all(SpriteModel.class, Transform2DComponent.class).get(), GamePipeline.BEFORE_RENDER);
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		SpriteModel sprite = SpriteModel.components.get(entity);
		Transform2DComponent transform = Transform2DComponent.components.get(entity);
		// TODO enabled
		sprite.sprite.setPosition(transform.position.x, transform.position.y);
		sprite.sprite.setRotation(transform.angle);
		sprite.sprite.setOrigin(transform.origin.x, transform.origin.y);
	}
}