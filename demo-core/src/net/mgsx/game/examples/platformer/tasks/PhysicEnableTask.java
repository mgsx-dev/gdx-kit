package net.mgsx.game.examples.platformer.tasks;

import net.mgsx.game.plugins.box2d.components.Box2DBodyModel;
import net.mgsx.game.plugins.btree.BTreePlugin.EntityLeafTask;
import net.mgsx.game.plugins.btree.annotations.TaskAlias;

@TaskAlias("physicsEnable")
public class PhysicEnableTask extends EntityLeafTask
{
	@Override
	public Status execute() {
		Box2DBodyModel physics = Box2DBodyModel.components.get(getObject().entity);
		if(physics != null){
			physics.body.setActive(true);
		}
		
		return Status.SUCCEEDED;
	}
}
