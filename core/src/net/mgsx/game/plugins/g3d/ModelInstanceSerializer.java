package net.mgsx.game.plugins.g3d;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;

import net.mgsx.game.core.storage.AssetSerializer;

public class ModelInstanceSerializer extends AssetSerializer<ModelInstance>
{
	public ModelInstanceSerializer() {
		super(ModelInstance.class);
	}
	
	@Override
	protected String getReference(ModelInstance object) {
		return getAssets().getAssetFileName(object.model);
	}
	
	@Override
	protected ModelInstance getInstance(String reference) {
		return new ModelInstance(getAssets().get(reference, Model.class));
	}

}