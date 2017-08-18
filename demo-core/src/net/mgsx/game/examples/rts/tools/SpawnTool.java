package net.mgsx.game.examples.rts.tools;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.tools.RectangleTool;
import net.mgsx.game.examples.rts.components.BulletComponent;

public class SpawnTool extends RectangleTool
{
	public SpawnTool(EditorScreen editor) {
		super("Spawn", editor);
	}

	@Override
	protected void create(Vector2 startPoint, Vector2 endPoint) 
	{
		Vector2 dir = new Vector2(endPoint).sub(startPoint);
		float dist = dir.len();
		dir.scl(1.f / dist);
		int count = (int)(dist * 4);
		float dispertion = .5f;
		for(int i=0 ; i<count ; i++){
			Entity entity = getEngine().createEntity();
			BulletComponent bullet = getEngine().createComponent(BulletComponent.class);
			bullet.speed = MathUtils.random(.9f, 1.f);
			bullet.origin.set(MathUtils.random(-dispertion, dispertion), MathUtils.random(-dispertion, dispertion)).add(startPoint);
			bullet.distance = MathUtils.random(-dispertion, dispertion) + dist;
			bullet.direction.set(dir);
			bullet.color.set(
					MathUtils.random(0.2f, 0.5f), 
					MathUtils.random(0.2f, 0.8f), 
					MathUtils.random(0.8f, 0.9f), 1);
			entity.add(bullet);
			getEngine().addEntity(entity);

		}
	}

}
