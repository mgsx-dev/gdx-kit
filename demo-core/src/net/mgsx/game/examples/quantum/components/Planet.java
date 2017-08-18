package net.mgsx.game.examples.quantum.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;

@Storable("quantum.planet")
@EditableComponent(autoClone=true)
public class Planet implements Component
{
	
	public final static ComponentMapper<Planet> components = ComponentMapper.getFor(Planet.class);
	
	public int id = 0;
	
	public int owner = -1;
	@Editable public float radius = 100;
	@Editable public float strength = 1;
	@Editable public float health = 1;
	@Editable public float speed = 1;
	@Editable public int resources = 50;
	@Editable public int full_resources = 50;
	@Editable public boolean is_start_planet = false;
	public int chain_planet = -1;
	public int last_idx = 0;
	public boolean is_regrowing = false;
	
	
}
