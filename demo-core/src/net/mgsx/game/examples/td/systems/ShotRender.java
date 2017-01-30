package net.mgsx.game.examples.td.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.GameScreen;
import net.mgsx.game.examples.td.components.Shot;

public class ShotRender extends AbstractShapeSystem
{
	private Vector2 pos = new Vector2();
	
	public ShotRender(GameScreen game) {
		super(game, Family.all(Shot.class).get(), GamePipeline.RENDER);
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		Shot shot = Shot.components.get(entity);
		
		pos.set(shot.start).lerp(shot.end, shot.t);
		
		renderer.setColor(Color.GREEN);
		final float s = .1f;
		renderer.rect(pos.x - s, pos.y - s, 2*s, 2*s);
	}
}
