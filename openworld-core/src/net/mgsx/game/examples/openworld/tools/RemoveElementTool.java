package net.mgsx.game.examples.openworld.tools;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.collision.Ray;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.core.tools.Tool;
import net.mgsx.game.examples.openworld.components.ObjectMeshComponent;
import net.mgsx.game.examples.openworld.systems.UserObjectSystem;
import net.mgsx.game.plugins.bullet.system.BulletWorldSystem;

@Editable
public class RemoveElementTool extends Tool
{

	@Inject BulletWorldSystem bulletWorld;
	@Inject UserObjectSystem userObject;
	
	public RemoveElementTool(EditorScreen editor) {
		super("Remove Element", editor);
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		
		Ray ray = camera().getPickRay(screenX, screenY);
		ray.direction.scl(camera().far);
		Ray rayResult = new Ray();
		Entity entity = bulletWorld.rayCast(ray, rayResult);
		if(entity != null){
			
			ObjectMeshComponent omc = ObjectMeshComponent.components.get(entity);
			if(omc != null){
				if(omc.userObject != null)
					userObject.removeElement(omc.userObject);
				else
					getEngine().removeEntity(entity);
			}
			return true;
		}
		return false;
	}

}
