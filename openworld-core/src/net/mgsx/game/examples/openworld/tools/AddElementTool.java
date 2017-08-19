package net.mgsx.game.examples.openworld.tools;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.collision.Ray;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.core.tools.Tool;
import net.mgsx.game.examples.openworld.editors.OpenWorldTypeSelector;
import net.mgsx.game.examples.openworld.model.OpenWorldElement;
import net.mgsx.game.examples.openworld.model.OpenWorldModel;
import net.mgsx.game.examples.openworld.systems.UserObjectSystem;
import net.mgsx.game.plugins.bullet.system.BulletWorldSystem;

@Editable
public class AddElementTool extends Tool
{

	@Inject BulletWorldSystem bulletWorld;
	@Inject UserObjectSystem userObjectSystem;
	@Editable public boolean dynamic;
	@Editable public float size = 1;
	
	@Editable(editor=OpenWorldTypeSelector.class)
	public String type;
	
	public AddElementTool(EditorScreen editor) {
		super("Add Element", editor);
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		
		if(button != Input.Buttons.LEFT) return false;
		
		Ray ray = camera().getPickRay(screenX, screenY);
		ray.direction.scl(camera().far);
		Ray rayResult = new Ray();
		Entity entity = bulletWorld.rayCast(ray, rayResult);
		if(entity != null){
			
			OpenWorldElement e = OpenWorldModel.generateNewElement(type);
			
			e.position.set(rayResult.origin);
			e.rotation.idt();
			
			userObjectSystem.appendObject(e);
			
			return true;
		}
		return false;
	}
	
}
