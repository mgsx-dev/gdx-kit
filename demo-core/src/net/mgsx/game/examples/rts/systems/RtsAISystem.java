package net.mgsx.game.examples.rts.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.examples.rts.components.AIComponent;
import net.mgsx.game.examples.rts.components.PlanetComponent;
import net.mgsx.game.examples.rts.components.PlayerComponent;
import net.mgsx.game.examples.rts.logic.RtsFactory;

public class RtsAISystem extends IteratingSystem
{
	private ImmutableArray<Entity> allPlanets;
	
	private Array<Entity> bestSources = new Array<Entity>();
	private Array<Entity> bestTargets = new Array<Entity>();
	
	public RtsAISystem() {
		super(Family.all(AIComponent.class).get(), GamePipeline.LOGIC);
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		allPlanets = engine.getEntitiesFor(Family.all(PlanetComponent.class).get());
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		AIComponent ai = AIComponent.components.get(entity);
		ai.timeout -= deltaTime;
		if(ai.timeout <= 0){
			ai.timeout += 2; // 2 seconds
		}else{
			return;
		}
		
		PlayerComponent player = PlayerComponent.components.get(entity);
		
		// TODO analyse surrounding and make decision.
		bestSources.clear();
		for(Entity e : ai.ownedPlanets){
			PlanetComponent planet = PlanetComponent.components.get(e);
			
			if(planet.population > 20){
				bestSources.add(e);
			}
		}
		bestTargets.clear();
		for(Entity e2 : allPlanets){
			PlanetComponent planet2 = PlanetComponent.components.get(e2);
			if(planet2.owner != player.id){
				bestTargets.add(e2);
			}
		}
		
		if(bestSources.size > 0 && bestTargets.size > 0){
			Entity s = bestSources.get(MathUtils.random(bestSources.size-1));
			Entity t = bestTargets.get(MathUtils.random(bestTargets.size-1));
			RtsFactory.moveUnits(getEngine(), s, t);
		}
		
	}
}
