package net.mgsx.game.examples.shmup.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.examples.shmup.component.PlayerBullet;
import net.mgsx.game.plugins.camera.model.POVModel;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

@EditableSystem
public class ShmupPlayerBulletSystem extends IteratingSystem
{
	@Inject public POVModel pov;
	
	public ShmupPlayerBulletSystem() {
		super(Family.all(Transform2DComponent.class, PlayerBullet.class).get(), GamePipeline.LOGIC);
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		Transform2DComponent t = Transform2DComponent.components.get(entity);
		
		Camera camera = pov.camera;
		
		float z = camera.project(new Vector3()).z;
		
		Vector3 a = camera.unproject(new Vector3(0, 0, z));
		Vector3 b = camera.unproject(new Vector3(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), z));
		
//		t.position.x += speed * deltaTime;
		
		boolean out = false;
		if(t.position.x < a.x){
			out = true;
		}
		if(t.position.x > b.x){
			out = true;
		}
		if(t.position.y > a.y){
			out = true;
		}
		if(t.position.y < b.y){
			out = true;
		}
		
		if(out){
			getEngine().removeEntity(entity);
		}
		
		
	}

}
