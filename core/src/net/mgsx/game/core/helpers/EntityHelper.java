package net.mgsx.game.core.helpers;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

/**
 * Helper for Ashley extension 
 */
public class EntityHelper 
{
	public static Entity first(Engine engine, Family family){
		ImmutableArray<Entity> elements = engine.getEntitiesFor(family);
		return elements.size() > 0 ? elements.first() : null;
	}
	
}
