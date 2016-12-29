package net.mgsx.game.examples.platformer.logic;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;

@Storable("cake.secret")
@EditableComponent
public class SecretComponent implements Component
{
	
	public final static ComponentMapper<SecretComponent> components = ComponentMapper.getFor(SecretComponent.class);
	
}
