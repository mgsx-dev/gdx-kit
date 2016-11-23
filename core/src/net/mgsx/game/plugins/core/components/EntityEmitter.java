package net.mgsx.game.plugins.core.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.storage.EntityGroup;

@EditableComponent(autoTool=false)
public class EntityEmitter implements Component
{
	public final static ComponentMapper<EntityEmitter> components = ComponentMapper.getFor(EntityEmitter.class);
	
	public EntityGroup template;
	
	@Editable public int max = -1;
	
	@Editable public float wait = 1;

	@Editable public float time;

	@Editable public int current;
	
	@Editable public boolean running;
	

}
