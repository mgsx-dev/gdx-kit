package net.mgsx.game.plugins.core.tasks;

import com.badlogic.gdx.ai.GdxAI;
import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;
import com.badlogic.gdx.math.Vector2;

import net.mgsx.game.plugins.btree.BTreePlugin.EntityLeafTask;
import net.mgsx.game.plugins.btree.annotations.TaskAlias;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

@TaskAlias("move")
public class MoveTask extends EntityLeafTask
{
	@TaskAttribute
	public float speed = 1, angle;
	
	@TaskAttribute
	public float duration;
	
	private float time;
	
	private Vector2 velocity = new Vector2();
	
	@Override
	public void start() {
		time = 0;
	}
	
	@Override
	public Status execute() {
		time += GdxAI.getTimepiece().getDeltaTime();
		Transform2DComponent transform = Transform2DComponent.components.get(getEntity());
		if(transform != null){
			velocity.set(speed * GdxAI.getTimepiece().getDeltaTime(), 0).rotate(angle);
			transform.position.add(velocity);
		}
		return time > duration ? Status.SUCCEEDED : Status.RUNNING;
	}
}
