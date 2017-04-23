package net.mgsx.game.examples.raycast.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.examples.raycast.components.RayCaster;
import net.mgsx.game.plugins.box2d.helper.RayCast;
import net.mgsx.game.plugins.box2d.helper.RayCastResult;
import net.mgsx.game.plugins.box2d.systems.Box2DWorldSystem;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

@EditableSystem
public class RayCasterBox2DSystem extends IteratingSystem
{
	@Inject
	public Box2DWorldSystem box2d;
	
	@Editable
	public int maxRays = 360;
	
	private RayCast rayCast = new RayCast();
	
	public RayCasterBox2DSystem() {
		super(Family.all(RayCaster.class, Transform2DComponent.class).get(), GamePipeline.AFTER_PHYSICS);
	}
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		Transform2DComponent transform = Transform2DComponent.components.get(entity);
		
		RayCaster rayCaster = RayCaster.components.get(entity);
		rayCaster.clear(); 
		
		rayCast.start.set(transform.position);
		for(int i=0 ; i<maxRays ; i++){
			float t = 2 * (float)i / (float)maxRays - 1f;
			rayCast.end.x = MathUtils.cosDeg(transform.angle + t * rayCaster.range) * rayCaster.maxLength + rayCast.start.x;
			rayCast.end.y = MathUtils.sinDeg(transform.angle + t * rayCaster.range) * rayCaster.maxLength + rayCast.start.y;
			
			RayCastResult r = box2d.getWorldContext().provider.rayCastFirst(rayCast);
			if(r != null){
				rayCaster.add(r.point);
			}else{
				rayCaster.add(rayCast.end);
			}
		}
		
	}
	
	
	
}
