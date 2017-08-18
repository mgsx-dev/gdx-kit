package net.mgsx.game.examples.gpu.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.math.Matrix4;

import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;

@Storable("gpu.spike")
@EditableComponent
public class SpikeComponent implements Component
{
	
	public final static ComponentMapper<SpikeComponent> components = ComponentMapper.getFor(SpikeComponent.class);
	
	transient public Mesh mesh;

	public Matrix4 transform;
}
