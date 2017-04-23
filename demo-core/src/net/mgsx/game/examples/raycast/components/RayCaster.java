package net.mgsx.game.examples.raycast.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;

@Storable("raycast.caster")
@EditableComponent(autoClone=true)
public class RayCaster implements Component
{
	
	public final static ComponentMapper<RayCaster> components = ComponentMapper.getFor(RayCaster.class);
	
	public static final Pool<Vector2> dotPool = new Pool<Vector2>(){
		@Override
		protected Vector2 newObject() {
			return new Vector2();
		}
	};
	
	public transient Array<Vector2> dots = new Array<Vector2>();

	@Editable
	public float maxLength = 10;
	
	@Editable
	public float range = 180;
	

	public void clear() {
		dotPool.freeAll(dots);
		dots.clear();
	}

	public void add(Vector2 point) {
		dots.add(dotPool.obtain().set(point));
	}
	
	
	
}
