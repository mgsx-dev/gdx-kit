package net.mgsx.game.examples.rts.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;

public class AIComponent implements Component{

	
	public final static ComponentMapper<AIComponent> components = ComponentMapper.getFor(AIComponent.class);
	
	public float timeout;
	
	public Array<Entity> ownedPlanets = new Array<Entity>();

}
