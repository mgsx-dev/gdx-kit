package net.mgsx.game.examples.td.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.BSpline;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.examples.td.components.NavComponent;
import net.mgsx.game.examples.td.components.NavMeshComponent;
import net.mgsx.game.examples.td.components.PathFollower;
import net.mgsx.game.examples.td.utils.NavMesh;
import net.mgsx.game.plugins.core.components.Transform2DComponent;
import net.mgsx.game.plugins.g3d.components.G3DModel;
import net.mgsx.game.plugins.spline.components.PathComponent;

@EditableSystem
public class NavSystem extends IteratingSystem
{
	public NavMesh navMesh;
	
	public NavSystem() {
		super(Family.all(NavComponent.class, G3DModel.class, Transform2DComponent.class).get(), GamePipeline.AFTER_LOGIC);
	}
	
	public void randomPath(Entity entity, Vector3 position, Vector3 normal)
	{
		int src = navMesh.triCast(position, normal);
		
		// TODO path to where ?
		Array<Vector3> path = new Array<Vector3>();
		if(src >= 0 && navMesh.pathFind(src, MathUtils.random(navMesh.triNodes.length-1), path)){
			// TODO build bspline
			path.insert(0, position);
			path.insert(0, position);
			path.insert(0, position);
			Vector3 [] cpts = path.toArray(Vector3.class);
			if(cpts.length < 4) return;
			
			BSpline<Vector3> bspline = new BSpline<Vector3>(cpts, 3, false);
			
			PathComponent pathComp = getEngine().createComponent(PathComponent.class);
			pathComp.path = bspline;
			PathFollower follower = getEngine().createComponent(PathFollower.class);
			follower.path3d = bspline;
			follower.length = bspline.approxLength(100);
			entity.add(pathComp);
			entity.add(follower);
		}
	}
	public void pathTo(Entity entity, Vector3 position, Vector3 destination, Vector3 normal)
	{
		int src = navMesh.triCast(position.cpy().mulAdd(normal, -1), normal);
		int dst = navMesh.triCast(destination.cpy().mulAdd(normal, -1), normal);
		
		if(src < 0 || dst < 0) return;
		
		// TODO path to where ?
		Array<Vector3> path = new Array<Vector3>();
		if(navMesh.pathFind(src, dst, path)){
			// TODO build bspline
			path.insert(0, position);
			path.insert(0, position);
			path.insert(0, position);
			path.add(destination);
			path.add(destination);
			path.add(destination);
			Vector3 [] cpts = path.toArray(Vector3.class);
			//if(cpts.length < 4) return;
			
			BSpline<Vector3> bspline = new BSpline<Vector3>(cpts, 3, false);
			
			PathComponent pathComp = getEngine().createComponent(PathComponent.class);
			pathComp.path = bspline;
			PathFollower follower = getEngine().createComponent(PathFollower.class);
			follower.path3d = bspline;
			follower.length = bspline.approxLength(100);
			entity.add(pathComp);
			entity.add(follower);
		}
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
				// model.modelInstance.transform.rotate(Vector3.X, 90);
				// TODO merge into navmesh !
				navMesh = new NavMesh(model.modelInstance.model);
				navMesh.buildGraph(180);
			}
		});
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		
		if(navMesh == null) return;
		Transform2DComponent transform = Transform2DComponent.components.get(entity);
		
		NavComponent nav = NavComponent.components.get(entity);
		
		if(nav.index < 0){
			// TODO set triangle
			if(navMesh.rayCast(new Ray(new Vector3(transform.position, transform.depth+1), new Vector3(0,0,-1)), nav.position, nav.normal)){
				transform.depth = nav.position.z;
				transform.normal.set(nav.normal.x, nav.normal.y, nav.normal.z);
			}
		}
		else{
			// TODO
		}
	}
	
}
