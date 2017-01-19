package net.mgsx.game.plugins.g3d.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.GameScreen;
import net.mgsx.game.core.components.Hidden;
import net.mgsx.game.plugins.g3d.components.PointLightComponent;

public class G3DLightCullingSystem extends IteratingSystem
{
	private GameScreen game;
	
	public G3DLightCullingSystem(GameScreen game) {
		super(Family.all(PointLightComponent.class).get(), GamePipeline.CULLING);
		this.game = game;
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) 
	{
		PointLightComponent light = PointLightComponent.components.get(entity);
		boolean visible = game.camera.frustum.sphereInFrustum(light.light.position, light.light.intensity);
		if(Hidden.components.has(entity)){
			if(visible) entity.remove(Hidden.class);
		}else{
			if(!visible) entity.add(getEngine().createComponent(Hidden.class));
		}
	}
}
