package net.mgsx.game.plugins.ui;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;

@Storable("ui.slider")
@EditableComponent(autoTool=false, autoClone=true)
public class WidgetComponent implements Component {

	
	public final static ComponentMapper<WidgetComponent> components = ComponentMapper.getFor(WidgetComponent.class);
	
	@Editable
	public String style;
	
	@Editable
	public WidgetFactory factory;
	
	public transient Actor widget;
	
	@Editable
	public Rectangle bounds = new Rectangle();

}
