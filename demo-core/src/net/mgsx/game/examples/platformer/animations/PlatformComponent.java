package net.mgsx.game.examples.platformer.animations;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;

@Storable("example.platformer.platform")
@EditableComponent(name="Platform")
public class PlatformComponent implements Component
{
	public float time = 0;
	public float speed = 1;
	public Vector3 position = new Vector3();
}
