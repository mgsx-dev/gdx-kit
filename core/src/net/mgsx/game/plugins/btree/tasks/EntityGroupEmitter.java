package net.mgsx.game.plugins.btree.tasks;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;

import net.mgsx.game.core.annotations.TaskAsset;
import net.mgsx.game.core.storage.EntityGroup;
import net.mgsx.game.core.storage.EntityGroupStorage;
import net.mgsx.game.plugins.btree.BTreePlugin.EntityLeafTask;
import net.mgsx.game.plugins.btree.annotations.TaskAlias;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

@TaskAlias("emit")
public class EntityGroupEmitter extends EntityLeafTask
{
	@TaskAsset(EntityGroup.class)
	@TaskAttribute(required=true)
	public String fileName;
	
	/** when relative, current entity position is added to emitted entities position. When not (absolute)
	 * then emitted entities position is set to current entity position. */
	@TaskAttribute
	public boolean relative = false;
	
	@Override
	public com.badlogic.gdx.ai.btree.Task.Status execute() 
	{
		Transform2DComponent selfTransform = Transform2DComponent.components.get(getEntity());
		for(Entity entityClone : EntityGroupStorage.get(getObject().assets, getObject().engine, fileName)){
			if(selfTransform != null){
				Transform2DComponent transform = Transform2DComponent.components.get(entityClone);
				if(transform != null){
					if(relative){
						transform.position.add(selfTransform.position);
					}else{
						transform.position.set(selfTransform.position);
					}
				}
			}
		}
		return Status.SUCCEEDED;
	}
}
