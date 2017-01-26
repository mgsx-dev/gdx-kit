package net.mgsx.game.examples.rts.tools;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.tools.RectangleTool;
import net.mgsx.game.examples.rts.components.PlanetComponent;
import net.mgsx.game.examples.rts.logic.RtsFactory;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

public class MoveToPlanetTool extends RectangleTool
{
	private Entity selectedPlanet = null;
	
	public MoveToPlanetTool(EditorScreen editor) {
		super("RTS - Move", editor);
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
		if(targetPlanet != null)
			RtsFactory.moveUnits(getEngine(), selectedPlanet, targetPlanet);
		
//		Vector2 dir = new Vector2(endPoint).sub(startPoint);
//		float dist = dir.len();
//		dir.scl(1.f / dist);
//		PlanetComponent origin = PlanetComponent.components.get(selectedPlanet);
//		Transform2DComponent originTransform = Transform2DComponent.components.get(selectedPlanet);
//		int count = (int)origin.population / 2; // Math.min((int)(dist * 4), (int)origin.population);
//		float dispertion = .5f;
//		
//		origin.population -= count;
//		for(int i=0 ; i<count ; i++){
//			Entity entity = getEngine().createEntity();
//			BulletComponent bullet = getEngine().createComponent(BulletComponent.class);
//			bullet.speed = MathUtils.random(.9f, 1.f) * 10.f; // XXX base speed
//			bullet.origin.set(MathUtils.random(-dispertion, dispertion), MathUtils.random(-dispertion, dispertion)).add(originTransform.position);
//			bullet.distance = MathUtils.random(-dispertion, dispertion) + dist;
//			bullet.direction.set(dir).rotate(MathUtils.random(-10, 10));
//			bullet.color.set(
//					MathUtils.random(0.2f, 0.5f), 
//					MathUtils.random(0.2f, 0.8f), 
//					MathUtils.random(0.8f, 0.9f), 1);
//			entity.add(bullet);
//			
//			if(targetPlanet == null){
//				// ray cast on planets
//				Entity closestPlanet = null;
//				for(Entity planetEntity : editor.entityEngine.getEntitiesFor(Family.all(PlanetComponent.class, Transform2DComponent.class).get())){
//					if(planetEntity == selectedPlanet) continue;
//					PlanetComponent planet = planetEntity.getComponent(PlanetComponent.class);
//					Transform2DComponent transform = Transform2DComponent.components.get(planetEntity);
//					Vector3 end = new Vector3();
//					if(Intersector.intersectRaySphere(new Ray(new Vector3(bullet.origin, 0), new Vector3(bullet.direction, 0)), new Vector3(transform.position, 0), planet.size, end)){
//						if(closestPlanet == null || end.dst(bullet.origin.x, bullet.origin.y, 0) < bullet.distance){
//							closestPlanet = planetEntity;
//							TravalComponent travel = getEngine().createComponent(TravalComponent.class);
//							travel.srcPlanet = selectedPlanet;
//							travel.dstPlanet = planetEntity;
//							entity.add(travel);
//							bullet.distance = end.dst(bullet.origin.x, bullet.origin.y, 0);
//						}
//					}
//				}
//			}
//			else{
//				Transform2DComponent targetTransform = Transform2DComponent.components.get(targetPlanet);
//				TravalComponent travel = getEngine().createComponent(TravalComponent.class);
//				travel.srcPlanet = selectedPlanet;
//				travel.dstPlanet = targetPlanet;
//				entity.add(travel);
//				bullet.distance = targetTransform.position.dst(bullet.origin);
//				bullet.direction.set(targetTransform.position).sub(bullet.origin).nor();
//				bullet.position.set(bullet.origin);
//			}
//			
//			bullet.position.set(bullet.origin);
//			
//			getEngine().addEntity(entity);
//
//		}
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
