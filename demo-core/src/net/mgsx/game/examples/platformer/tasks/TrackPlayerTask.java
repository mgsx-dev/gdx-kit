package net.mgsx.game.examples.platformer.tasks;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;
import com.badlogic.gdx.math.Vector2;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EnumType;
import net.mgsx.game.examples.platformer.inputs.PlayerController;
import net.mgsx.game.plugins.box2d.components.Box2DBodyModel;
import net.mgsx.game.plugins.btree.BTreePlugin.EntityLeafTask;
import net.mgsx.game.plugins.btree.annotations.TaskAlias;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

@TaskAlias("trackPlayer")
public class TrackPlayerTask extends EntityLeafTask
{
	private ImmutableArray<Entity> players;
	
	@Editable(type=EnumType.UNIT)
	@TaskAttribute
	public float smooth = 1f;
	
	@TaskAttribute
	public float ox, oy;
	
	private Vector2 playerTarget = new Vector2();
	private Vector2 currentTarget = new Vector2();
	
	@Override
	public void start() {
		players = getEngine().getEntitiesFor(Family.all(PlayerController.class).get());
		Box2DBodyModel physic = Box2DBodyModel.components.get(getEntity());
		if(physic != null){
			physic.body.setActive(true);
		}
	}
	
	@Override
	public void end() {
		Box2DBodyModel physic = Box2DBodyModel.components.get(getEntity());
		if(physic != null){
			physic.body.setActive(false);
		}
	}
	
	@Override
	public Status execute() 
	{
		// try to get position from player and set the target.
		if(players.size() > 0){
			Entity playerEntity = players.first();
			Transform2DComponent playerTransform = Transform2DComponent.components.get(playerEntity);
			if(playerTransform != null){
				playerTarget.set(playerTransform.position).add(ox, oy);
			}
			
		}
		
		// move collider to player target
		
		Box2DBodyModel physic = Box2DBodyModel.components.get(getEntity());
		if(physic != null){
			physic.body.setActive(true);
			currentTarget.set(physic.body.getPosition());
			physic.body.setLinearVelocity(physic.body.getPosition().sub(playerTarget).scl(-30)); // TODO timestep
		}
		
		Transform2DComponent transform = Transform2DComponent.components.get(getEntity());
		if(transform == null) return Status.FAILED;
		
		transform.enabled = false;
		transform.position.lerp(currentTarget, smooth);
		
		return Status.RUNNING;
	}
}
