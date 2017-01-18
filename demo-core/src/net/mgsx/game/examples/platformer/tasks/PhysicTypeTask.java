package net.mgsx.game.examples.platformer.tasks;

import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import net.mgsx.game.plugins.box2d.components.Box2DBodyModel;
import net.mgsx.game.plugins.btree.BTreePlugin.EntityLeafTask;
import net.mgsx.game.plugins.btree.annotations.TaskAlias;

@TaskAlias("physicsType")
public class PhysicTypeTask extends EntityLeafTask
{
	@TaskAttribute
	public boolean dynamic;
	
	@Override
	public Status execute() {
		Box2DBodyModel physics = Box2DBodyModel.components.get(getObject().entity);
		if(physics != null){
			physics.body.setType(dynamic ? BodyType.DynamicBody : BodyType.KinematicBody);
		}
		
		return Status.SUCCEEDED;
	}
}
