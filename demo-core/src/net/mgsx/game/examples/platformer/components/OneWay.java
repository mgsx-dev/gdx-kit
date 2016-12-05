package net.mgsx.game.examples.platformer.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableComponent;

@EditableComponent
public class OneWay implements Component{
	
	public final static ComponentMapper<OneWay> components = ComponentMapper.getFor(OneWay.class);
	
	@Editable public float angle = 90;
}
