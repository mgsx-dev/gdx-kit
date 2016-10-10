package net.mgsx.box2d.editor.tools;

import net.mgsx.box2d.editor.BodyItem;
import net.mgsx.box2d.editor.WorldItem;
import net.mgsx.fwk.editor.tools.MultiClickTool;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;

public class CreateEdgeTool extends MultiClickTool
{
	private WorldItem worldItem;
	private BodyItem bodyItem;
	
	public CreateEdgeTool(Camera camera, WorldItem worldItem, BodyItem bodyItem) {
		super("Edge", camera, 4);
		this.worldItem = worldItem;
		this.bodyItem = bodyItem;
	}


	@Override
	protected void complete() 
	{
		if(bodyItem == null){
			BodyDef bodyDef = worldItem.settings.body();
			bodyDef.position.set(dots.first());
			Body body = worldItem.world.createBody(bodyDef);
			bodyItem = new BodyItem("", bodyDef, body);
			worldItem.items.bodies.add(bodyItem);
		}

		
		EdgeShape shape = new EdgeShape();
		shape.set(dots.get(1), dots.get(2));
		shape.setVertex0(dots.get(0));
		shape.setVertex3(dots.get(3));
		shape.setHasVertex0(true);
		shape.setHasVertex3(true);
		FixtureDef def = worldItem.settings.fixture();
		def.shape = shape;
		
		bodyItem.body.createFixture(def);
		
		bodyItem = null; // clear because of convex crash!
	}

}
