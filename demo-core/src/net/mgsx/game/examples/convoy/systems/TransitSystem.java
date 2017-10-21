package net.mgsx.game.examples.convoy.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.examples.convoy.components.Conveyor;
import net.mgsx.game.examples.convoy.components.Docked;
import net.mgsx.game.examples.convoy.components.Planet;
import net.mgsx.game.examples.convoy.components.Transit;
import net.mgsx.game.examples.convoy.model.Goods;

public class TransitSystem extends IteratingSystem
{
	@Inject ConvoyHUDSystem game;
	
	public TransitSystem() {
		super(Family.all(Transit.class, Conveyor.class).get(), GamePipeline.RENDER);
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		
		Conveyor conveyor = Conveyor.components.get(entity);
		
		Transit transit = Transit.components.get(entity);
		
		Planet target = Planet.components.get(transit.target);
		
		float distance = transit.origin.dst(target.position);
		
		if(conveyor.oil > 0.5f){
			
			transit.t += deltaTime * transit.speed / distance;
			
			float traveled = deltaTime * transit.speed;
			
			conveyor.oil -= traveled;
			if(conveyor.oil < 0){
				conveyor.oil = 0;
			}
		}else{
			conveyor.oil += deltaTime * 0.05f; // refill from surrounding garbage ? or let burn some goods ...
		}
		
		if(transit.t > 1){
			entity.remove(Transit.class);
			
			Array<Goods> goodsToDeliver = new Array<Goods>();
			for(Goods goods : conveyor.goods){
				if(goods.destination == target){
					goodsToDeliver.add(goods);
				}
			}
			
			for(Goods goods : goodsToDeliver){
				conveyor.goods.removeValue(goods, true);
				game.universe.playerMoney += goods.sellValue;
				game.updatePlayer();
			}
			
			Docked docking = getEngine().createComponent(Docked.class);
			docking.planet = transit.target;
			entity.add(docking);
		}else{
			conveyor.position.set(transit.origin).lerp(target.position, transit.t);
			conveyor.angle = MathUtils.lerpAngleDeg(conveyor.angle, conveyor.position.cpy().sub(target.position).angle(), .01f);
		}
		
	}

}
