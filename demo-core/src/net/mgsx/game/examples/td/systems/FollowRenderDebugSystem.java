package net.mgsx.game.examples.td.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.Color;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.GameScreen;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.examples.td.components.Follow;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

@EditableSystem(isDebug=true)
public class FollowRenderDebugSystem extends AbstractShapeSystem
{

	public FollowRenderDebugSystem(GameScreen game) {
		super(game, Family.all(Transform2DComponent.class, Follow.class).get(), GamePipeline.RENDER_DEBUG);
	}

	@Override
	public void update(float deltaTime) {
		renderer.setColor(0, 0, 1, 1);
		super.update(deltaTime);
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		Transform2DComponent transform = Transform2DComponent.components.get(entity);
		Follow follow = Follow.components.get(entity);
		if(follow == null || follow.head == null) return; // XXX ??
		Transform2DComponent targetTransform = Transform2DComponent.components.get(follow.head);

		renderer.rectLine(transform.position.x, transform.position.y, 
				targetTransform.position.x, targetTransform.position.y,
				0.1f,
				Color.BLUE, Color.LIGHT_GRAY);
		
	}

}
