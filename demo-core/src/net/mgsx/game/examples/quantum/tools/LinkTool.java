package net.mgsx.game.examples.quantum.tools;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.tools.RectangleTool;
import net.mgsx.game.examples.quantum.components.Link;
import net.mgsx.game.examples.quantum.components.Planet;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

public class LinkTool extends RectangleTool
{
	Entity selectedPlanet;
	public LinkTool(EditorScreen editor) {
		super("Link Planet", editor);
	}
	
	@Override
	protected void begin(Vector2 startPoint) {
		super.begin(startPoint);
		
		selectedPlanet = null;
		for(Entity entity : getEngine().getEntitiesFor(Family.all(Transform2DComponent.class, Planet.class).get())){
			Transform2DComponent transform = Transform2DComponent.components.get(entity);
			Planet planet = Planet.components.get(entity);
			if(transform.position.dst(startPoint) <= planet.radius){
				selectedPlanet = entity;
				return;
			}
		}
	}

	@Override
	protected void create(Vector2 startPoint, Vector2 endPoint) 
	{
		Entity targetPlanet = null;
		for(Entity entity : getEngine().getEntitiesFor(Family.all(Transform2DComponent.class, Planet.class).get())){
			Transform2DComponent transform = Transform2DComponent.components.get(entity);
			Planet planet = Planet.components.get(entity);
			if(transform.position.dst(endPoint) <= planet.radius){
				targetPlanet = entity;
				break;
			}
		}
		if(targetPlanet != null){
			
			// find link
			boolean found = false;
			for(Entity entity : getEngine().getEntitiesFor(Family.all(Link.class).get())){
				Link link = Link.components.get(entity);
				if(link.source == selectedPlanet && link.target == targetPlanet || link.source == targetPlanet && link.target == selectedPlanet){
					getEngine().removeEntity(entity);
					found = true;
				}
			}
			if(found) return;
			
			Entity entity = getEngine().createEntity();
			Link link = getEngine().createComponent(Link.class);
			link.source = selectedPlanet;
			link.target = targetPlanet;
			entity.add(link);
			getEngine().addEntity(entity);
			
			Entity entity2 = getEngine().createEntity();
			Link link2 = getEngine().createComponent(Link.class);
			link2.source = targetPlanet;
			link2.target = selectedPlanet;
			entity2.add(link2);
			getEngine().addEntity(entity2);
			
			
		}
	}
	
	@Override
	public void render(ShapeRenderer renderer) {
		if(startPoint != null && endPoint != null){
			renderer.begin(ShapeType.Line);
			renderer.line(startPoint, endPoint);
			renderer.end();
		}
	}

}
