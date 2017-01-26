package net.mgsx.game.examples.rts.tools;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.math.Vector2;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.tools.RectangleTool;
import net.mgsx.game.examples.rts.components.AIComponent;
import net.mgsx.game.examples.rts.components.PlanetComponent;
import net.mgsx.game.examples.rts.components.PlayerComponent;
import net.mgsx.game.examples.rts.logic.RtsFactory;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

public class CreateAIColonyTool extends RectangleTool
{
	public CreateAIColonyTool(EditorScreen editor) {
		super("RTS - AI Colony", editor);
	}
	
	@Override
	protected void begin(Vector2 startPoint) 
	{
		Entity selectedPlanet = null;
		for(Entity entity : editor.entityEngine.getEntitiesFor(Family.all(PlanetComponent.class, Transform2DComponent.class).get())){
			PlanetComponent planet = entity.getComponent(PlanetComponent.class);
			Transform2DComponent transform = Transform2DComponent.components.get(entity);
			if(startPoint.dst2(transform.position) <= planet.size * planet.size){
				selectedPlanet = entity;
			}
		}
		if(selectedPlanet != null){
			
			PlanetComponent planet = selectedPlanet.getComponent(PlanetComponent.class);
			if(planet.population <= 0 && planet.owner < 0){
				// create AI
				Entity entity = RtsFactory.createAI(getEngine());
				AIComponent ai = AIComponent.components.get(entity);
				PlayerComponent player = PlayerComponent.components.get(entity);
				
				ai.ownedPlanets.add(selectedPlanet);
				
				// aquire planet
				planet.population = 1;
				planet.owner = player.id;
			}
		}
		end();
	}

	@Override
	protected void create(Vector2 startPoint, Vector2 endPoint) {
		// TODO Auto-generated method stub
		
	}


}
