package net.mgsx.game.plugins.g3d.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.components.Hidden;
import net.mgsx.game.plugins.g3d.components.PointLightComponent;

@EditableSystem
public class G3DProfilerSystem extends EntitySystem
{
	@Editable(realtime=true, readonly=true)
	public int activePointLights, totalPointLights;
	
	private ImmutableArray<Entity> visiblePointLights;
	private ImmutableArray<Entity> allPointLights;

	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		visiblePointLights = engine.getEntitiesFor(Family.all(PointLightComponent.class).exclude(Hidden.class).get());
		allPointLights = engine.getEntitiesFor(Family.all(PointLightComponent.class).get());
	}
	
	@Override
	public void update(float deltaTime) 
	{
		activePointLights = visiblePointLights.size();
		totalPointLights = allPointLights.size();
	}
}
