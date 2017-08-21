package net.mgsx.game.examples.openworld.tools;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.core.tools.Tool;
import net.mgsx.game.plugins.bullet.system.BulletWorldSystem;
import net.mgsx.game.plugins.g3d.components.G3DModel;

public class AlignMeshTool extends Tool
{

	@Inject BulletWorldSystem bulletWorld;
	@Editable public float angle;
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		
		Ray ray = camera().getPickRay(screenX, screenY);
		ray.direction.scl(camera().far);
		Ray rayResult = new Ray();
		Entity entity = bulletWorld.rayCast(ray, rayResult);
		if(entity != null){
			
			G3DModel model = G3DModel.components.get(currentEntity());
			if(model != null){
				rayResult.direction.nor();
				model.modelInstance.transform
				.idt()
				.setToLookAt(rayResult.direction, rayResult.direction.cpy().rotate(Vector3.Z, angle).rotate(Vector3.X, 90))
				.mulLeft(new Matrix4().idt().translate(rayResult.origin.cpy().add(0, -0.1f, 0)))
				
						;
				
				System.out.println(rayResult.origin);
			}
			
			return true;
		}
		return false;
	}
	
	
}
