package net.mgsx.game.examples.platformer.logic;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;

@Storable("example.platformer.life")
@EditableComponent
public class LifeComponent implements Component {
	
	
	public final static ComponentMapper<LifeComponent> components = ComponentMapper.getFor(LifeComponent.class);
	
	public float life;
}
