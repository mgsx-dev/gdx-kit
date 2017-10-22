package net.mgsx.game.examples.shmup.blueprint;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import net.mgsx.game.blueprint.annotations.Inlet;
import net.mgsx.game.blueprint.annotations.Outlet;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.examples.shmup.component.Emitter;
import net.mgsx.game.examples.shmup.component.Enemy;
import net.mgsx.game.examples.shmup.component.EnemyBullet;
import net.mgsx.game.examples.shmup.system.ShmupPlayerSystem;
import net.mgsx.game.examples.shmup.utils.ShmupCollision;
import net.mgsx.game.plugins.box2d.components.Box2DBodyModel;
import net.mgsx.game.plugins.box2d.components.Box2DFixtureModel;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

@Inlet
public class EmitBullet implements StateNode {

	@Outlet public transient StateNode over;
	
	@Editable
	public int count;
	
	@Editable
	public float delay;
	
	@Editable
	public float angle;
	
	@Editable
	public float speed;
	
	@Editable
	public boolean player;
	
	private transient Vector2 velocity = new Vector2();
	
	@Override
	public void update(Engine engine, Entity entity, float deltaTime)
	{
		Emitter emitter = Emitter.components.get(entity);
		if(emitter == null){
			emitter = engine.createComponent(Emitter.class);
			emitter.remains = count;
			entity.add(emitter);
		}
		
		Transform2DComponent emitterTransform = Transform2DComponent.components.get(entity);
		if(emitter.remains > 0 || count < 0){
			emitter.timeout -= deltaTime;
			if(emitter.timeout < 0){
				emitter.timeout = delay;
				emitter.remains--;
				
				boolean velocitySet = false;
				if(player){
					ImmutableArray<Entity> players = engine.getSystem(ShmupPlayerSystem.class).getEntities();
					if(players.size() > 0){
						Entity playerEntity = players.first();
						Transform2DComponent tPlayer = Transform2DComponent.components.get(playerEntity);
						if(tPlayer != null){
							velocity.set(tPlayer.position).sub(emitterTransform.position).nor()
								.scl(speed);
							velocitySet = true;
						}
					}
				}
				if(!velocitySet){
					velocity.set(speed, 0).rotate(angle);
				}
				
				Entity e = engine.createEntity();
				
				Box2DBodyModel physics = engine.createComponent(Box2DBodyModel.class);
				BodyDef def = physics.def = new BodyDef();
				def.allowSleep = false;
				def.type = BodyType.DynamicBody;
				def.bullet = true;
				def.linearVelocity.set(velocity);
				def.gravityScale = 0;
				def.position.set(emitterTransform.position);
				
				Box2DFixtureModel fixMod = new Box2DFixtureModel();
				FixtureDef fdef = fixMod.def = new FixtureDef();
				CircleShape shape = new CircleShape();
				shape.setRadius(.5f);
				fdef.filter.categoryBits = ShmupCollision.enemyBullet;
				fdef.filter.maskBits = ShmupCollision.enemyBulletMask;
				fdef.shape = shape;
				physics.fixtures.add(fixMod);
				
				engine.addEntity(e.add(physics).add(engine.createComponent(EnemyBullet.class)));
			}
		}
		else
		{
			entity.remove(Emitter.class);
			Enemy enemy = Enemy.components.get(entity);
			enemy.replace(this, over);
		}
		
	}
}
