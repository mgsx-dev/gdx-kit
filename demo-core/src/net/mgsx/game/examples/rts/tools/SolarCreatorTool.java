package net.mgsx.game.examples.rts.tools;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.tools.RectangleTool;
import net.mgsx.game.examples.rts.components.OrbitComponent;
import net.mgsx.game.examples.rts.components.PlanetComponent;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

public class SolarCreatorTool extends RectangleTool
{

	
	public SolarCreatorTool(EditorScreen editor) {
		super("Planet Solar", editor);
	}

	@Override
	protected void create(Vector2 startPoint, Vector2 endPoint) 
	{
		// scattering ...
		
		float x = Math.min(startPoint.x, endPoint.x);
		float y = Math.min(startPoint.y, endPoint.y);
		float w = Math.max(0, Math.abs(startPoint.x - endPoint.x));
		float h = Math.max(0, Math.abs(startPoint.y - endPoint.y));
		
		float cx = x + w/2;
		float cy = y + h/2;
		float r = Math.min(w, h) / 2;
		
		// create sun
		Entity entity = getEngine().createEntity();
		PlanetComponent planet = getEngine().createComponent(PlanetComponent.class);
		Transform2DComponent transform = getEngine().createComponent(Transform2DComponent.class);
		transform.position.set(cx, cy);
		planet.population = MathUtils.random(0, 100);
		planet.health = MathUtils.random();
		planet.size = MathUtils.random(.5f, 1) * r * 0.25f;
		entity.add(transform);
		entity.add(planet);
		getEngine().addEntity(entity);
		
		// create some sattelites
		createSat(entity, cx, cy, planet.size, r, 2);
		
		
	}
	
	private void createSat(Entity parent, float cx, float cy, float minOrbit, float maxOrbit, int depth) 
	{
		// split segment in 2
		if(depth <= 0){
			Entity entity = getEngine().createEntity();
			PlanetComponent planet = getEngine().createComponent(PlanetComponent.class);
			Transform2DComponent transform = getEngine().createComponent(Transform2DComponent.class);
			OrbitComponent orbit = getEngine().createComponent(OrbitComponent.class);
			planet.size = MathUtils.random(.5f, 1) * (maxOrbit - minOrbit) * 0.25f;
			planet.population = 0;
			orbit.center = parent;
			planet.solarDistance = orbit.distance = MathUtils.random(minOrbit + planet.size, maxOrbit - planet.size);
			orbit.speed = MathUtils.random(0.5f, 1) * 2f / planet.size; 
			entity.add(transform);
			entity.add(planet);
			entity.add(orbit);
			getEngine().addEntity(entity);
		}else{
			createSat(parent, cx, cy, minOrbit, (minOrbit + maxOrbit)/2, depth-1);
			createSat(parent, cx, cy, (minOrbit + maxOrbit)/2, maxOrbit, depth-1);
		}
		
	}


}
