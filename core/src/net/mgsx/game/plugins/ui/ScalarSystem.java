package net.mgsx.game.plugins.ui;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.utils.ObjectMap;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.annotations.EditableSystem;

// TODO maybe use components instead :
// find entity with scalar and "my aspect" and use the scalar value ...
@EditableSystem
public class ScalarSystem extends IteratingSystem
{
	public ScalarSystem() {
		super(Family.all(ScalarComponent.class).get(), GamePipeline.AFTER_INPUT);
	}

	private final ObjectMap<String, Float> map = new ObjectMap<String, Float>();

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		ScalarComponent scalar = ScalarComponent.components.get(entity);
		if(scalar.id != null){
			map.put(scalar.id, scalar.value);
		}
	}

	public float get(String key) {
		Float f = map.get(key);
		return f == null ? 0 : f.floatValue();
	}
	
	
}
