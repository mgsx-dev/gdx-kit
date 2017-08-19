package net.mgsx.game.examples.openworld.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

import net.mgsx.game.examples.openworld.model.OpenWorldElement;
import net.mgsx.game.examples.openworld.systems.OpenWorldSpawnAnimalSystem.SpawnAnimalChunk;

public class SpawnAnimalComponent implements Component
{
	public static enum State{
		STROLL, FLEE, IDLE, DYING // TODO, APPROACH, ATTACK, DIE
	}
	public static enum Environment{
		WATER, LAND, AIR
	}
	
	public final static ComponentMapper<SpawnAnimalComponent> components = ComponentMapper.getFor(SpawnAnimalComponent.class);
	
	public SpawnAnimalChunk chunk;

	/** whether this spawn element is active (chunk entered) */
	public boolean active;
	
	public OpenWorldElement element;

	/** current animal behavior state  */
	public State state;
	
	/** current animal behavior environment mode */
	public Environment environment;

	public float pathTime;
	
	public float speed;
	
	public int directionBias;
}
