package net.mgsx.game.examples.platformer.ai;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.math.Vector2;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;
import net.mgsx.game.core.storage.EntityGroupRef;

@Storable("cake.shoot")
@EditableComponent(autoClone=true)
public class MortarComponent implements Component{

	
	public final static ComponentMapper<MortarComponent> components = ComponentMapper.getFor(MortarComponent.class);
	
	@Editable
	public String shootAnimation;
	
	@Editable
	public float angle = 0;
	
	@Editable
	public float speed = 4;
	
	@Editable
	public EntityGroupRef projectile;
	
	@Editable
	public float reloadTime = 4;

	public float time;
	@Editable
	public float distance = 10;
	@Editable
	public float expiry = 4;
	@Editable
	public Vector2 offset = new Vector2();
	
	/** dynamic target */
	public Vector2 target = new Vector2();
}
