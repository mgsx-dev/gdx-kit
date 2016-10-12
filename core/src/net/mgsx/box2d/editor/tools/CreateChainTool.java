package net.mgsx.box2d.editor.tools;

import net.mgsx.box2d.editor.BodyItem;
import net.mgsx.box2d.editor.FixtureItem;
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
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;

public class CreateChainTool extends MultiClickTool
{
	private WorldItem worldItem;
	
	public CreateChainTool(Camera camera, WorldItem worldItem) {
		super("Chain", camera);
		this.worldItem = worldItem;
	}


	@Override
	protected void complete() 
	{
		final BodyItem bodyItem = worldItem.currentBody("Chain", dots.get(0).x, dots.get(0).y);

		ChainShape shape = new ChainShape();
		// TODO find another way or create helper
		Vector2 [] array = new Vector2[dots.size];
		for(int i=0 ; i<dots.size; i++) array[i] = new Vector2(dots.get(i)).sub(bodyItem.body.getPosition());
		shape.createChain(array);
		
		FixtureDef def = worldItem.settings.fixture();
		def.shape = shape;
		
		bodyItem.fixtures.add(new FixtureItem("Chain", def, bodyItem.body.createFixture(def)));
	}

}
