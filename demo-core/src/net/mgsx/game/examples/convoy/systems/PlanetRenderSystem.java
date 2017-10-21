package net.mgsx.game.examples.convoy.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.examples.convoy.components.Planet;
import net.mgsx.game.examples.convoy.components.Selected;
import net.mgsx.game.examples.convoy.model.MaterialType;
import net.mgsx.game.plugins.camera.model.POVModel;

public class PlanetRenderSystem extends IteratingSystem
{
	@Inject POVModel pov;
	
	private ShapeRenderer renderer;

	public PlanetRenderSystem() {
		super(Family.all(Planet.class).get(), GamePipeline.RENDER);
		renderer = new ShapeRenderer();
	}

	@Override
	public void update(float deltaTime) {
		renderer.setProjectionMatrix(pov.camera.combined);
		renderer.begin(ShapeType.Filled);
		super.update(deltaTime);
		renderer.end();
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		Planet planet = Planet.components.get(entity);
		
		float radius = planet.radius;
		
		renderer.setColor(Selected.components.has(entity) ? new Color(.2f, .5f, 1.f, 1.f) : new Color(.2f, .2f, 1.f, 1.f));
		renderer.circle(planet.position.x, planet.position.y, radius, 12);
		
		int matCount = 0;
		for(MaterialType type : MaterialType.ALL){
			int stock = planet.materials.get(type).stock;
			if(stock != 0){
				
				float matRadius = Math.abs(stock) / 10f;
				
				renderer.setColor(type.color);
				// renderer.getColor().mul(Math.abs(balance));
				renderer.circle(planet.position.x + MathUtils.cos(matCount * 30) * radius, planet.position.y + MathUtils.sin(matCount * 30) * radius, matRadius, 6);
				
				if(stock < 0){
					renderer.setColor(type.color);
					renderer.getColor().mul(.2f);
					renderer.circle(planet.position.x + MathUtils.cos(matCount * 30) * radius, planet.position.y + MathUtils.sin(matCount * 30) * radius, matRadius * .8f, 6);
				}
			}
			
			matCount ++;
		}
		
		if(planet.gazAvailability > 0){
			renderer.setColor(Color.PURPLE);
			renderer.circle(planet.position.x + MathUtils.cos(matCount * 30) * radius, planet.position.y + MathUtils.sin(matCount * 30) * radius * 1.5f, .5f * planet.gazAvailability, 12);
			
			
			
		}
		
//		if(Selected.components.has(entity)){
//			renderer.setColor(Color.BLACK);
//			renderer.getColor().a = .1f;
//			renderer.circle(planet.position.x, planet.position.y, planet.radius * 1.1f, 12);
//		}
	}

}
