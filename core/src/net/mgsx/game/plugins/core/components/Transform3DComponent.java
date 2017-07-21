package net.mgsx.game.plugins.core.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool.Poolable;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;
import net.mgsx.game.core.components.Duplicable;

@Storable("3d")
@EditableComponent
public class Transform3DComponent implements Component, Duplicable, Poolable
{
	
	public final static ComponentMapper<Transform3DComponent> components = ComponentMapper
			.getFor(Transform3DComponent.class);
	
	@Editable public Vector3 position = new Vector3();
	@Editable public Quaternion rotation = new Quaternion();
	
	@Override
	public void reset() {
		position.setZero();
		rotation.idt();
		
	}
	@Override
	public Component duplicate(Engine engine) {
		Transform3DComponent clone = engine.createComponent(Transform3DComponent.class);
		clone.position.set(position);
		clone.rotation.set(rotation);
		return clone;
	}
}
