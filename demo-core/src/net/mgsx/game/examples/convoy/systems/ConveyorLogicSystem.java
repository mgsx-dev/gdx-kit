package net.mgsx.game.examples.convoy.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.examples.convoy.components.Conveyor;

public class ConveyorLogicSystem extends IteratingSystem
{
	public ConveyorLogicSystem() {
		super(Family.all(Conveyor.class).get(), GamePipeline.LOGIC);
	}

	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		Conveyor conveyor = Conveyor.components.get(entity);
		
		conveyor.oil = Math.min(conveyor.oil + deltaTime * conveyor.autoOilRate, conveyor.oilMax);
	}

}
