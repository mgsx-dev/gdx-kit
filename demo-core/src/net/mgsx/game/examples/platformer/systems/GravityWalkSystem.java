package net.mgsx.game.examples.platformer.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.examples.platformer.components.GravityWalk;
import net.mgsx.game.examples.platformer.components.PlayerController;
import net.mgsx.game.plugins.box2d.components.Box2DBodyModel;
import net.mgsx.game.plugins.box2d.systems.Box2DWorldContext.RayCastResult;

public class GravityWalkSystem extends IteratingSystem {

	private final Vector2 force = new Vector2();
	
	public GravityWalkSystem() {
		super(Family.all(GravityWalk.class, Box2DBodyModel.class, PlayerController.class).get(), GamePipeline.AFTER_LOGIC);
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		Box2DBodyModel physics = Box2DBodyModel.components.get(entity);
		GravityWalk gravity = GravityWalk.components.get(entity);
		PlayerController player = PlayerController.components.get(entity);
		boolean ok = false;
		if(player.right || player.left){
			float spd = player.right ? 1 : player.left ? -1 : 0;
			RayCastResult result = physics.context.rayCastFirstDetails(physics.body.getPosition(), force.set(gravity.groundNormal).scl(-1), 0.5f);
			if(result.isValid()){
				gravity.groundNormal.set(result.normal.nor());
				if(gravity.groundNormal.y < .8f){
					physics.body.setLinearVelocity(result.normal.scl(-gravity.force).add(force.set(result.normal).rotate(90).scl(spd * 5)));
					physics.body.setTransform(physics.body.getPosition(), gravity.groundNormal.angleRad());
					ok = true;
				}
			}
		}
		if(!ok){
			gravity.groundNormal.set(0,1);
			physics.body.setTransform(physics.body.getPosition(), 0);
		}
		
	}


}
