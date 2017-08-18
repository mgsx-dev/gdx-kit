package net.mgsx.game.examples.rts.tools;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.tools.RectangleTool;
import net.mgsx.game.examples.rts.components.PlanetComponent;
import net.mgsx.game.examples.rts.components.RoadComponent;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

public class CreateRoadTool extends RectangleTool
{
	private Entity selectedPlanet = null;
	private ImmutableArray<Entity> roads;
	
	public CreateRoadTool(EditorScreen editor) {
		super("RTS - Road", editor);
		roads = getEngine().getEntitiesFor(Family.all(RoadComponent.class).get());
	}
	
	@Override
	protected void begin(Vector2 startPoint) 
	{
		for(Entity entity : editor.entityEngine.getEntitiesFor(Family.all(PlanetComponent.class, Transform2DComponent.class).get())){
			PlanetComponent planet = entity.getComponent(PlanetComponent.class);
			Transform2DComponent transform = Transform2DComponent.components.get(entity);
			if(startPoint.dst2(transform.position) <= planet.size * planet.size){
				selectedPlanet = entity;
			}
		}
		if(selectedPlanet == null){
			this.startPoint = null;
		}
	}

	@Override
	protected void create(Vector2 startPoint, Vector2 endPoint) 
	{
		Entity targetPlanet = null;
		for(Entity entity : editor.entityEngine.getEntitiesFor(Family.all(PlanetComponent.class, Transform2DComponent.class).get())){
			PlanetComponent planet = entity.getComponent(PlanetComponent.class);
			Transform2DComponent transform = Transform2DComponent.components.get(entity);
			if(endPoint.dst2(transform.position) <= planet.size * planet.size){
				targetPlanet = entity;
			}
		}
		if(targetPlanet == null) return;
		
		for(Entity e : roads){
			RoadComponent r = RoadComponent.components.get(e);
			if(r.srcPlanet == selectedPlanet && r.dstPlanet == targetPlanet || 
					r.dstPlanet == selectedPlanet && r.srcPlanet == targetPlanet){
				getEngine().removeEntity(e);
				return;
			}
		}
		
		Entity entity = getEngine().createEntity();
		
		RoadComponent road = getEngine().createComponent(RoadComponent.class);
		road.srcPlanet = selectedPlanet;
		road.dstPlanet = targetPlanet;
		entity.add(road);
		getEngine().addEntity(entity);

	}
	
	@Override
	public void render(ShapeRenderer renderer) {
		if(startPoint != null && endPoint != null){
			if(selectedPlanet != null){
				Transform2DComponent transform = Transform2DComponent.components.get(selectedPlanet);
				startPoint.set(transform.position);
			}
			renderer.begin(ShapeType.Line);
			renderer.line(startPoint.x,startPoint.y,endPoint.x,endPoint.y);
			renderer.end();
		}
	}

}
