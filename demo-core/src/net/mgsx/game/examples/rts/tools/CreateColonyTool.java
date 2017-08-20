package net.mgsx.game.examples.rts.tools;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.math.Vector2;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.tools.RectangleTool;
import net.mgsx.game.examples.rts.components.PlanetComponent;
import net.mgsx.game.examples.rts.components.PlayerComponent;
import net.mgsx.game.examples.rts.logic.RtsFactory;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

public class CreateColonyTool extends RectangleTool
{
	public CreateColonyTool(EditorScreen editor) {
		super("RTS - Colony", editor);
	}
	
	@Override
	protected void begin(Vector2 startPoint) 
	{
		Entity selectedPlanet = null;
		for(Entity entity : getEngine().getEntitiesFor(Family.all(PlanetComponent.class, Transform2DComponent.class).get())){
			PlanetComponent planet = entity.getComponent(PlanetComponent.class);
			Transform2DComponent transform = Transform2DComponent.components.get(entity);
			if(startPoint.dst2(transform.position) <= planet.size * planet.size){
				selectedPlanet = entity;
			}
		}
		if(selectedPlanet != null){
			Entity e = RtsFactory.createPlayer(getEngine());
			PlayerComponent player = PlayerComponent.components.get(e);
			PlanetComponent planet = selectedPlanet.getComponent(PlanetComponent.class);
			if(planet.population <= 0){
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
