package net.mgsx.game.examples.td.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;

/**
 * Tower is just the basement of player weapon, can be mobile or fixed.
 * @author mgsx
 *
 */
@Storable("td.tower")
@EditableComponent(autoClone=true)
public class Tower implements Component 
{
	
	public final static ComponentMapper<Tower> components = ComponentMapper.getFor(Tower.class);
}
