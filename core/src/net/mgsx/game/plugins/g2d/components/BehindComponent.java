package net.mgsx.game.plugins.g2d.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.math.Vector2;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;

@Storable("g2d.behind")
@EditableComponent(autoClone=true)
public class BehindComponent implements Component
{
	
	public final static ComponentMapper<BehindComponent> components = ComponentMapper.getFor(BehindComponent.class);
	
	@Editable
	public Vector2 parallax = new Vector2();
	@Editable
	public Vector2 offset = new Vector2();
}
