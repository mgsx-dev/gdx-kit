package net.mgsx.game.examples.platformer.inputs;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.examples.platformer.animations.Character2D;

public class KeyboardControllerSystem extends IteratingSystem
{
	public KeyboardControllerSystem() {
		super(Family.all(KeyboardController.class, PlayerController.class, Character2D.class).get(), GamePipeline.INPUT);
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) 
	{
		PlayerController player = PlayerController.components.get(entity);
		KeyboardController keys = KeyboardController.components.get(entity);
		Character2D character = Character2D.components.get(entity);
		
		player.left = Gdx.input.isKeyPressed(keys.left);
		player.right = Gdx.input.isKeyPressed(keys.right);
		player.up = Gdx.input.isKeyPressed(keys.up);
		player.down = Gdx.input.isKeyPressed(keys.down);

		player.jump = Gdx.input.isKeyPressed(keys.jump);
		player.grab = Gdx.input.isKeyPressed(keys.grab);
		
		player.justGrab = Gdx.input.isKeyJustPressed(keys.grab);
		player.justJump = Gdx.input.isKeyJustPressed(keys.jump);
		
		if(player.left)
			character.rightToLeft = true;
		else if(player.right)
			character.rightToLeft = false;
	}
}
