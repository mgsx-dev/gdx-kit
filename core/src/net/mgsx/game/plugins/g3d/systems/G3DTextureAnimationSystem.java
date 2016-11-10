package net.mgsx.game.plugins.g3d.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.NodePart;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.plugins.g3d.components.G3DModel;
import net.mgsx.game.plugins.g3d.components.TextureAnimationComponent;

public class G3DTextureAnimationSystem extends IteratingSystem {
	public G3DTextureAnimationSystem() {
		super(Family.all(TextureAnimationComponent.class, G3DModel.class).get(), GamePipeline.BEFORE_RENDER);
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		TextureAnimationComponent component = entity.getComponent(TextureAnimationComponent.class);
		component.time += deltaTime;
		component.uOffset += component.uPerSec * deltaTime;
		component.vOffset += component.vPerSec * deltaTime;
		G3DModel model = entity.getComponent(G3DModel.class);
		for(Node node : model.modelInstance.nodes)
			for(NodePart part : node.parts){
				TextureAttribute ta = (TextureAttribute)part.material.get(TextureAttribute.Diffuse);
				ta.offsetU = component.uOffset;
				ta.offsetV = component.vOffset;
			}
	}
}