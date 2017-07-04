package net.mgsx.game.plugins.bullet.components;

import java.nio.FloatBuffer;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

import net.mgsx.game.core.annotations.EditableComponent;

@EditableComponent(autoTool=false, autoClone=false)
public class BulletHeightFieldComponent implements Component, Poolable
{
	public transient FloatBuffer data;

	@Override
	public void reset() {
		data = null;
	}
}
