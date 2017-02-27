package net.mgsx.game.plugins.ui;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableComponent;

// FIXME @Storable("ui.slider")
@EditableComponent(autoTool=false)
public class SliderComponent implements Component {

	
	public final static ComponentMapper<SliderComponent> components = ComponentMapper.getFor(SliderComponent.class);
	
	@Editable
	public String style;
	
	public transient Slider widget;
	
	@Editable
	public Rectangle bounds = new Rectangle();
}
