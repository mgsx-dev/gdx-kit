package net.mgsx.game.examples.shmup.system;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.examples.shmup.blueprint.Init;
import net.mgsx.game.examples.shmup.component.Enemy;
import net.mgsx.game.examples.shmup.utils.ShmupCollision;
import net.mgsx.game.plugins.box2d.components.Box2DBodyModel;
import net.mgsx.game.plugins.box2d.listeners.Box2DAdapter;
import net.mgsx.game.plugins.camera.model.POVModel;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

public class ShmupEnemySystem extends IteratingSystem
{
	@Inject public POVModel pov;
	
	public ShmupEnemySystem() {
		super(Family.all(Transform2DComponent.class, Enemy.class).get(), GamePipeline.LOGIC);
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		engine.addEntityListener(Family.all(Enemy.class, Box2DBodyModel.class).get(), new EntityListener() {
			@Override
			public void entityRemoved(Entity entity) {
			}
			
			@Override
			public void entityAdded(Entity entity) {
				// XXX patch filters
				Box2DBodyModel physics = Box2DBodyModel.components.get(entity);
				if(physics.fixtures.size <= 0){
					// physics.fixtures.first()
				}else{
					Filter filter = physics.fixtures.first().fixture.getFilterData();
					filter.categoryBits = ShmupCollision.enemy;
					filter.maskBits = ShmupCollision.enemyMask;
					physics.fixtures.first().fixture.setFilterData(filter);
					
					physics.setListener(new Box2DAdapter() {
						@Override
						public void beginContact(Contact contact, Fixture self, Fixture other) {
							short cb = other.getFilterData().categoryBits;
							if(cb == ShmupCollision.playerBullet){
								Entity selfEntity = (Entity)self.getBody().getUserData();
								Enemy enemy = Enemy.components.get(selfEntity);
								enemy.life -= 1;
								if(enemy.life <= 0){
									getEngine().removeEntity(selfEntity);
								}
								
								System.out.println("enemy get shot");
								getEngine().removeEntity((Entity)other.getBody().getUserData());
							}else{
								System.out.println("enemy collide with ???");
							}
						}
					});
				}
				
			}
		});
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		Enemy enemy = Enemy.components.get(entity);
		
		if(enemy.current.size == 0){
			for(Init init : enemy.fsm.find(Init.class)){
				enemy.current.add(init);
			}
		}
		
		for(int i=0 ; i<enemy.current.size ; i++){
			enemy.current.get(i).update(getEngine(), entity, deltaTime);
		}
		
	}

}
