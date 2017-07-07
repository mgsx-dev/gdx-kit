package net.mgsx.game.examples.openworld.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.annotations.EnumType;

@EditableSystem
public class OpenWorldEnvSystem extends EntitySystem
{
	@Editable public Color fogColor = new Color(1.0f, 0.8f, 0.7f, 1.0f);
	
	@Editable(type=EnumType.UNIT) public Vector3 sunDirection = new Vector3(.5f, -1f, .5f).nor();
	
	public OpenWorldEnvSystem() {
		super(GamePipeline.LOGIC);
	}
	
	@Override
	public void update(float deltaTime) {
		sunDirection.nor();
	}
	
}
