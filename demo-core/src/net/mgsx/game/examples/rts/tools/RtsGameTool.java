package net.mgsx.game.examples.rts.tools;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.tools.RectangleTool;
import net.mgsx.game.examples.rts.components.BulletComponent;
import net.mgsx.game.examples.rts.components.PlanetComponent;
import net.mgsx.game.examples.rts.components.TravalComponent;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

public class RtsGameTool extends RectangleTool
{
	private Entity selectedPlanet = null;
	
	public RtsGameTool(EditorScreen editor) {
		super("RTS", editor);
	}
	
	@Override
	protected void begin(Vector2 startPoint) 
	{
		for(Entity entity : getEngine().getEntitiesFor(Family.all(PlanetComponent.class, Transform2DComponent.class).get())){
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
		Vector2 dir = new Vector2(endPoint).sub(startPoint);
		float dist = dir.len();
		dir.scl(1.f / dist);
		PlanetComponent origin = PlanetComponent.components.get(selectedPlanet);
		Transform2DComponent originTransform = Transform2DComponent.components.get(selectedPlanet);
		int count = Math.min((int)(dist * 4), (int)origin.population);
		float dispertion = .5f;
		
		origin.population -= count;
		for(int i=0 ; i<count ; i++){
			Entity entity = getEngine().createEntity();
			BulletComponent bullet = getEngine().createComponent(BulletComponent.class);
			bullet.speed = MathUtils.random(.9f, 1.f);
			bullet.origin.set(MathUtils.random(-dispertion, dispertion), MathUtils.random(-dispertion, dispertion)).add(originTransform.position);
			bullet.distance = MathUtils.random(-dispertion, dispertion) + dist;
			bullet.direction.set(dir).rotate(MathUtils.random(-10, 10));
			bullet.color.set(
					MathUtils.random(0.2f, 0.5f), 
					MathUtils.random(0.2f, 0.8f), 
					MathUtils.random(0.8f, 0.9f), 1);
			entity.add(bullet);
			
			// ray cast on planets
			Entity closestPlanet = null;
			for(Entity planetEntity : getEngine().getEntitiesFor(Family.all(PlanetComponent.class, Transform2DComponent.class).get())){
				if(planetEntity == selectedPlanet) continue;
				PlanetComponent planet = planetEntity.getComponent(PlanetComponent.class);
				Transform2DComponent transform = Transform2DComponent.components.get(planetEntity);
				Vector3 end = new Vector3();
				if(Intersector.intersectRaySphere(new Ray(new Vector3(bullet.origin, 0), new Vector3(bullet.direction, 0)), new Vector3(transform.position, 0), planet.size, end)){
					if(closestPlanet == null || end.dst(bullet.origin.x, bullet.origin.y, 0) < bullet.distance){
						closestPlanet = planetEntity;
						TravalComponent travel = getEngine().createComponent(TravalComponent.class);
						travel.srcPlanet = selectedPlanet;
						travel.dstPlanet = planetEntity;
						entity.add(travel);
						bullet.distance = end.dst(bullet.origin.x, bullet.origin.y, 0);
					}
				}
			}
			
			
			getEngine().addEntity(entity);

		}
	}
	
	@Override
	public void render(ShapeRenderer renderer) {
		if(startPoint != null && endPoint != null){
			renderer.begin(ShapeType.Line);
			renderer.line(startPoint.x,startPoint.y,endPoint.x,endPoint.y);
			renderer.end();
		}
	}

}
