package net.mgsx.game.examples.raycast.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;

@Storable("raycast.audio.source")
@EditableComponent(autoClone=true)
public class AudioSource implements Component
{
	
	public final static ComponentMapper<AudioSource> components = ComponentMapper.getFor(AudioSource.class);
	
	@Editable
	public int id;

	@Editable
	public float intensity = 1;
}
