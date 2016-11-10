package net.mgsx.game.plugins.g3d.components;

import com.badlogic.ashley.core.Component;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableComponent;

@EditableComponent(all=G3DModel.class)
public class TextureAnimationComponent implements Component
{
	public float time;
	
	@Editable public float uOffset, vOffset;
	@Editable public float uPerSec = 0;
	@Editable public float vPerSec = 0;
}
