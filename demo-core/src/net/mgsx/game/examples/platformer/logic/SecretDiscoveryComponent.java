package net.mgsx.game.examples.platformer.logic;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.math.Vector2;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;

@Storable("cake.secret.discovery")
@EditableComponent(autoClone=true)
public class SecretDiscoveryComponent implements Component {

	
	public final static ComponentMapper<SecretDiscoveryComponent> components = ComponentMapper
			.getFor(SecretDiscoveryComponent.class);
	
	@Editable
	public float radius = 1;
	
	@Editable
	public Vector2 offset = new Vector2();
}
