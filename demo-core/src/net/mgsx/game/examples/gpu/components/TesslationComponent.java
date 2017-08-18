package net.mgsx.game.examples.gpu.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.math.Matrix4;

import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;

@EditableComponent
@Storable("gpu.tess")
public class TesslationComponent implements Component
{
	
	public final static ComponentMapper<TesslationComponent> components = ComponentMapper
			.getFor(TesslationComponent.class);
	
	transient public Mesh mesh;

	public Matrix4 transform;

}
