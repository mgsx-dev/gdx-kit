package net.mgsx.game.plugins.procedural.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.ModelBatch;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.plugins.g3d.components.G3DModel;
import net.mgsx.game.plugins.procedural.components.GasComponent;

@EditableSystem
public class GasRenderSystem extends IteratingSystem
{
	private EditorScreen screen;
	
	private ModelBatch batch;
	
	
	public GasRenderSystem(EditorScreen screen) {
		super(Family.all(G3DModel.class, GasComponent.class).get(), GamePipeline.RENDER);
		this.screen = screen;
		reload();
	}
	
	@Editable
	public void reload(){
		if(batch != null) batch.dispose();
		batch = new ModelBatch(
				Gdx.files.internal("shaders/gas.vert"),
				Gdx.files.internal("shaders/gas.frag"));
	}
	
	@Override
	public void update(float deltaTime) {
		batch.begin(screen.getGameCamera());
		super.update(deltaTime);
		batch.end();
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		G3DModel model = G3DModel.components.get(entity);
		batch.render(model.modelInstance);
	}

}
