package net.mgsx.game.examples.td.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.GameScreen;
import net.mgsx.game.examples.td.components.Follow;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

public class FollowRenderSystem extends AbstractShapeSystem
{

	public FollowRenderSystem(GameScreen game) {
		super(game, Family.all(Transform2DComponent.class, Follow.class).get(), GamePipeline.RENDER);
	}

	@Override
	public void update(float deltaTime) {
		renderer.setColor(0, 0, 1, 1);
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ZERO);
		super.update(deltaTime);
		
		Gdx.gl.glDisable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		Transform2DComponent transform = Transform2DComponent.components.get(entity);
		Follow follow = Follow.components.get(entity);
		if(follow == null || follow.head == null) return; // XXX ??
		Transform2DComponent targetTransform = Transform2DComponent.components.get(follow.head);

		Color a = new Color(Color.BLUE);
		a.a = .2f;
		Color b = new Color(Color.CYAN);
		b.a = a.a;
		
		renderer.rectLine(transform.position.x, transform.position.y, 
				targetTransform.position.x, targetTransform.position.y,
				0.05f,
				a, b);
		
	}

}
