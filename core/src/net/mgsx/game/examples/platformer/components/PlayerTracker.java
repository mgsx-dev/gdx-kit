package net.mgsx.game.examples.platformer.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;

@Storable("example.platformer.player-tracker")
@EditableComponent
public class PlayerTracker implements Component
{
	
	public final static ComponentMapper<PlayerTracker> components = ComponentMapper.getFor(PlayerTracker.class);
	
	@Editable public Vector3 offset = new Vector3(0,0,10); // TODO ...
	@Editable public float smoothness = 1; 
}
