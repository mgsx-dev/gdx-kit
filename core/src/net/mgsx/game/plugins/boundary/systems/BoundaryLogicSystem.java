package net.mgsx.game.plugins.boundary.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.components.BoundaryComponent;
import net.mgsx.game.core.components.LogicComponent;

/**
 * abstract system for component inherit from LogicComponent
 * @author mgsx
 *
 * @param <T>
 */
public class BoundaryLogicSystem<T extends LogicComponent> extends IteratingSystem
{
	private Class<T> type;
	public BoundaryLogicSystem(Class<T> type) 
	{
		super(Family.all(BoundaryComponent.class, type).get(), GamePipeline.BEFORE_LOGIC);
		this.type = type;
	}
	@Override
	protected void processEntity(Entity entity, float deltaTime) 
	{
		BoundaryComponent boundary = BoundaryComponent.components.get(entity);
		LogicComponent logic = entity.getComponent(type);
		if(boundary.justInside){
			if(logic.behavior != null) logic.behavior.enter();
		}
		else if(boundary.justOutside){
			if(logic.behavior != null) logic.behavior.exit();
		}
		if(boundary.inside){
			if(logic.behavior != null) logic.behavior.update(deltaTime);
		}
	}
	
	public static <T extends LogicComponent> BoundaryLogicSystem<T> create(Class<T> type) {
		return new BoundaryLogicSystem<T>(type);
	}

}
