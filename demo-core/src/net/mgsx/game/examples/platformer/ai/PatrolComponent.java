package net.mgsx.game.examples.platformer.ai;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.math.Vector2;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;

@Storable("demo.platformer.patrol")
@EditableComponent(autoClone=true)
public class PatrolComponent implements Component
{
	
	public final static ComponentMapper<PatrolComponent> components = ComponentMapper.getFor(PatrolComponent.class);
	
	@Editable public boolean checkVoid = false;

	@Editable public float horizon = 1;

	@Editable public Vector2 rayStart = new Vector2();

	public float timeout;

	@Editable public float speed = 1;
}
