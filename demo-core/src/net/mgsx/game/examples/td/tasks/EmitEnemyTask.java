package net.mgsx.game.examples.td.tasks;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;

import net.mgsx.game.examples.td.components.Enemy;
import net.mgsx.game.examples.td.components.Life;
import net.mgsx.game.examples.td.components.PathFollower;
import net.mgsx.game.examples.td.components.TileComponent;
import net.mgsx.game.plugins.btree.BTreePlugin.EntityLeafTask;
import net.mgsx.game.plugins.btree.annotations.TaskAlias;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

@TaskAlias("enemy")
public class EmitEnemyTask extends EntityLeafTask
{
	@TaskAttribute
	public float speed = 2;
	
	@Override
	public com.badlogic.gdx.ai.btree.Task.Status execute() 
	{
		// TODO create entity from template ... ?
		
		Entity entity = getEngine().createEntity();
		
		Enemy enemy = getEngine().createComponent(Enemy.class);
		Life life = getEngine().createComponent(Life.class);
		PathFollower path = getEngine().createComponent(PathFollower.class);
		Transform2DComponent transform = getEngine().createComponent(Transform2DComponent.class);
		
		TileComponent tile = TileComponent.components.get(getEntity());
		
		path.speed = speed;
		path.tx = tile.x;
		path.ty = tile.y;
		path.t = 1;
		
		transform.position.set(path.tx + .5f, path.ty + .5f);
		
		life.max = life.current = 10;
		
		entity.add(transform);
		entity.add(path);
		entity.add(enemy);
		entity.add(life);
		
		getEngine().addEntity(entity);
		
		return Status.SUCCEEDED;
	}
}
