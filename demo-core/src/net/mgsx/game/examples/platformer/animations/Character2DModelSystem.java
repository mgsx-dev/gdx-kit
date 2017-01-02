package net.mgsx.game.examples.platformer.animations;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.plugins.core.components.Transform2DComponent;
import net.mgsx.game.plugins.g3d.components.G3DModel;

public class Character2DModelSystem extends IteratingSystem
{

	public Character2DModelSystem() {
		super(Family.all(Character2D.class, G3DModel.class).get(), GamePipeline.BEFORE_CULLING);
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		G3DModel model = G3DModel.components.get(entity);
		Character2D character = Character2D.components.get(entity);
		Transform2DComponent transform = Transform2DComponent.components.get(entity);
		
		model.modelInstance.transform.idt();
		if(transform != null){
			model.modelInstance.transform.translate(transform.position.x, transform.position.y, 0);
		}
		float angle = character.angleRange;
		float targetAngle = character.facing ? 0 : (character.rightToLeft ? -angle : angle);
		character.angle = Interpolation.linear.apply(character.angle, targetAngle, character.angularSpeed * deltaTime);
		model.modelInstance.transform.rotate(Vector3.Y, character.angle);
	}

}
