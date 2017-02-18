package net.mgsx.game.examples.platformer.tasks;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.game.core.annotations.TaskAsset;
import net.mgsx.game.core.components.Movable;
import net.mgsx.game.core.storage.EntityGroup;
import net.mgsx.game.plugins.box2d.components.Box2DBodyModel;
import net.mgsx.game.plugins.btree.BTreeModel;
import net.mgsx.game.plugins.btree.BTreePlugin.EntityLeafTask;
import net.mgsx.game.plugins.btree.annotations.TaskAlias;
import net.mgsx.game.plugins.core.components.Transform2DComponent;
import net.mgsx.game.plugins.particle2d.components.Particle2DComponent;

@TaskAlias("emitOnce")
public class EmitOnce extends EntityLeafTask
{
	@TaskAsset(EntityGroup.class)
	@TaskAttribute
	public String particle;
	
	@Override
	public void start() {
		Transform2DComponent transform = Transform2DComponent.components.get(getEntity());
		if(transform != null){
			EntityGroup template = getObject().assets.get(particle, EntityGroup.class);
			for(Entity entity : template.create(getObject().assets, getEngine())){
				if(transform == null) continue;
				Movable m = Movable.components.get(entity);
				if(m != null){
					m.move(entity, new Vector3(transform.position, 0));
				}
				Box2DBodyModel childPhysics = Box2DBodyModel.components.get(entity);
				if(childPhysics != null){
					childPhysics.def.position.set(transform.position);
				}
				Transform2DComponent t = Transform2DComponent.components.get(entity);
				if(transform != null){
					t.position.add(transform.position);
				}
				BTreeModel btree = BTreeModel.components.get(entity);
				if(btree!=null){
					btree.enabled = true;
					btree.remove = true;
				}
				Particle2DComponent p = Particle2DComponent.components.get(entity);
				if(p != null){
					p.autoRemove = true;
				}
			}
		}
	}
}