package net.mgsx.game.examples.openworld.tools;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.core.tools.Tool;
import net.mgsx.game.examples.openworld.components.ObjectMeshComponent;
import net.mgsx.game.examples.openworld.model.OpenWorldElement;
import net.mgsx.game.examples.openworld.model.OpenWorldModel;
import net.mgsx.game.examples.openworld.utils.SmoothBoxShapeBuilder;
import net.mgsx.game.plugins.bullet.system.BulletWorldSystem;

@Editable
public class AddElementTool extends Tool
{

	@Inject BulletWorldSystem bulletWorld;
	@Editable public boolean dynamic;
	@Editable public float size = 1;
	
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
			
			Entity newEntity = getEngine().createEntity();
			ObjectMeshComponent lmc = getEngine().createComponent(ObjectMeshComponent.class);
			lmc.transform.idt().translate(rayResult.origin);
			RandomXS128 rnd = new RandomXS128();
			OpenWorldElement e = OpenWorldModel.generateNewElement(rnd.nextLong());
			e.size *= size;
			lmc.element = e;
			lmc.mesh = createMesh(e);
			newEntity.add(lmc);
			
			// physics :
			bulletWorld.createBox(newEntity, dynamic ? lmc.transform.translate(0,1,0) : lmc.transform, e.size * e.geo_x, e.size * e.geo_y, e.size, dynamic);
			
			getEngine().addEntity(newEntity);
			
			return true;
		}
		return false;
	}

	private Mesh createMesh(OpenWorldElement e) 
	{
		
		//e.size = 1;
		// Matrix4 m = new Matrix4().scale(e.size * e.geo_x, e.size * e.geo_y, e.size);
		MeshBuilder builder = new MeshBuilder();
		builder.setColor(e.color);
		builder.begin(new VertexAttributes(
				VertexAttribute.Position(), 
				VertexAttribute.Normal(),
				VertexAttribute.ColorUnpacked()), GL20.GL_TRIANGLES);
		SmoothBoxShapeBuilder.build(builder, Vector3.Zero, new Vector3(e.size * e.geo_x, e.size * e.geo_y, e.size).scl(.5f));
		// BoxShapeBuilder.build(builder, m);
		Mesh mesh = builder.end();
		
		return mesh;
	}
	
	
}
