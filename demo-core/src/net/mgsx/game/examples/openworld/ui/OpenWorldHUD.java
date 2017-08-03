package net.mgsx.game.examples.openworld.ui;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
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
import net.mgsx.game.examples.openworld.model.Compound;
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
		LOOK, GRAB, EAT, USE, DROP, CRAFT // ...
	}
	private GameAction action;
	private Label infoLabel;
	
	// TODO something better like : Backpack selection and World selection
	private OpenWorldElement backpackSelection;
	
	static class WorldSelection {
		public String elementName = null;
		/** short living reference, should be cleared upon processing since it may be removed at any time */
		public Entity e = null;
		public UserObject uo = null;
		public Vector3 position = new Vector3();
		public Vector3 normal = new Vector3();
		public Object object;
	}
	
	private WorldSelection worldSelection;
	
	private Array<OpenWorldElement> craftSelection;
	
	
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
				
				worldSelection = new WorldSelection();
				worldSelection.position.set(rayResult.origin);
				worldSelection.normal.set(rayResult.direction);
				worldSelection.object = o;
				if(o != null){
					// find which object as been picked :
					if(o instanceof Entity){
						worldSelection.e = (Entity)o;
						ObjectMeshComponent omc = ObjectMeshComponent.components.get(worldSelection.e);
						if(omc != null){
							if(omc.userObject != null){
								worldSelection.uo = omc.userObject;
								worldSelection.elementName = omc.userObject.element.type;
							}
						}
					}
					else if(o instanceof TreesComponent){
						worldSelection.elementName = "tree";
					}
				}
				return resolveInteraction();
			}
		});
	}
	
	public void resetState() {
		action = null;
		actionButtonGroup = null;
		backpackSelection = null;
		craftSelection = null;
		worldSelection = null;
		backpackItemButtons.clear();;
		backpackContent.clear();
		
		if(popin != null) popin.remove();
		popin = null;
		craftingView = null;
		
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
					backpackSelection = backpackContent.get(item.type).peek();
					resolveInteraction();
					//removeFromBackpack(item.type);
				}
			});
		}
		backpackContent.get(item.type).add(item);
		bt.setText(OpenWorldModel.name(item.type) + " : " + backpackContent.get(item.type).size);
	}
	
	private void dropFromBackpack(String type) {
		OpenWorldElement item = backpackContent.get(type).peek();
		
		removeFromBackpack(item);
		
		// append just in front of player ! TODO ray cast for ground !
		Camera camera = engine.getSystem(OpenWorldCameraSystem.class).getCamera();
		
		// materialize item
		OpenWorldModel.generateElement(item);
		
		// TODO project on bullet world
		
		item.position.set(camera.position).mulAdd(camera.direction, 2); // 2m
		item.rotation.idt();
		
		engine.getSystem(UserObjectSystem.class).appendObject(item);
			
	}
	
	private void removeFromBackpack(OpenWorldElement item) {
		backpackContent.get(item.type).removeValue(item, true);
		if(backpackContent.get(item.type).size > 0){
			backpackItemButtons.get(item.type).setText(OpenWorldModel.name(item.type) + " : " + backpackContent.get(item.type).size);
		}else{
			backpackContent.remove(item.type);
			backpackItemButtons.get(item.type).remove();
			backpackItemButtons.remove(item.type);
		}
		engine.getSystem(OpenWorldGameSystem.class).backpack.removeValue(item, true);
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
		actionButtonGroup.setMaxCheckCount(1);
		actionButtonGroup.setMinCheckCount(0);
		
		Table actionsTable = new Table(getSkin());
		
		actionsTable.add("Actions : ");
		
		actionsTable.add(createActionButton("Pick Up", GameAction.GRAB));
		actionsTable.add(createActionButton("Look At", GameAction.LOOK));
		actionsTable.add(createActionButton("Eat/Drink", GameAction.EAT));
		actionsTable.add(createActionButton("Use", GameAction.USE));
		actionsTable.add(createActionButton("Drop", GameAction.DROP));
		actionsTable.add(createActionButton("Build", GameAction.CRAFT));
		
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
			if(worldSelection != null){
				// remove it TODO but keep its properties somewhere in order to regenerates !
				if(worldSelection.uo != null){
					userObject.removeElement(worldSelection.uo);
					engine.removeEntity(worldSelection.e);
					// and add it to the player backpack ! if meet conditions (size, ...etc).
					// animate model : lerp to player and inc GUI
					addItemToBackpack(worldSelection.uo.element);
					engine.getSystem(OpenWorldGameSystem.class).backpack.add(worldSelection.uo.element);
					infoLabel.setText(OpenWorldModel.name(worldSelection.uo.element.type) + " added to your backpack.");
					actionPerformed = true;
				}
			}else{
				infoLabel.setText("Pick anything you want...");
			}
		}
		else if(action == GameAction.USE){
			if(backpackSelection != null && worldSelection != null && worldSelection.elementName != null){
				OpenWorldElement element = OpenWorldModel.useTool(backpackSelection.type, worldSelection.elementName);
				if(element != null){
					element.position.set(worldSelection.position);
					userObject.appendObject(element);
					infoLabel.setText(OpenWorldModel.name(element.type) + " just spawned!");
					actionPerformed = true;
				}else{
					infoLabel.setText("Nothing happens...");
					actionCanceled = true;
				}
			}
			else if(backpackSelection == null && (worldSelection == null || worldSelection.elementName == null)){
				infoLabel.setText("Use something from your backpack...");
			}
			// use in the world ?
			else if(backpackSelection != null){
				infoLabel.setText("Use your " + OpenWorldModel.name(backpackSelection.type) + " on something...");
			}
		}
		else if(action == GameAction.DROP){
			// TODO and if raycasted toward the world ...
			if(backpackSelection != null){
				dropFromBackpack(backpackSelection.type);
				actionPerformed = true;
				infoLabel.setText(OpenWorldModel.name(backpackSelection.type) + " was dropped from your backpack");
			}else{
				infoLabel.setText("Drop something from your backpack");
			}
		}
		else if(action == GameAction.EAT){
			if(worldSelection != null && worldSelection.elementName != null){
				// TODO eat if not too far
				infoLabel.setText("You're too far from it.");
				actionCanceled = true;
			}
			else if(backpackSelection != null){
				// TODO eatable only !
				removeFromBackpack(backpackSelection);
				actionPerformed = true;
				infoLabel.setText(OpenWorldModel.name(backpackSelection.type) + " gives you some energie!");
			}else{
				infoLabel.setText("Get something to eat...");
			}
		}
		else if(action == GameAction.CRAFT){
			if(craftSelection != null && worldSelection != null){
				// fusion
				Compound compound = new Compound();
				for(OpenWorldElement item : craftSelection){
					compound.add(item.type);
					removeFromBackpack(item);
				}
				String newType = OpenWorldModel.findFusion(compound);
				
				OpenWorldElement e;
				if(newType != null){
					// create the new object !
					e = OpenWorldModel.generateNewElement(newType);
					infoLabel.setText("Congrats! you get " + OpenWorldModel.name(e.type));
				}
				else
				{
					// create some basic objects (fail !)
					e = OpenWorldModel.generateNewGarbageElement(compound);
					infoLabel.setText("Ooops ... you get nothing valuable, check your note book.");
				}
				
				e.position.set(worldSelection.position);
				e.rotation.idt(); // TODO normal ?
				
				engine.getSystem(UserObjectSystem.class).appendObject(e);
				
				actionPerformed = true;
			}
			else if(craftSelection != null && worldSelection == null){
				infoLabel.setText("Choose where to build your ... thing.");
			}
			else if(craftingView == null){
				openCrafting();
			}
			else{
				actionCanceled = true;
				infoLabel.setText("");
			}
		}
		// default look
		else
		{
			if(worldSelection != null && worldSelection.elementName != null){
				String description = OpenWorldModel.description(worldSelection.elementName);
				infoLabel.setText(description);
				actionPerformed = true;
			}
			else if(backpackSelection != null){
				String description = OpenWorldModel.description(backpackSelection.type);
				infoLabel.setText(description);
				actionPerformed = true;
			}else{
				infoLabel.setText("Touch something to examin it...");
			}
		}
		
		
		// TODO not always clear if action not resolved
		// clear all but action
		if(actionPerformed || actionCanceled){
			craftSelection = null;
			if(action != GameAction.USE)
				backpackSelection = null; // TODO depends on context : use will reuse
			if(action == GameAction.CRAFT){
				actionButtonGroup.uncheckAll();
				action = null;
			}
		}
		// always reset world selection because this is the last action in the workflow
		worldSelection = null;
		
		// XXX
		if(action != GameAction.CRAFT){
			if(popin != null) popin.remove();
			popin = null;
			craftingView = null;
		}
		
		
		
		
		return actionPerformed;
	}

	private Table popin;
	private CraftingView craftingView;
	
	private void openCrafting() {
		craftingView = new CraftingView(getSkin(), engine, new CraftingView.Callback() {
			@Override
			public void onComplete(Array<OpenWorldElement> selection) {
				craftSelection = selection;
				resolveInteraction();
				if(popin != null) popin.remove();
				popin = null;
				craftingView = null;
			}
		});
		
		popin = new Table();
		popin.setFillParent(true);
		popin.setTouchable(Touchable.enabled);
		popin.add(craftingView);
		getStage().addActor(popin);
	}

	
}
