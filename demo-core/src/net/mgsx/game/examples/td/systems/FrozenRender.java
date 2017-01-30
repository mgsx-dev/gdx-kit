package net.mgsx.game.examples.td.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.Color;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.GameScreen;
import net.mgsx.game.examples.td.components.Frozen;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

public class FrozenRender extends AbstractShapeSystem {

	public FrozenRender(GameScreen game) {
		super(game, Family.all(Frozen.class, Transform2DComponent.class).get(), GamePipeline.RENDER);
		renderer.setColor(Color.CYAN);
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		Transform2DComponent transform = Transform2DComponent.components.get(entity);
		float s = .05f;
		renderer.rect(transform.position.x - .4f, transform.position.y + .4f, s, s);
	}
}
