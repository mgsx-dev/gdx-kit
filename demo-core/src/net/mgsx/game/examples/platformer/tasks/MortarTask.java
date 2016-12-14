package net.mgsx.game.examples.platformer.tasks;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import net.mgsx.game.examples.platformer.ai.MortarComponent;
import net.mgsx.game.examples.platformer.inputs.PlayerController;
import net.mgsx.game.plugins.box2d.components.Box2DBodyModel;
import net.mgsx.game.plugins.btree.BTreePlugin.EntityLeafTask;
import net.mgsx.game.plugins.core.components.ExpiryComponent;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

public class MortarTask extends EntityLeafTask
{
	@Override
	public Status execute() 
	{
		Entity entity = getObject().entity;
	
		ImmutableArray<Entity> players = getEngine().getEntitiesFor(Family.all(PlayerController.class, Transform2DComponent.class).get());
		
		if(players.size() <= 0) return Status.FAILED;
		
		Entity playerEntity = players.first();
		Transform2DComponent playerTransform = Transform2DComponent.components.get(playerEntity);
		
		Transform2DComponent parentTransform = Transform2DComponent.components.get(entity);
		MortarComponent mortar = MortarComponent.components.get(entity);
		
		for(Entity child : mortar.projectile.group.create(getObject().assets, getObject().engine)){
			ExpiryComponent expiry = getEngine().createComponent(ExpiryComponent.class);
			expiry.time = mortar.expiry;
			child.add(expiry);
			Box2DBodyModel childPhysics = Box2DBodyModel.components.get(child);
			Transform2DComponent childTransform = Transform2DComponent.components.get(child);
			if(childPhysics != null){
				// body is null has it has not been created yet !
				childPhysics.def.type = BodyType.DynamicBody; // TODO sure ??
				if(childTransform != null){
					childTransform.position.add(parentTransform.position).add(mortar.offset);
				}
				childPhysics.def.linearVelocity.set(new Vector2(mortar.speed, 0).setAngle(mortar.angle));
				if(playerTransform.position.x < parentTransform.position.x)
					childPhysics.def.linearVelocity.x = -childPhysics.def.linearVelocity.x;
			}
		}
		
		return Status.SUCCEEDED;
	}
	
}
