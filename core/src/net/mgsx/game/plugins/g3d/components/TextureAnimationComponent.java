package net.mgsx.game.plugins.g3d.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;
import net.mgsx.game.core.components.Duplicable;

@Storable("g3d.texAnim")
@EditableComponent(all=G3DModel.class)
public class TextureAnimationComponent implements Component, Duplicable
{
	public float time;
	
	@Editable public float uOffset, vOffset;
	@Editable public float uPerSec = 0;
	@Editable public float vPerSec = 0;
	@Override
	public Component duplicate(Engine engine) {
		TextureAnimationComponent clone = engine.createComponent(TextureAnimationComponent.class);
		clone.uOffset = uOffset;
		clone.vOffset = vOffset;
		clone.uPerSec = uPerSec;
		clone.vPerSec = vPerSec;
		return clone;
	}
}
