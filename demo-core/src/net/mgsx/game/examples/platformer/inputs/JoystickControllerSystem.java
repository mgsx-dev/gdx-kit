package net.mgsx.game.examples.platformer.inputs;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.examples.platformer.animations.Character2D;

public class JoystickControllerSystem extends IteratingSystem
{
	public JoystickControllerSystem() {
		super(Family.all(JoystickController.class, PlayerController.class, Character2D.class).get(), GamePipeline.INPUT+1); // after keyboard
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) 
	{
		JoystickController joystick = JoystickController.components.get(entity);
		PlayerController player = PlayerController.components.get(entity);
		Character2D character = Character2D.components.get(entity);
		
		if(joystick.controller == null || !joystick.configured) return;
		
		float x = joystick.controller.getAxis(joystick.xAxis);
		float y = joystick.controller.getAxis(joystick.yAxis);
		
		float epsilon = .5f;
		
		player.left = x < -epsilon;
		player.right = x > epsilon;
		player.up = y > epsilon;
		player.down = y < -epsilon;

		boolean jump = joystick.controller.getButton(joystick.jumpButton);
		player.justJump = jump && !player.jump;
		player.jump = jump;
		
		boolean grab = joystick.controller.getButton(joystick.grabButton);
		player.justGrab = grab && !player.grab;
		player.grab = grab;
		
		if(player.left)
			character.rightToLeft = true;
		else if(player.right)
			character.rightToLeft = false;
	}
}
