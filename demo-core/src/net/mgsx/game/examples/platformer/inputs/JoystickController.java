package net.mgsx.game.examples.platformer.inputs;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.controllers.Controller;

import net.mgsx.game.core.annotations.EditableComponent;

@EditableComponent
public class JoystickController implements Component
{
	
	public final static ComponentMapper<JoystickController> components = ComponentMapper
			.getFor(JoystickController.class);
	
	
	public Controller controller;
	
	public int xAxis, yAxis, jumpButton, grabButton;


	public boolean configured; // true when all codes are ok
}
