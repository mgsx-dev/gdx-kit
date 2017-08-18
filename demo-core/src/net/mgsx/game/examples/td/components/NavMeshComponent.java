package net.mgsx.game.examples.td.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;

@Storable("td.navMesh")
@EditableComponent
public class NavMeshComponent implements Component
{
	
	public final static ComponentMapper<NavMeshComponent> components = ComponentMapper.getFor(NavMeshComponent.class);
}
