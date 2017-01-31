package net.mgsx.game.examples.td.tasks;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;

import net.mgsx.game.examples.td.components.Damage;
import net.mgsx.game.examples.td.components.Enemy;
import net.mgsx.game.examples.td.components.Enemy.Type;
import net.mgsx.game.examples.td.components.Life;
import net.mgsx.game.examples.td.components.PathFollower;
import net.mgsx.game.examples.td.components.TileComponent;
import net.mgsx.game.examples.td.systems.WaveSystem;
import net.mgsx.game.plugins.btree.BTreePlugin.EntityLeafTask;
import net.mgsx.game.plugins.btree.annotations.TaskAlias;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

@TaskAlias("enemy")
public class EmitEnemyTask extends EntityLeafTask
{
	@TaskAttribute
	public float speed = 2;
	
	@TaskAttribute
	public float life = 5;
	
	@TaskAttribute
	public float damage = 1;
	
	@TaskAttribute
	public Enemy.Type type = Type.SQUARE;
	
	@Override
	public com.badlogic.gdx.ai.btree.Task.Status execute() 
	{
		// TODO create entity from template ... ? or create sub tasks for each kind of enemy
		
		WaveSystem wave = getEngine().getSystem(WaveSystem.class);
		
		Entity entity = getEngine().createEntity();
		
		Enemy enemy = getEngine().createComponent(Enemy.class);
		Life life = getEngine().createComponent(Life.class);
		PathFollower path = getEngine().createComponent(PathFollower.class);
		Transform2DComponent transform = getEngine().createComponent(Transform2DComponent.class);
		Damage damage = getEngine().createComponent(Damage.class);
		
		TileComponent tile = TileComponent.components.get(getEntity());
		
		enemy.speed = speed;
		enemy.type = this.type;
		
		path.tx = tile.x;
		path.ty = tile.y;
		path.t = 1;
		
		transform.position.set(path.tx + .5f, path.ty + .5f);
		
		damage.amount = this.damage * wave.waveFactor;
		
		life.max = life.current = this.life * wave.waveFactor;
		
		entity.add(transform);
		entity.add(path);
		entity.add(enemy);
		entity.add(life);
		entity.add(damage);
		
		getEngine().addEntity(entity);
		
		return Status.SUCCEEDED;
	}
}
