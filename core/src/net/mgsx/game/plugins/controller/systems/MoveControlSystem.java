package net.mgsx.game.plugins.controller.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.plugins.controller.components.MoveControl;
import net.mgsx.game.plugins.controller.components.OrientationControl;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

public class MoveControlSystem extends IteratingSystem
{
	private Vector2 direction = new Vector2();
	
	public MoveControlSystem() {
		super(Family.all(Transform2DComponent.class, MoveControl.class).get(), GamePipeline.INPUT);
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		MoveControl control = MoveControl.components.get(entity);
		Transform2DComponent transform = Transform2DComponent.components.get(entity);
		
		OrientationControl oc = OrientationControl.components.get(entity);
		
		if(oc != null){
			
			float vy = 0, v = control.speed * deltaTime;
			float va= 0, a = oc.angleSpeed * deltaTime;
			if(Gdx.input.isKeyPressed(control.leftKey)) va = a ; 
			else if(Gdx.input.isKeyPressed(control.rightKey)) va = -a;
			if(Gdx.input.isKeyPressed(control.upKey)) vy = v ; 
			else if(Gdx.input.isKeyPressed(control.downKey)) vy = -v;
			
			transform.angle += va;
			direction.set(vy, 0).rotate(transform.angle);
			transform.position.add(direction);

		}else{
			
			float vx = 0, vy = 0, v = control.speed * deltaTime;
			if(Gdx.input.isKeyPressed(control.leftKey)) vx = -v ; 
			else if(Gdx.input.isKeyPressed(control.rightKey)) vx = v;
			if(Gdx.input.isKeyPressed(control.upKey)) vy = v ; 
			else if(Gdx.input.isKeyPressed(control.downKey)) vy = -v;
			
			transform.position.add(vx,  vy);
		}
		
	}
}
