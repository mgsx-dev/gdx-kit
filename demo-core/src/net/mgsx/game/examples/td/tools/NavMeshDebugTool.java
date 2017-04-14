package net.mgsx.game.examples.td.tools;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.tools.Tool;
import net.mgsx.game.examples.td.utils.NavMesh;
import net.mgsx.game.examples.td.utils.NavMesh.TriNode;
import net.mgsx.game.plugins.g3d.components.G3DModel;

@Editable
public class NavMeshDebugTool extends Tool
{
	@Editable
	public boolean pathMode;
	
	private NavMesh navMesh;
	
	Vector3 origin = null;
	
	
	private Array<Vector3> dots = new Array<Vector3>();
	
	public NavMeshDebugTool(EditorScreen editor) {
		super("NavMeshDebugTool", editor);
	}
	
	@Override
	public boolean allowed(Array<Entity> selection) {
		// TODO Auto-generated method stub
		return selection.size == 1 && G3DModel.components.has(selection.first());
	}
	
	@Override
	protected void activate() {
		Entity entity = currentEntity();
		G3DModel model = G3DModel.components.get(entity);
		model.modelInstance.transform.idt();
		model.modelInstance.transform.rotate(Vector3.X, 0);
		navMesh = new NavMesh(model.modelInstance.model);
		build();
		super.activate();
	}
	
	@Editable
	public float maxAngle = 40;
	@Editable
	public float rotation = 0;
	
	@Editable
	public void build(){
		navMesh.buildGraph(maxAngle);
	}
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		
		if(button != Input.Buttons.LEFT) return false;
		
		Entity entity = currentEntity();
		G3DModel model = G3DModel.components.get(entity);
		
		Vector3 nearest = new Vector3();
		Vector3 normal = new Vector3();
		Ray ray = editor.getGameCamera().getPickRay(screenX, screenY);
		Matrix4 mat = model.modelInstance.nodes.get(0).globalTransform.cpy().inv();
//		ray.mul(editor.getGameCamera().combined);
		ray.mul(mat);
//		ray.origin.z += 1;
		
		if(pathMode){
			if(origin == null){
				dots.clear();
				if(navMesh.rayCast(ray, nearest, normal))
				{
					origin = nearest.cpy();
				}
			}else{
				if(navMesh.rayCast(ray, nearest, normal))
				{
					Array<Vector3> path = new Array<Vector3>();
					if(navMesh.pathFind(origin.cpy().add(0,0,1), nearest.cpy().add(0,0,1), new Vector3(0,0,-1), path)){
						dots.clear();
						dots.add(origin.cpy());
						for(Vector3 v : path){
							dots.add(v);
						}
						dots.add(nearest);
						origin = null; //.set(nearest);
					}
				}
			}
			
			
		}else{
			if(navMesh.rayCast(ray, nearest, normal))
			{
				System.out.println("new!");
				dots.add(nearest);
				dots.add(normal);
			}
		}
		
		return true;
	}

	
	private Vector3 tmp = new Vector3();
	
	@Override
	public void render(ShapeRenderer renderer) {
		Entity entity = currentEntity();
		G3DModel model = G3DModel.components.get(entity);
		
		model.modelInstance.transform.idt();
		
		model.modelInstance.nodes.get(0).localTransform.idt();
		model.modelInstance.nodes.get(0).localTransform.rotate(Vector3.X, -90);
		model.modelInstance.nodes.get(0).localTransform.rotate(Vector3.Z, rotation);
		model.modelInstance.nodes.get(0).calculateWorldTransform();
		
		renderer.setProjectionMatrix(editor.getGameCamera().combined);
		renderer.setTransformMatrix(model.modelInstance.nodes.get(0).localTransform);
		renderer.begin(ShapeType.Line);
		
		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
		// dots.clear();
		if(pathMode){
			renderer.setColor(Color.BLUE);
			for(int i=0 ; i+1<dots.size ; i++){
				renderer.line(dots.get(i), dots.get(i+1));
			}
		}else{
			renderer.setColor(Color.ORANGE);
			for(int i=0 ; i+1<dots.size ; i+=2){
				renderer.line(dots.get(i), tmp.set(dots.get(i)).mulAdd(dots.get(i+1), 1f));
			}
		}
		
		renderer.setColor(Color.YELLOW);
		for(TriNode tri : navMesh.triNodes){
			renderer.line(tri.position, tmp.set(tri.position).mulAdd(tri.normal, 1f));
			
		}
		
		renderer.end();
		
	}

}
