package net.mgsx.game.examples.rts.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.examples.rts.components.PlanetComponent;

@EditableSystem
public class PlanetSystem extends IteratingSystem
{
	public PlanetSystem() {
		super(Family.all(PlanetComponent.class).get(), GamePipeline.LOGIC);
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		PlanetComponent planet = PlanetComponent.components.get(entity);
		
		// TODO if pop < 1/3 => expand
		// TODO if pop > 2/3 => collapse
		
		float maxResource = planet.size;
		float populationPerResource = 100;
		float rate =  ((planet.population+1) / populationPerResource) / maxResource ;
		planet.maxPopulation = maxResource * populationPerResource;
		if(rate < .25f){
			planet.population += deltaTime * 4;
		}else if(rate > 0.75){
			planet.health = Math.max(0, planet.health - deltaTime / 10);
			// planet.population -= deltaTime * 10;
		}
		planet.health = Math.min(1, planet.health + deltaTime / 20f); // 30 secs
		if(planet.health <= 0.5f)
			planet.population -= deltaTime ;
		
//		// planet produce resources (up to max resources)
//		// max resources depends on its size (planet volume relative to unit sphere)
//		float maxResource = planet.size ;
//		// common regeneration rate : 1 unit planet in ten seconds. 
//		float resourceVelocity = 5.f;
//		planet.resources = Math.min(maxResource, planet.resources + resourceVelocity * maxResource * deltaTime);
//		
//		float populationPerResource = 10;
//		
//		// population need some resources to live (100 population for a unit planet)
//		// then eat the habited planet.
//		float resourcesNeeded = planet.population / populationPerResource;
//		planet.resources -= resourcesNeeded * deltaTime;
//		
//		// planet health is mapped below half resources used.
//		if(planet.resources < 0)
//			planet.health = Math.max(0, planet.health - deltaTime * .1f); // ten secs
//		planet.health = Math.min(1, planet.health + deltaTime / 20f); // 30 secs
//		
//		
//		planet.maxPopulation = maxResource * populationPerResource;
//		
//		// population grows :
//		// - bigger population generate bigger population
//		// - technology influence fertility
//		// - planet health influence fertility
//		float growRate = (planet.population+10) / 10; // * planet.health;
//		if(planet.resources > 0 && planet.health >= 1)
//		planet.population += growRate * deltaTime;
//		
//		// population die (naturally) or :
//		// because of planet health
//		float deathRate = planet.population / 10 * (1 - planet.health);
//		planet.population -= deathRate * deltaTime;
//		if(planet.population  < 0) planet.population  = 0;
//	//	if(planet.population > planet.maxPopulation) planet.population = planet.maxPopulation;
	}
	
	
}
