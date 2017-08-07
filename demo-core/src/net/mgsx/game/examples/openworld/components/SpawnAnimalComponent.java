package net.mgsx.game.examples.openworld.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

import net.mgsx.game.examples.openworld.model.OpenWorldElement;
import net.mgsx.game.examples.openworld.systems.OpenWorldSpawnAnimalSystem.SpawnAnimalChunk;

public class SpawnAnimalComponent implements Component
{
	public static enum State{
		IDLE, FLEE, APPROACH, ATTACK, DIE, STROLL
	}
	
	public final static ComponentMapper<SpawnAnimalComponent> components = ComponentMapper.getFor(SpawnAnimalComponent.class);
	
	public SpawnAnimalChunk chunk;

	/** whether this spawn element is active (chunk entered) */
	public boolean active;
	
	public OpenWorldElement element;

	public State state;

	public float pathTime;
	
	public float speed;
	
	public int directionBias;
}
