package net.mgsx.game.examples.openworld.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

import net.mgsx.game.examples.openworld.systems.OpenWorldSpawnSystem.SpawnChunk;

public class SpawnComponent implements Component
{
	
	public final static ComponentMapper<SpawnComponent> components = ComponentMapper.getFor(SpawnComponent.class);
	
	public SpawnChunk chunk;

	/** whether this spawn element is active (chunk entered) */
	public boolean active;
}
