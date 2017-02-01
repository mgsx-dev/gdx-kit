package net.mgsx.game.examples.td.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.utils.Pool.Poolable;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;

/**
 * Lazer is a kind of weapon that needs to be filled and then empty energy to targets.
 * @author mgsx
 *
 */
@Storable("td.lazer")
@EditableComponent(autoClone=true)
public class Lazer implements Component, Poolable
{
	
	public final static ComponentMapper<Lazer> components = ComponentMapper.getFor(Lazer.class);
	
	/** max amount of energy */
	@Editable public float chargeMax;
	/** min amount of energy in order to be released on enemies */
	@Editable public float chargeMin;
	
	@Editable public float emptySpeed;
	@Editable public float fillSpeed;
	
	/** lazer state : when active it releasing energy, when not it charging */
	public transient boolean active;
	
	public transient float charge;

	@Override
	public void reset() {
		charge = 0;
		active = false;
	}
	
}
