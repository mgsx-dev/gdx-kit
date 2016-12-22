package net.mgsx.game.examples.platformer.tasks;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.ai.GdxAI;
import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EnumType;
import net.mgsx.game.examples.platformer.inputs.PlayerController;
import net.mgsx.game.plugins.btree.BTreePlugin.EntityLeafTask;
import net.mgsx.game.plugins.btree.annotations.TaskAlias;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

@TaskAlias("trackPlayer")
public class TrackPlayerTask extends EntityLeafTask
{
	private ImmutableArray<Entity> players;
	
	@Editable(type=EnumType.UNIT)
	@TaskAttribute
	public float smooth = 0.01f;
	
	@Override
	public void start() {
		players = getEngine().getEntitiesFor(Family.all(PlayerController.class).get());
	}
	
	@Override
	public Status execute() {
		if(players.size() <= 0) return Status.FAILED;
		Entity playerEntity = players.first();
		Transform2DComponent playerTransform = Transform2DComponent.components.get(playerEntity);
		if(playerTransform == null) return Status.FAILED;
		
		Transform2DComponent transform = Transform2DComponent.components.get(getEntity());
		if(transform == null) return Status.FAILED;
		
		transform.position.lerp(playerTransform.position, smooth * GdxAI.getTimepiece().getDeltaTime());
		
		return Status.RUNNING;
	}
}
