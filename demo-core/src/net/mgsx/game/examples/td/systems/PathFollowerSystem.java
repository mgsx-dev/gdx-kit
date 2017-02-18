package net.mgsx.game.examples.td.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.examples.td.components.PathFollower;
import net.mgsx.game.examples.td.components.Speed;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

public class PathFollowerSystem extends IteratingSystem
{
	private Vector2 derivative = new Vector2();
	
	public PathFollowerSystem() {
		super(Family.all(Transform2DComponent.class, Speed.class, PathFollower.class).get(), GamePipeline.LOGIC);
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) 
	{
		Transform2DComponent transform = Transform2DComponent.components.get(entity);
		PathFollower pathFollower = PathFollower.components.get(entity);
		Speed speed = Speed.components.get(entity);
		
		// move on path at constant speed.
		pathFollower.t += pathFollower.speed * speed.current * deltaTime / pathFollower.length;
		if(pathFollower.loop)
		{
			if(pathFollower.wrap){
				if(pathFollower.t > 1){
					pathFollower.t -= 1;
				}else if(pathFollower.t < 0){
					pathFollower.t += 1;
				}
			}
			else{
				if(pathFollower.speed > 0)
				{
					if(pathFollower.t > 1){
						pathFollower.speed = -pathFollower.speed;
					}
				}
				else
				{
					if(pathFollower.t < 0){
						pathFollower.speed = -pathFollower.speed;
					}
				}
				
			}
			
		}
		else
		{
			if(pathFollower.t > 1) pathFollower.t = 1;
			else if(pathFollower.t < 0) pathFollower.t = 0;
		}
		
		pathFollower.path.valueAt(transform.position, pathFollower.t);
		
		pathFollower.path.derivativeAt(derivative, pathFollower.t);
		transform.angle = derivative.angle();
	}
}
