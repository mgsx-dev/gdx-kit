package net.mgsx.game.plugins.box2d.storage;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import net.mgsx.game.core.storage.ContextualSerializer;
import net.mgsx.game.plugins.box2d.components.Box2DJointModel;
import net.mgsx.game.plugins.box2d.components.WorldItem;

public class Box2DJointSerializer extends ContextualSerializer<Box2DJointModel>
{

	private WorldItem context;
	
	
	public Box2DJointSerializer(WorldItem context) 
	{
		super(Box2DJointModel.class);
		this.context = context;
	}

	@Override
	public void write(Json json, Box2DJointModel object, Class knownType) {
		json.writeObjectStart();
		json.writeField(object, "id");
		json.writeField(object, "def");
		json.writeObjectEnd();
	}

	@Override
	public Box2DJointModel read(Json json, JsonValue jsonData, Class type) {
		Box2DJointModel object = new Box2DJointModel();
		
		// first read defs
		json.readField(object, "id", jsonData);
		json.readField(object, "def", jsonData);

		// then create instances
		// object.context = context;
		object.joint = context.world.createJoint(object.def);
		object.joint.setUserData(null); // dont know yet
		
		return object;
	}

}
