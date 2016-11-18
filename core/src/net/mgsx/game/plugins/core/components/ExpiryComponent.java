package net.mgsx.game.plugins.core.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

/**
 * Entity with this component will be removed when time reach zero (countdown)
 * 
 * @author mgsx
 *
 */
public class ExpiryComponent implements Component
{
	
	public final static ComponentMapper<ExpiryComponent> components = ComponentMapper.getFor(ExpiryComponent.class);
	
	public float time;
}
