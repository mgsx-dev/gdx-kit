package net.mgsx.game.examples.openworld.tools;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.collision.Ray;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.core.tools.Tool;
import net.mgsx.game.examples.openworld.model.OpenWorldElement;
import net.mgsx.game.examples.openworld.model.OpenWorldElement.GeometryType;
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
	
	@Editable public boolean forceShape = false;
	@Editable public float sx = 1;
	@Editable public float sy = 1;
	@Editable public float sz = 1;
	@Editable public GeometryType type = GeometryType.BOX;
	
	public AddElementTool(EditorScreen editor) {
		super("Add Element", editor);
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		
		Ray ray = camera().getPickRay(screenX, screenY);
		ray.direction.scl(camera().far);
		Ray rayResult = new Ray();
		Entity entity = bulletWorld.rayCast(ray, rayResult);
		if(entity != null){
			
			RandomXS128 rnd = new RandomXS128();
			OpenWorldElement e = OpenWorldModel.generateNewElement(rnd.nextLong());
			
			if(forceShape){
				e.geo_x = sx;
				e.geo_y = sy;
				e.size = sz;
				e.type = type.toString().toLowerCase(); // XXX TODO list factory for editable selector
			}
			
			e.dynamic = dynamic;
			
			e.size *= size;
			
			e.position.set(rayResult.origin);
			e.rotation.idt();
			
			userObjectSystem.appendObject(e);
			
			return true;
		}
		return false;
	}
	
}
