package net.mgsx.game.examples.rts.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.examples.rts.components.BulletComponent;
import net.mgsx.game.examples.rts.components.PlanetComponent;
import net.mgsx.game.examples.rts.components.TravalComponent;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

@EditableSystem
public class BulletSystem extends EntitySystem
{
	@Editable
	public int max = 0; // XXX 300;
	public ImmutableArray<Entity> bullets;
	
	public BulletSystem() {
		super(GamePipeline.LOGIC);
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		bullets = engine.getEntitiesFor(Family.all(BulletComponent.class).get());
	}
	
	@Override
	public void update(float deltaTime) 
	{
		int currentCount = bullets.size();
		while(currentCount < max){
			// spawn
			Entity entity = getEngine().createEntity();
			BulletComponent bullet = getEngine().createComponent(BulletComponent.class);
			bullet.speed = MathUtils.random(.1f, 1.f);
			bullet.origin.set(MathUtils.random(-5, 5), MathUtils.random(-5, 5));
			bullet.distance = MathUtils.random(2, 10);
			bullet.direction.set(Vector2.X).setAngle(MathUtils.random(0, 360));
			bullet.color.set(
					MathUtils.random(0.2f, 0.5f), 
					MathUtils.random(0.2f, 0.8f), 
					MathUtils.random(0.8f, 0.9f), 1);
			entity.add(bullet);
			getEngine().addEntity(entity);
			currentCount++;
		}
		
		for(Entity entity : bullets){
			BulletComponent bullet = BulletComponent.components.get(entity);
			bullet.time += deltaTime * bullet.speed;
			TravalComponent travel = TravalComponent.components.get(entity);
			if(travel != null){
				Transform2DComponent target = Transform2DComponent.components.get(travel.dstPlanet);
				PlanetComponent dstPlanet = PlanetComponent.components.get(travel.dstPlanet);
				bullet.direction.set(target.position).sub(bullet.position);
				if(bullet.direction.len2() < 0.25f * dstPlanet.size * dstPlanet.size){
					dstPlanet.population+=1;
					getEngine().removeEntity(entity);
				}else{
					// bullet.distance = bullet.direction.len();
					bullet.direction.nor();
					bullet.position.mulAdd(bullet.direction, bullet.speed * deltaTime);
				}
			}else{
				if(bullet.time >= bullet.distance){
					getEngine().removeEntity(entity);
				}else{
					bullet.position.set(bullet.origin).mulAdd(bullet.direction, bullet.time);
				}
			}
		}
	}
}
