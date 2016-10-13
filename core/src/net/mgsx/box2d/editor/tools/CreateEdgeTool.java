package net.mgsx.box2d.editor.tools;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import net.mgsx.box2d.editor.model.BodyItem;
import net.mgsx.box2d.editor.model.FixtureItem;
import net.mgsx.box2d.editor.model.WorldItem;
import net.mgsx.fwk.editor.tools.MultiClickTool;

public class CreateEdgeTool extends MultiClickTool
{
	private WorldItem worldItem;
	
	public CreateEdgeTool(Camera camera, WorldItem worldItem) {
		super("Edge", camera, 4);
		this.worldItem = worldItem;
	}


	@Override
	protected void complete() 
	{
		final BodyItem bodyItem = worldItem.currentBody("Chain Loop", dots.get(0).x, dots.get(0).y);
		
		// TODO not good ...
		EdgeShape shape = new EdgeShape();
		shape.set(bodyItem.body.getPosition(), new Vector2(dots.get(2)).sub(bodyItem.body.getPosition()));
		shape.setVertex0(new Vector2(dots.get(0)).sub(bodyItem.body.getPosition()));
		shape.setVertex3(new Vector2(dots.get(3)).sub(bodyItem.body.getPosition()));
		shape.setHasVertex0(true);
		shape.setHasVertex3(true);
		FixtureDef def = worldItem.settings.fixture();
		def.shape = shape;
		
		bodyItem.fixtures.add(new FixtureItem("Edge", def, bodyItem.body.createFixture(def)));
	}

}
