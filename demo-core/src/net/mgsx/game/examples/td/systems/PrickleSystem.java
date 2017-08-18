package net.mgsx.game.examples.td.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.examples.td.components.Damage;
import net.mgsx.game.examples.td.components.Enemy;
import net.mgsx.game.examples.td.components.Life;
import net.mgsx.game.examples.td.components.Road;
import net.mgsx.game.examples.td.components.TileComponent;

public class PrickleSystem extends IteratingSystem
{
	private Rectangle rectangle = new Rectangle(0, 0, 1, 1);
	private Array<Entity> targets = new Array<Entity>();
	private Family targetable = Family.all(Enemy.class, Life.class).get();
	
	public PrickleSystem() {
		super(Family.all(TileComponent.class, Road.class, Damage.class).get(), GamePipeline.LOGIC);
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		TileComponent tile = TileComponent.components.get(entity);
		Damage damage = Damage.components.get(entity);
		MapSystem map = getEngine().getSystem(MapSystem.class);
		
		targets.clear();
		rectangle.x = tile.x;
		rectangle.y = tile.y;
		map.getEntities(targets, targetable, rectangle);
		for(Entity entityInTile : targets)
		{
			Life life = Life.components.get(entityInTile);
			life.current -= damage.amount * deltaTime;
		}
	}
}
