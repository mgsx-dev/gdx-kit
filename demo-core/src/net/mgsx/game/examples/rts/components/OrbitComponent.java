package net.mgsx.game.examples.rts.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableComponent;

@EditableComponent
public class OrbitComponent implements Component
{
	
	public final static ComponentMapper<OrbitComponent> components = ComponentMapper.getFor(OrbitComponent.class);
	
	public Entity center;
	@Editable public float speed = 50;
	@Editable public float angleDegree;

	@Editable public float distance = 4;
}
