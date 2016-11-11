package net.mgsx.game.plugins.box2d.tools;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.game.core.components.Movable;
import net.mgsx.game.plugins.box2d.components.Box2DJointModel;

public class Box2DJointMovable extends Movable
{
	@Override
	public void getPosition(Entity entity, Vector3 pos) 
	{
		Box2DJointModel model = entity.getComponent(Box2DJointModel.class);
		Vector2 p = model.joint.getAnchorA().add(model.joint.getAnchorB()).scl(0.5f);
		pos.set(p, 0);
	}
}
