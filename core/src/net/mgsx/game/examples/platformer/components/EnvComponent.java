package net.mgsx.game.examples.platformer.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;

@Storable("example.platformer.env")
@EditableComponent(name="Env Logic")
public class EnvComponent implements Component {

	@Editable public Vector3 cameraOffset = new Vector3();
	@Editable public boolean enabled = false;
}
