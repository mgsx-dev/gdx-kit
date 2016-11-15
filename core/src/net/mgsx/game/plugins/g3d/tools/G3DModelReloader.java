package net.mgsx.game.plugins.g3d.tools;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;

import net.mgsx.game.core.helpers.EntityReloader;
import net.mgsx.game.plugins.g3d.components.G3DModel;

public class G3DModelReloader extends EntityReloader<Model>
{
	
	public G3DModelReloader(Engine engine) {
		super(engine, Family.all(G3DModel.class).get());
	}

	@Override
	protected void reload(Entity entity, Model asset) 
	{
		G3DModel model = G3DModel.components.get(entity);
		model.modelInstance = new ModelInstance(asset);
		model.animationController = new AnimationController(model.modelInstance);
	}
	
}
