package net.mgsx.game.plugins.box2d;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializer;
import com.badlogic.gdx.utils.JsonValue;

import net.mgsx.game.plugins.box2d.model.Box2DBodyModel;
import net.mgsx.game.plugins.box2d.model.Box2DFixtureModel;
import net.mgsx.game.plugins.box2dold.model.WorldItem;

public class Box2DModelSerializer implements Serializer<Box2DBodyModel> {

	private WorldItem context;
	
	
	public Box2DModelSerializer(WorldItem context) {
		super();
		this.context = context;
	}

	@Override
	public void write(Json json, Box2DBodyModel object, Class knownType) {
		json.writeObjectStart();
		json.writeField(object, "id");
		json.writeField(object, "def");
		json.writeField(object, "fixtures");
		json.writeObjectEnd();
	}

	@Override
	public Box2DBodyModel read(Json json, JsonValue jsonData, Class type) {
		Box2DBodyModel object = new Box2DBodyModel();
		
		// first read defs
		json.readField(object, "id", jsonData);
		json.readField(object, "def", jsonData);
		json.readField(object, "fixtures", jsonData);

		// then create instances
		object.context = context;
		object.body = context.world.createBody(object.def);
		object.body.setUserData(null); // dont know yet
		for(Box2DFixtureModel fixture : object.fixtures){
			fixture.fixture = object.body.createFixture(fixture.def);
		}
		
		return object;
	}

}
