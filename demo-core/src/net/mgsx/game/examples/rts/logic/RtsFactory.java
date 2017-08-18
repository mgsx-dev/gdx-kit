package net.mgsx.game.examples.rts.logic;

import java.util.Random;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.examples.rts.components.AIComponent;
import net.mgsx.game.examples.rts.components.BulletComponent;
import net.mgsx.game.examples.rts.components.OrbitComponent;
import net.mgsx.game.examples.rts.components.PlanetComponent;
import net.mgsx.game.examples.rts.components.PlayerComponent;
import net.mgsx.game.examples.rts.components.SunComponent;
import net.mgsx.game.examples.rts.components.TravalComponent;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

public class RtsFactory {

	public static Array<Entity> players = new Array<Entity>();
	public static Color[] colors = {
			Color.GRAY, Color.RED, Color.GREEN, Color.YELLOW, Color.PURPLE
	};
	
	public static Entity createAI(Engine engine)
	{
		Entity entity = engine.createEntity();
		PlayerComponent player = engine.createComponent(PlayerComponent.class);
		AIComponent ai = engine.createComponent(AIComponent.class);
		player.id = players.size;
		players.add(entity);
		entity.add(player);
		entity.add(ai);
		engine.addEntity(entity);
		return entity;
	}
	
	public static Entity createPlayer(Engine engine)
	{
		Entity entity = engine.createEntity();
		PlayerComponent player = engine.createComponent(PlayerComponent.class);
		player.id = players.size;
		players.add(entity);
		entity.add(player);
		engine.addEntity(entity);
		return entity;
	}
	
	public static Entity getPlayer(int playerID) {
		return players.get(playerID);
	}
	
	
	
	public static class GalaxySettings{
		
		/** base planet size is 1 unit radius */
		@Editable
		public float minPlanetSize = .5f, maxPlanetSize = 2;
		
		@Editable
		public long seed;
		
		public Random random = new RandomXS128();
	}
	
	/**
	 * Galaxy is made of stars with planets orbiting around them.
	 * Planets may have some satellites.
	 * This will generate a 3 level hierarchy : suns / planets / satellites
	 */
	public static void createGalaxy(Engine engine, Rectangle zone, GalaxySettings settings)
	{
		// base planet size is one unit radius.
		// sun is twice
		float solarSystemSize = 20;
		boolean rect = false;
		if(rect){
			int ny = (int)(zone.height / (solarSystemSize * 2));
			int nx = (int)(zone.width / (solarSystemSize * 2));
			
			// split rectangle to generate solar system
			float w = zone.width / (float)ny;
			float h = zone.height / (float)nx;
			for(int y=0 ; y<ny ; y++){
				for(int x=0 ; x<nx ; x++){
					createSolarSystem(engine,
							zone.x + (x + .5f) * w, 
							zone.y + (y + .5f) * h, solarSystemSize, settings);
				}
			}
		}else{
			// TODO a better way is to
			// get a circle and split in 7 circles (1 in center and 6 around)
			
			float cx = zone.x + zone.width/2;
			float cy = zone.y + zone.height/2;
			float globalRadius = Math.min(zone.width, zone.height) / 2;
			
			float subRadius = globalRadius / 3;
			
			createSolarSystem(engine, cx, cy, subRadius, settings);
			float phase = MathUtils.random(0, 360);
			for(int i=0 ; i<6 ; i++){
				Vector2 v = new Vector2(Vector2.X).scl(subRadius * 2);
				v.rotate(phase + 360 * i / 6.f);
				createSolarSystem(engine, cx + v.x, cy + v.y, subRadius, settings);
			}
		}
	}
	
	public static void createSolarSystem(Engine engine, float cx, float cy, float radius, GalaxySettings settings)
	{
		float sunRadius = MathUtils.random(.5f, 1) * radius * 0.25f;
		Entity sun = createSun(engine, cx, cy, sunRadius);
		
		createPlanets(engine, sun, sunRadius, radius, settings);
	}
	
	public static void createPlanets(Engine engine, Entity parent, float minOrbit, float maxOrbit, GalaxySettings settings)
	{
		float maxSize = (maxOrbit - minOrbit) / 2;
		if(maxSize < settings.minPlanetSize){
			// cannot create a planet here.
			return;
		}else if(maxSize > settings.maxPlanetSize * 4){ // TODO twice or more ...
			// too huge to create a planet, let divide the place a bit
			float midOrbit =  (maxOrbit + minOrbit) / 2; // + MathUtils.lerp(-maxSize, maxSize, settings.random.nextFloat());
			createPlanets(engine, parent, minOrbit, midOrbit, settings);
			createPlanets(engine, parent, midOrbit, maxOrbit, settings);
			return;
		}
		
		float planetSize = MathUtils.lerp(settings.minPlanetSize, maxSize, settings.random.nextFloat());
		float orbit = MathUtils.lerp(minOrbit + planetSize, maxOrbit - planetSize, settings.random.nextFloat());
		
		Entity planet = createPlanet(engine, parent, orbit, planetSize, settings);
		
		float outerRadius = Math.min(orbit - minOrbit, maxOrbit - orbit);
		
		createPlanets(engine, planet, planetSize, outerRadius, settings); // TODO maybe half outerRdius
	}
	
	public static Entity createPlanet(Engine engine, Entity parent, float distance, float radius, GalaxySettings settings)
	{
		Entity entity = engine.createEntity();
		PlanetComponent planet = engine.createComponent(PlanetComponent.class);
		Transform2DComponent transform = engine.createComponent(Transform2DComponent.class);
		OrbitComponent orbit = engine.createComponent(OrbitComponent.class);
		planet.size = radius;
		planet.population = 0;
		orbit.center = parent;
		orbit.angleDegree = MathUtils.random(0, 360);
		planet.solarDistance = orbit.distance = distance;
		orbit.speed = MathUtils.random(0.5f, 1) * 16f / planet.size; 
		entity.add(transform);
		entity.add(planet);
		entity.add(orbit);
		engine.addEntity(entity);
		return entity;
	}
	
	public static Entity createSun(Engine engine, float cx, float cy, float radius)
	{
		// create sun
		Entity entity = engine.createEntity();
		Transform2DComponent transform = engine.createComponent(Transform2DComponent.class);
		SunComponent sun = engine.createComponent(SunComponent.class);
		transform.position.set(cx, cy);
		sun.size = radius;
		entity.add(transform);
		entity.add(sun);
		engine.addEntity(entity);
		return entity;
	}
	
	public static void createSat(Engine engine, Entity parent, float minOrbit, float maxOrbit, int depth) 
	{
		// split segment in 2
		if(depth <= 0){
			Entity entity = engine.createEntity();
			PlanetComponent planet = engine.createComponent(PlanetComponent.class);
			Transform2DComponent transform = engine.createComponent(Transform2DComponent.class);
			OrbitComponent orbit = engine.createComponent(OrbitComponent.class);
			planet.size = MathUtils.random(.5f, 1) * (maxOrbit - minOrbit) * 0.25f;
			planet.population = 0;
			orbit.center = parent;
			planet.solarDistance = orbit.distance = MathUtils.random(minOrbit + planet.size, maxOrbit - planet.size);
			orbit.speed = MathUtils.random(0.5f, 1) * 2f / planet.size; 
			entity.add(transform);
			entity.add(planet);
			entity.add(orbit);
			engine.addEntity(entity);
		}else{
			createSat(engine, parent, minOrbit, (minOrbit + maxOrbit)/2, depth-1);
			createSat(engine, parent, (minOrbit + maxOrbit)/2, maxOrbit, depth-1);
		}
		
	}

	public static void moveUnits(Engine engine, Entity selectedPlanet, Entity targetPlanet) {
		
		PlanetComponent origin = PlanetComponent.components.get(selectedPlanet);
		Transform2DComponent originTransform = Transform2DComponent.components.get(selectedPlanet);
		int count = (int)origin.population / 2; // Math.min((int)(dist * 4), (int)origin.population);
		float dispertion = .5f;

		Transform2DComponent targetTransform = Transform2DComponent.components.get(targetPlanet);
		
		Vector2 dir = new Vector2(targetTransform.position).sub(originTransform.position);
		float dist = dir.len();
		dir.scl(1.f / dist);

		origin.population -= count;
		while(count > 0)
		{
			int size = (int)MathUtils.log(10, count);
			int crew = (int)Math.pow(10, size);
			crew = Math.max(1, crew);
			count -= crew;
			
			Entity entity = engine.createEntity();
			BulletComponent bullet = engine.createComponent(BulletComponent.class);
			bullet.speed = MathUtils.random(.9f, 1.f) * 10.f; // XXX base speed
			bullet.origin.set(MathUtils.random(-dispertion, dispertion), MathUtils.random(-dispertion, dispertion)).add(originTransform.position);
			bullet.distance = MathUtils.random(-dispertion, dispertion) + dist;
			bullet.direction.set(dir).rotate(MathUtils.random(-10, 10));
			bullet.crew = crew;
			bullet.size = size;
			bullet.color.set(RtsFactory.colors[origin.owner+1]);
//					MathUtils.random(0.2f, 0.5f), 
//					MathUtils.random(0.2f, 0.8f), 
//					MathUtils.random(0.8f, 0.9f), 1);
			entity.add(bullet);
			
			TravalComponent travel = engine.createComponent(TravalComponent.class);
			travel.srcPlanet = selectedPlanet;
			travel.dstPlanet = targetPlanet;
			travel.playerID = origin.owner;
			entity.add(travel);
			bullet.distance = targetTransform.position.dst(bullet.origin);
			bullet.direction.set(targetTransform.position).sub(bullet.origin).nor();
			bullet.position.set(bullet.origin);
		
		
			engine.addEntity(entity);

		}
	}

	
}
