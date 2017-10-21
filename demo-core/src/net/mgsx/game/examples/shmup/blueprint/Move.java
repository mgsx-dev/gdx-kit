package net.mgsx.game.examples.shmup.blueprint;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.game.blueprint.annotations.Inlet;
import net.mgsx.game.blueprint.annotations.Outlet;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.examples.shmup.component.Enemy;
import net.mgsx.game.examples.shmup.system.ShmupEmitterSystem;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

@Inlet
public class Move implements StateNode
{
	@Outlet public StateNode exitScreen;
	
	@Editable
	public Vector2 velocity = new Vector2();

	@Override
	public void update(Engine engine, Entity entity, float deltaTime) 
	{
		Transform2DComponent transform = Transform2DComponent.components.get(entity);
		if(transform != null){
			transform.position.mulAdd(velocity, deltaTime);
		}
		
		Camera camera = engine.getSystem(ShmupEmitterSystem.class).pov.camera;
		
		float z = camera.project(new Vector3()).z;
		
		Vector3 a = camera.unproject(new Vector3(0, 0, z));
		Vector3 b = camera.unproject(new Vector3(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), z));
		
		float tmp = a.y;
		a.y = b.y;
		b.y = tmp;
		
		boolean out = false;
		if(velocity.x < 0 && transform.position.x < a.x){
			out = true;
		}
		else if(velocity.x > 0 && transform.position.x > b.x){
			out = true;
		}
		// TODO verify b.y > a.y !!
		if(velocity.y < 0 && transform.position.y < a.y){
			out = true;
		}
		else if(velocity.y > 0 && transform.position.y > b.y){
			out = true;
		}
		
		if(out){
			// TODO could be done in system ... avoiding array scans
			Enemy enemy = Enemy.components.get(entity);
			enemy.replace(this, exitScreen);
		}
	}

}
