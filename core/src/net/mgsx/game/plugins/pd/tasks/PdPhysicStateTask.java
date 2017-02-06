package net.mgsx.game.plugins.pd.tasks;

import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;

import net.mgsx.game.plugins.box2d.components.Box2DBodyModel;
import net.mgsx.game.plugins.btree.BTreePlugin.EntityLeafTask;
import net.mgsx.game.plugins.btree.annotations.TaskAlias;
import net.mgsx.pd.Pd;

@TaskAlias("pdPhysics")
public class PdPhysicStateTask extends EntityLeafTask
{
	@TaskAttribute
	public String symbol;
	
	@Override
	public Status execute() 
	{
		Box2DBodyModel physics = Box2DBodyModel.components.get(getEntity());
		if(physics != null){
			Pd.audio.sendList(symbol,
					physics.body.getPosition().x,
					physics.body.getPosition().y,
					physics.body.getAngle(),
					physics.body.getLinearVelocity().x,
					physics.body.getLinearVelocity().y,
					physics.body.getAngularVelocity(),
					physics.body.getMass(),
					physics.body.getInertia());
		}
		return Status.RUNNING;
	}
}