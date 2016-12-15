package net.mgsx.game.plugins.btree.storage;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.ai.btree.utils.BehaviorTreeLibraryManager;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import net.mgsx.game.core.storage.AssetAwareSerializer;
import net.mgsx.game.plugins.btree.BTreeModel;

public class BehaviorTreeSerializer extends AssetAwareSerializer<BTreeModel> 
{
	private Engine engine;
	
	public BehaviorTreeSerializer(Engine engine) {
		super();
		this.engine = engine;
	}

	@Override
	public void write(Json json, BTreeModel object, Class knownType) 
	{
		json.writeObjectStart();
		json.writeValue("library", parent.reference(object.libraryName));
		json.writeObjectEnd();
	}

	@Override
	public BTreeModel read(Json json, JsonValue jsonData, Class type) {
		BTreeModel model = engine.createComponent(BTreeModel.class);
		String library = json.readValue("library", String.class, jsonData);
		model.libraryName = library;
		model.tree = BehaviorTreeLibraryManager.getInstance().createBehaviorTree(library);
		return model;
	}
	
	

}
