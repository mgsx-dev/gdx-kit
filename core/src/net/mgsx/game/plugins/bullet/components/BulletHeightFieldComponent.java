package net.mgsx.game.plugins.bullet.components;

import java.nio.FloatBuffer;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.utils.Pool.Poolable;

import net.mgsx.game.core.annotations.EditableComponent;

@EditableComponent
public class BulletHeightFieldComponent implements Component, Poolable
{
	
	public final static ComponentMapper<BulletHeightFieldComponent> components = ComponentMapper
			.getFor(BulletHeightFieldComponent.class);
	
	public transient FloatBuffer data;

	@Override
	public void reset() {
		data = null;
	}
}
