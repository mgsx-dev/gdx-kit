package net.mgsx.game.examples.shmup.blueprint;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.game.blueprint.annotations.Inlet;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.examples.shmup.system.ShmupEnemySystem;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

@Inlet
public class ExitScreen extends TransitionNode
{
	public static enum Direction{ANY,TOP,LEFT,RIGHT,BOTTOM}
	
	@Editable
	public Direction dir = Direction.ANY;
	
	
	@Override
	public boolean isActive(Engine engine, Entity entity) {
		
		Transform2DComponent transform = Transform2DComponent.components.get(entity);
		
		Camera camera = engine.getSystem(ShmupEnemySystem.class).pov.camera;
		
		float z = camera.project(new Vector3()).z;
		
		Vector3 a = camera.unproject(new Vector3(0, 0, z));
		Vector3 b = camera.unproject(new Vector3(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), z));
		
		if((dir == Direction.LEFT || dir == Direction.ANY) && transform.position.x < a.x){
			return true;
		}
		if((dir == Direction.RIGHT || dir == Direction.ANY) && transform.position.x > b.x){
			return true;
		}
		if((dir == Direction.TOP || dir == Direction.ANY) && transform.position.y > a.y){
			return true;
		}
		if((dir == Direction.BOTTOM || dir == Direction.ANY) && transform.position.y < b.y){
			return true;
		}
		
		return false;
	}

}
