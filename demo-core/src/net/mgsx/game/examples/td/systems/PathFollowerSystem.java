package net.mgsx.game.examples.td.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.examples.td.components.PathFollower;
import net.mgsx.game.examples.td.components.Speed;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

public class PathFollowerSystem extends IteratingSystem
{
	private Vector2 derivative = new Vector2();
	private Vector3 derivative3d = new Vector3();
	private Vector3 position3d = new Vector3();
	
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
		
		if(pathFollower.path3d != null){
			pathFollower.path3d.valueAt(position3d, pathFollower.t);
			pathFollower.path3d.derivativeAt(derivative3d, pathFollower.t);
			Vector3 ppos = new Vector3(transform.position, transform.depth);
			transform.position.set(position3d.x, position3d.y);
			transform.depth = position3d.z;
			// transform.angle = derivative.set(derivative3d.x, derivative3d.y).angle();
			// transform.normal.set(derivative3d.crs(ppos.sub(position3d).scl(-1).nor())).nor();
			transform.derivative.set(derivative3d).nor();
		}else{
			
			pathFollower.path.valueAt(transform.position, pathFollower.t);
			
			pathFollower.path.derivativeAt(derivative, pathFollower.t);
			transform.angle = derivative.angle();
		}
		
	}
}
