package net.mgsx.game.examples.platformer.tasks;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.game.core.annotations.Asset;
import net.mgsx.game.core.components.Movable;
import net.mgsx.game.core.storage.EntityGroup;
import net.mgsx.game.plugins.box2d.components.Box2DBodyModel;
import net.mgsx.game.plugins.btree.BTreeModel;
import net.mgsx.game.plugins.btree.BTreePlugin.EntityLeafTask;
import net.mgsx.game.plugins.btree.annotations.TaskAlias;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

@TaskAlias("emitAtDistance")
public class EmitAtDistanceTask extends EntityLeafTask
{
	@Asset(EntityGroup.class)
	@TaskAttribute
	public String particle;
	
	@TaskAttribute
	public float distance = 1;

	private Vector2 lastDropPosition = new Vector2();
	
	public int dropCount;
	
	@Override
	public void start() {
		dropCount = 0;
		Transform2DComponent transform = Transform2DComponent.components.get(getEntity());
		if(transform != null){
			lastDropPosition.set(transform.position);
		}
	}
	
	@Override
	public Status execute() 
	{
		Transform2DComponent transform = Transform2DComponent.components.get(getEntity());
		if(transform != null){
			Vector2 dif = new Vector2().set(transform.position).sub(lastDropPosition);
			transform.angle = dif.angleRad();
			if(lastDropPosition.dst(transform.position) >= distance){
				// drop
				
				
				
				lastDropPosition.set(transform.position);
				EntityGroup template = getObject().assets.get(particle, EntityGroup.class);
				for(Entity entity : template.create(getObject().assets, getEngine())){
					Movable m = Movable.components.get(entity);
					if(m != null){
						m.move(entity, new Vector3(lastDropPosition, 0));
					}
					Box2DBodyModel childPhysics = Box2DBodyModel.components.get(entity);
					if(childPhysics != null){
						childPhysics.def.position.set(lastDropPosition);
						childPhysics.def.angle = dif.angleRad();
					}
					Transform2DComponent t = Transform2DComponent.components.get(entity);
					if(transform != null){
						t.position.add(lastDropPosition);
						t.rotation = true;
						t.angle = dif.angleRad();
					}
					BTreeModel btree = BTreeModel.components.get(entity);
					if(btree!=null){
						btree.enabled = true;
						btree.remove = true;
					}
					
				}
				dropCount++;
			}
		}
		return Status.RUNNING;
	}
}
