package net.mgsx.game.examples.platformer.logic;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.math.Vector2;

import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;

@Storable("example.platformer.ai")
@EditableComponent
public class PlatformerAI implements Component {
	
	public final static ComponentMapper<PlatformerAI> components = ComponentMapper.getFor(PlatformerAI.class);
	
	public Vector2 initialPosition = new Vector2();
	public Vector2 direction = new Vector2();
	public float walkTime;
}
