package net.mgsx.game.plugins.core.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;

@Storable("3d")
@EditableComponent
public class Transform3DComponent implements Component
{
	
	public final static ComponentMapper<Transform3DComponent> components = ComponentMapper
			.getFor(Transform3DComponent.class);
	
	@Editable public Vector3 position = new Vector3();
	@Editable public Quaternion rotation = new Quaternion();
}
