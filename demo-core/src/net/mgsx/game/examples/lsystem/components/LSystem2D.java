package net.mgsx.game.examples.lsystem.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableComponent;

@EditableComponent
public class LSystem2D implements Component
{
	
	public final static ComponentMapper<LSystem2D> components = ComponentMapper.getFor(LSystem2D.class);
	
	@Editable public String rules = "F[+F]F[-F]F";
	@Editable public float angle = 30;
	@Editable public float scale = 1.f / 3.f;
	@Editable public float size = 1;
	@Editable public float depth = 3;
	@Editable public boolean normalized = true;
	
}
