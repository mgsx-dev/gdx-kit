package net.mgsx.game.plugins.parallax;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;

@Storable("parallax")
@EditableComponent
public class ParallaxModel implements Component
{
	
	public static ComponentMapper<ParallaxModel> components = ComponentMapper.getFor(ParallaxModel.class);
	
	@Editable public float rateX = 1, rateY = 1;
	
	public Vector3 cameraOrigin = new Vector3();
	public Vector3 objectOrigin = new Vector3();
}
