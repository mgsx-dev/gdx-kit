package net.mgsx.game.examples.platformer.editors;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.plugins.EntityEditorPlugin;
import net.mgsx.game.core.ui.accessors.Accessor;
import net.mgsx.game.core.ui.accessors.FieldAccessor;
import net.mgsx.game.examples.platformer.inputs.JoystickController;

public class JoystickEditor extends Table {


	public static final EntityEditorPlugin factory = new EntityEditorPlugin() {
		@Override
		public Actor createEditor(Entity entity, Skin skin) {
			// Thread.currentThread().getContextClassLoader().loadClass("com.badlogic.gdx.controllers.desktop.DesktopControllerManager");
			return new JoystickEditor(JoystickController.components.get(entity), skin);
		}
	};
	
	final private JoystickController joystick;
	
	private ControllerListener listener;
	
	private Accessor currentLearn;
	
	private ButtonGroup<Button> group;
	
	public JoystickEditor(final JoystickController joystick, Skin skin) 
	{
		super(skin);
		this.joystick = joystick;
		
		group = new ButtonGroup<Button>();
		group.setMinCheckCount(0);
		
		final SelectBox<String> ctrlSelector = new SelectBox<String>(skin);
		Array<String> names = new Array<String>();
		names.add("");
		for (Controller controller : Controllers.getControllers()) {
			names.add(controller.getName());
		}
		ctrlSelector.setItems(names);

		final TextButton bt = new TextButton("Apply", getSkin());
		
		add("Controller");
		add(ctrlSelector).row();
		
		add("x");
		add(learn(new FieldAccessor(joystick, "xAxis"))).row();
		add("y");
		add(learn(new FieldAccessor(joystick, "yAxis"))).row();
		add("jump");
		add(learn(new FieldAccessor(joystick, "jumpButton"))).row();
		add("grab");
		add(learn(new FieldAccessor(joystick, "grabButton"))).row();
		
		add(bt);
		
		bt.addListener(new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				joystick.configured = true;
			}
		});
		
		ctrlSelector.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				select(ctrlSelector.getSelected());
			}
		});
	}

	private Actor learn(final Accessor accessor){
		final TextButton bt = new TextButton("Learn", getSkin(), "toggle");
		final Label value = new Label("", getSkin());
		group.add(bt);
		bt.addListener(new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if(bt.isChecked()){
					currentLearn = accessor;
				}else{
					currentLearn = null;
				}
			}
		});
		Table table = new Table(getSkin());
		table.add(bt);
		table.add(value);
		return table;
		
	}
	
	protected void select(String name) 
	{
		if(joystick.controller != null && listener != null){
			joystick.controller.removeListener(listener);
		}
		for (Controller controller : Controllers.getControllers()) {
			if(controller.getName().equals(name)){
				listener = new ControllerAdapter(){
					@Override
					public boolean axisMoved(Controller controller, int axisIndex, float value) {
						if(currentLearn != null){
							currentLearn.set(axisIndex);
						}
						return true;
					}
					@Override
					public boolean buttonDown(Controller controller, int buttonIndex) {
						if(currentLearn != null){
							currentLearn.set(buttonIndex);
						}
						return true;
					}
				};
				
				joystick.controller = controller;
				joystick.controller.addListener(listener);
			}
		}
	}


}
