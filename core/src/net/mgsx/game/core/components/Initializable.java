package net.mgsx.game.core.components;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;

public interface Initializable 
{
	// TODO rename ECSAware ? or EntityAwareComponent or EntityAwareComponent and EngineAwareComponent
	public void initialize(Engine manager, Entity entity);
}
