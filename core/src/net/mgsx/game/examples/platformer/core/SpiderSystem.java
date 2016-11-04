package net.mgsx.game.examples.platformer.core;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.components.Transform2DComponent;
import net.mgsx.game.plugins.boundary.components.BoundaryComponent;
import net.mgsx.game.plugins.box2d.model.Box2DBodyModel;
import net.mgsx.game.plugins.g3d.G3DModel;

public class SpiderSystem extends IteratingSystem
{
	public SpiderSystem() {
		super(Family.all(SpiderComponent.class, Box2DBodyModel.class, G3DModel.class).get(), GamePipeline.LOGIC);
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) 
	{
		SpiderComponent spider = entity.getComponent(SpiderComponent.class);
		Box2DBodyModel physics = entity.getComponent(Box2DBodyModel.class);
		G3DModel model = entity.getComponent(G3DModel.class);
		
		BoundaryComponent boundary = BoundaryComponent.components.get(entity);
		if(boundary != null && boundary.justInside){
			// reset
			physics.body.setActive(true);
			physics.body.setTransform(326.92f,  -36.8f, 0);
			spider.time = 0;
			spider.speed = 0;
		}
		if(boundary != null && boundary.justOutside){
			physics.body.setActive(false);
		}
		if(boundary != null && !boundary.inside){
			return;
		}
		
		// TODO maybe a system before logic (after inputs) to set kinematics
		// then during logic
		
		if(!spider.init){
			spider.init = true;
			entity.getComponent(Transform2DComponent.class).enabled = false;
			model.animationController.allowSameAnimation = true;
			model.animationController.setAnimation("SpiderSkeleton|Spider.Walk.loop", -1);
		}
		
		spider.time += deltaTime;
		if(spider.time > 1){
			// choose a target
			spider.zone.set(326.92f, -36.8f, 20, 0);
			spider.target.set(spider.zone.x + MathUtils.random(spider.zone.width), physics.body.getPosition().y);
			spider.time = 0;
			spider.speed = Math.abs(spider.target.x - physics.body.getPosition().x) * 0.1f;
		}
		
		
		
		float dx = 0;
//		if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
//			dx = -1;
//		}else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
//			dx = 1;
//		}
		
		dx = spider.target.x - physics.body.getPosition().x;
		if(dx > 0.1f) dx = 1; else if(dx < -0.1f) dx = -1;
		dx = dx * 0.2f * spider.speed;
		
		
		float speed = -dx * 10f;
		model.animationController.current.speed = speed; //Math.abs(speed);
		float x = physics.body.getPosition().x;
		float y = physics.body.getPosition().y;
		x += dx;
		physics.body.setLinearVelocity(dx/deltaTime, 0);
		
	//	float t = model.animationController.current.time /  model.animationController.current.duration;
		
		model.modelInstance.transform.idt();
		model.modelInstance.transform.translate(x + 2.1f ,y-0.2f,0);
//		model.modelInstance.transform.rotate(Vector3.Y, dx > 0 ? 180 : 0);
//		model.modelInstance.transform.translate(1,0,0);
	}

}
