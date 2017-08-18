package net.mgsx.game.examples.gpu.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;

@EditableComponent
@Storable("gpu.foliage")
public class FoliageComponent implements Component {

	public final static ComponentMapper<FoliageComponent> components = ComponentMapper.getFor(FoliageComponent.class);

	@Editable public boolean asLine = true;
}
