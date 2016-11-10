package net.mgsx.game.core.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.math.Vector2;

import net.mgsx.game.core.annotations.EditableComponent;

@EditableComponent(name="2D Transform")
public class Transform2DComponent implements Component, Duplicable, OverrideProxy
{
	
	public static ComponentMapper<Transform2DComponent> components = ComponentMapper.getFor(Transform2DComponent.class);
	
	public Vector2 position = new Vector2();
	public float angle;
	public boolean rotation = true;
	public boolean enabled = true;
	public Vector2 origin = new Vector2();
	
	@Override
	public Component duplicate() {
		Transform2DComponent clone = new Transform2DComponent();
		clone.position.set(position);
		clone.angle = angle;
		return clone;
	}
}
