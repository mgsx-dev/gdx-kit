package net.mgsx.game.examples.openworld.ui;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

import net.mgsx.game.core.Kit;
import net.mgsx.game.examples.openworld.components.ObjectMeshComponent;
import net.mgsx.game.examples.openworld.components.TreesComponent;
import net.mgsx.game.examples.openworld.model.OpenWorldElement;
import net.mgsx.game.examples.openworld.model.OpenWorldModel;
import net.mgsx.game.examples.openworld.systems.OpenWorldCameraSystem;
import net.mgsx.game.examples.openworld.systems.OpenWorldGameSystem;
import net.mgsx.game.examples.openworld.systems.UserObjectSystem;
import net.mgsx.game.examples.openworld.systems.UserObjectSystem.UserObject;
import net.mgsx.game.plugins.bullet.system.BulletWorldSystem;

public class OpenWorldHUD extends Table
{
	private static enum GameAction{
		LOOK, GRAB, EAT, USE, DROP // ...
	}
	private GameAction action;
	private Label infoLabel;
	
	// TODO something better like : Backpack selection and World selection
	private OpenWorldElement selectedItem;
	String elementName = null;
	Entity e = null; // TODO dangerous to keep reference to entity ... except if it's in the same frame.
	UserObject uo = null;
	Vector3 position = new Vector3();
	
	Table backpack;
	ObjectMap<String, TextButton> backpackItemButtons = new ObjectMap<String, TextButton>();
	// TODO should be saved as well and reloaded !
	ObjectMap<String, Array<OpenWorldElement>> backpackContent = new ObjectMap<String, Array<OpenWorldElement>>();
	private Engine engine;
	
	public OpenWorldHUD(Skin skin, final Engine engine) {
		super(skin);
		this.engine = engine;
		
		build();
		
		Kit.inputs.addProcessor(new InputAdapter(){
			@Override
			public boolean touchDown(int screenX, int screenY, int pointer, int button) {
				if(button != Input.Buttons.LEFT) return false;
				Camera camera = engine.getSystem(OpenWorldCameraSystem.class).getCamera();
				
				BulletWorldSystem bulletWorld = engine.getSystem(BulletWorldSystem.class);
				
				Ray ray = camera.getPickRay(screenX, screenY);
				ray.direction.scl(camera.far);
				Ray rayResult = new Ray();
				Object o = bulletWorld.rayCastObject(ray, rayResult);
				
				// TODO always set ray for action resolution
				
				if(o != null){
					
					// find which object as been picked :
					
					if(o instanceof Entity){
						e = (Entity)o;
						ObjectMeshComponent omc = ObjectMeshComponent.components.get(e);
						if(omc != null){
							if(omc.userObject != null){
								uo = omc.userObject;
								elementName = omc.userObject.element.type;
							}
						}
					}
					else if(o instanceof TreesComponent){
						elementName = "tree";
					}
					
					position.set(rayResult.origin);
					
					return resolveInteraction();
				}
				return false;
			}
		});
	}
	
	public void resetState() {
		action = null;
		actionButtonGroup = null;
		uo = null;
		selectedItem = null;
		position.setZero();
		elementName = null;
		e = null;
		backpackItemButtons.clear();;
		backpackContent.clear();
		
		clearChildren();
		clearActions();
		
		build();
	}

	public void addItemToBackpack(final OpenWorldElement item){
		TextButton bt = backpackItemButtons.get(item.type);
		if(bt == null){
			backpackItemButtons.put(item.type, bt = new TextButton("", getSkin()));
			backpack.add(bt);
			backpackContent.put(item.type, new Array<OpenWorldElement>());
			bt.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					selectedItem = backpackContent.get(item.type).peek();
					resolveInteraction();
					//removeFromBackpack(item.type);
				}
			});
		}
		backpackContent.get(item.type).add(item);
		bt.setText(OpenWorldModel.name(item.type) + " : " + backpackContent.get(item.type).size);
	}
	
	private void removeFromBackpack(String type) {
		OpenWorldElement item = backpackContent.get(type).pop();
		if(backpackContent.get(type).size > 0){
			backpackItemButtons.get(type).setText(OpenWorldModel.name(item.type) + " : " + backpackContent.get(item.type).size);
		}else{
			backpackContent.remove(type);
			backpackItemButtons.get(type).remove();
			backpackItemButtons.remove(type);
		}
		engine.getSystem(OpenWorldGameSystem.class).backpack.removeValue(item, true);
		
		// append just in front of player ! TODO ray cast for ground !
		Camera camera = engine.getSystem(OpenWorldCameraSystem.class).getCamera();
		
		// materialize item
		OpenWorldModel.generateElement(item);
		
		item.dynamic = true;
		
		// TODO mimic the move tool (drag'n'drop)
		
		item.position.set(camera.position).mulAdd(camera.direction, 2); // 2m
		item.rotation.idt();
		
		engine.getSystem(UserObjectSystem.class).appendObject(item);
			
	}

	private ButtonGroup<TextButton> actionButtonGroup;
	
	private Table contextualActionsTable;
	
	private void build() {
		
		// TODO player status
		
		// TODO backpack
		backpack = new Table(getSkin());
		
		backpack.add("Backpack content : ");
		
		
		
		// TODO actions !
		// like Point'n'click : look, build, ...etc
		actionButtonGroup = new ButtonGroup<TextButton>();
		
		
		Table actionsTable = new Table(getSkin());
		
		actionsTable.add("Actions : ");
		
		actionsTable.add(createActionButton("Pick Up", GameAction.GRAB));
		actionsTable.add(createActionButton("Look At", GameAction.LOOK));
		actionsTable.add(createActionButton("Eat/Drink", GameAction.EAT));
		actionsTable.add(createActionButton("Use", GameAction.USE));
		actionsTable.add(createActionButton("Drop", GameAction.DROP));
		
		contextualActionsTable = new Table(getSkin());
		actionsTable.add(contextualActionsTable);
		
		infoLabel = new Label("", getSkin());
		add(infoLabel).colspan(2).expand().center().bottom().row();
		add(actionsTable).expandX().right();
		add(backpack).expandX().left();
		
		
		for(OpenWorldElement e : engine.getSystem(OpenWorldGameSystem.class).backpack){
			addItemToBackpack(e);
		}
	}

	private TextButton createActionButton(String label, final GameAction newAction) {
		TextButton bt = new TextButton(label, getSkin(), "toggle");
		actionButtonGroup.add(bt);
		bt.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				action = newAction;
				resolveInteraction();
			}
		});
		return bt;
	}

	protected boolean resolveInteraction() {
		
		// TODO put texts in model with i18n
		
		boolean actionPerformed = false;
		boolean actionCanceled = false;
		
		UserObjectSystem userObject = engine.getSystem(UserObjectSystem.class);
		
		// eat, grab, look
		if(action == GameAction.GRAB){
			// try to grab element
			if(e != null){
				// remove it TODO but keep its properties somewhere in order to regenerates !
				if(uo != null){
					userObject.removeElement(uo);
					engine.removeEntity(e);
					// and add it to the player backpack ! if meet conditions (size, ...etc).
					// animate model : lerp to player and inc GUI
					addItemToBackpack(uo.element);
					engine.getSystem(OpenWorldGameSystem.class).backpack.add(uo.element);
					infoLabel.setText(OpenWorldModel.name(uo.element.type) + " added to your backpack.");
					actionPerformed = true;
				}
			}else{
				infoLabel.setText("Pick anything you want...");
			}
		}
		else if(action == GameAction.USE){
			if(selectedItem != null && elementName != null){
				OpenWorldElement element = OpenWorldModel.useTool(selectedItem.type, elementName);
				if(element != null){
					element.position.set(position);
					userObject.appendObject(element);
					infoLabel.setText(OpenWorldModel.name(element.type) + " just spawned!");
					actionPerformed = true;
				}else{
					infoLabel.setText("Nothing happens...");
					actionCanceled = true;
				}
			}
			else if(selectedItem == null && elementName == null){
				infoLabel.setText("Use something from your backpack...");
			}
			// use in the world ?
			else if(selectedItem != null){
				infoLabel.setText("Use your " + OpenWorldModel.name(selectedItem.type) + " on something...");
			}
		}
		else if(action == GameAction.DROP){
			// TODO and if raycasted toward the world ...
			if(selectedItem != null){
				removeFromBackpack(selectedItem.type);
				actionPerformed = true;
				infoLabel.setText(OpenWorldModel.name(selectedItem.type) + " was dropped from your backpack");
			}else{
				infoLabel.setText("Drop something from your backpack");
			}
		}
		else if(action == GameAction.EAT){
			if(elementName != null){
				// TODO eat if not too far
				infoLabel.setText("You're too far from it.");
				actionCanceled = true;
			}
			else if(selectedItem != null){
				// TODO eatable only !
				removeFromBackpack(selectedItem.type);
				actionPerformed = true;
				infoLabel.setText(OpenWorldModel.name(selectedItem.type) + " gives you some energie!");
			}else{
				infoLabel.setText("Get something to eat...");
			}
		}
		// default look
		else
		{
			if(elementName != null){
				String description = OpenWorldModel.description(elementName);
				infoLabel.setText(description);
				actionPerformed = true;
			}
			else if(selectedItem != null){
				String description = OpenWorldModel.description(selectedItem.type);
				infoLabel.setText(description);
				actionPerformed = true;
			}else{
				infoLabel.setText("Touch something to examin it...");
			}
		}
		
		
		// TODO not always clear if action not resolved
		// clear all but action
		if(actionPerformed || actionCanceled){
			e = null;
			uo = null;
			elementName = null;
			if(action != GameAction.USE)
				selectedItem = null; // TODO depends on context : use will reuse
		}
		
		return actionPerformed;
	}

	
}
