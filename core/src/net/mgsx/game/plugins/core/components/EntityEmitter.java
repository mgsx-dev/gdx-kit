package net.mgsx.game.plugins.core.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableComponent;

@EditableComponent(autoTool=false)
public class EntityEmitter implements Component
{
	public final static ComponentMapper<EntityEmitter> components = ComponentMapper.getFor(EntityEmitter.class);
	
	// TODO reference file ... (entity group or patch file !)
	public Array<Entity> template;
	
	@Editable public int max = -1;
	
	@Editable public float wait = 1;

	@Editable public float time;

	@Editable public int current;

}
