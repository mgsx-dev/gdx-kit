package net.mgsx.game.plugins.core.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool.Poolable;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;
import net.mgsx.game.core.components.Duplicable;

@Storable("2d")
@EditableComponent(name="2D Transform")
public class Transform2DComponent implements Component, Duplicable, Poolable
{
	
	public static final ComponentMapper<Transform2DComponent> components = ComponentMapper.getFor(Transform2DComponent.class);
	
	@Editable public Vector2 position = new Vector2();
	
	/* depth in (Z component) : 0 is sprite plan, positive is backround, negative is foreground */
	@Editable public float depth = 0;
	
	@Editable public float angle;
	@Editable public boolean rotation = true;
	@Editable public boolean enabled = true;
	@Editable public Vector2 origin = new Vector2();

	@Editable
	public transient Vector3 normal = new Vector3(0,0,1);
	@Editable
	public transient Vector3 derivative = new Vector3(1,0,0);

	@Override
	public Component duplicate(Engine engine) {
		Transform2DComponent clone = engine.createComponent(Transform2DComponent.class);
		clone.position.set(position);
		clone.angle = angle;
		clone.rotation = rotation;
		clone.enabled = enabled;
		clone.origin.set(origin);
		clone.depth = depth;
		return clone;
	}

	@Override
	public void reset() {
		position.setZero();
		angle = 0;
		rotation = true;
		enabled = true;
		origin.setZero();
		depth = 0;
	}

}
