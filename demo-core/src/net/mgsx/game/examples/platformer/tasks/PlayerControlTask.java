package net.mgsx.game.examples.platformer.tasks;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

import net.mgsx.game.examples.platformer.inputs.PlayerController;
import net.mgsx.game.examples.platformer.logic.Enemy;
import net.mgsx.game.examples.platformer.logic.LifeComponent;
import net.mgsx.game.plugins.box2d.components.Box2DBodyModel;
import net.mgsx.game.plugins.box2d.listeners.Box2DComponentListener;
import net.mgsx.game.plugins.box2d.listeners.Box2DListener;
import net.mgsx.game.plugins.box2d.listeners.Box2DMultiplexer;
import net.mgsx.game.plugins.btree.annotations.TaskAlias;

@TaskAlias("control")
public class PlayerControlTask extends ComponentTask
{
	public PlayerControlTask() {
		super(PlayerController.class);
	}
	
	@Override
	public void start() {
		super.start();
		Box2DBodyModel physics = Box2DBodyModel.components.get(getEntity());
		final PlayerController player = PlayerController.components.get(getEntity());
		if(physics != null){
			
			Box2DListener enemyListener = new Box2DComponentListener<Enemy>(Enemy.class){
				@Override
				protected void preSolve(Contact contact, Fixture self, Fixture other, Entity otherEntity,
						Enemy otherComponent, Manifold oldManifold) 
				{
					boolean fromTop = oldManifold.getLocalNormal().y > 0.2f;
					
					Vector2 dir = self.getBody().getPosition().cpy().sub(other.getBody().getPosition()).nor();
					fromTop = dir.y > .2f;
					
					if(fromTop){
						//contact.setRestitution(1f);
						self.getBody().applyLinearImpulse(new Vector2(0, 1).scl(10), self.getBody().getWorldCenter(), true);
						LifeComponent enemyLife = LifeComponent.components.get(otherEntity);
						if(enemyLife != null){
							enemyLife.life = -1;
						}
					}else{
						if(player != null && !player.isHurt){
							boolean fromRight = dir.x < 0;
							self.getBody().applyLinearImpulse(new Vector2(fromRight ? -1 : 1, 0).scl(30), self.getBody().getWorldCenter(), true);
							player.isHurt = true;
						}
					}
				}
				
				
			};
			
			 
			
			physics.setListener(new Box2DMultiplexer(enemyListener));
		}
	}
	
	@Override
	public void end() {
		Box2DBodyModel physics = Box2DBodyModel.components.get(getEntity());
		if(physics != null){
			physics.setListener(null);
		}
		super.end();
	}
}
