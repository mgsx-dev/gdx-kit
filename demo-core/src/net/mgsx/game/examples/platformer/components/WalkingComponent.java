package net.mgsx.game.examples.platformer.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.graphics.g3d.model.Animation;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;

@Storable("example.platformer.walking")
@EditableComponent(autoClone=true)
public class WalkingComponent implements Component {

	
	public final static ComponentMapper<WalkingComponent> components = ComponentMapper.getFor(WalkingComponent.class);
	
	@Editable
	public float speedScale = 1; // one world unit per seconds.

	@Editable
	public Animation animation;
}
