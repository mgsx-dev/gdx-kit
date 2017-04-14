package net.mgsx.game.examples.td.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.examples.td.components.NavComponent;
import net.mgsx.game.examples.td.components.NavMeshComponent;
import net.mgsx.game.examples.td.utils.NavMesh;
import net.mgsx.game.plugins.core.components.Transform2DComponent;
import net.mgsx.game.plugins.g3d.components.G3DModel;

@EditableSystem
public class NavSystem extends IteratingSystem
{
	NavMesh navMesh;
	
	public NavSystem() {
		super(Family.all(NavComponent.class, G3DModel.class, Transform2DComponent.class).get(), GamePipeline.AFTER_LOGIC);
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		engine.addEntityListener(Family.all(G3DModel.class, NavMeshComponent.class).get(), new EntityListener() {
			
			@Override
			public void entityRemoved(Entity entity) {
				
			}
			
			@Override
			public void entityAdded(Entity entity) {
				G3DModel model = G3DModel.components.get(entity);
				model.modelInstance.transform.idt();
				model.modelInstance.transform.rotate(Vector3.X, 90);
				navMesh = new NavMesh(model.modelInstance.model);
			}
		});
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		
		if(navMesh == null) return;
		Transform2DComponent transform = Transform2DComponent.components.get(entity);
		
		Vector3 contact = new Vector3();
		if(navMesh.rayCast(new Ray(new Vector3(transform.position, 100), new Vector3(0,0,-1)), contact, new Vector3())){
			transform.depth = contact.z;
		}
		
	}
}
