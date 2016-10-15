package net.mgsx.plugins.box2d;

import java.lang.reflect.Field;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;

import net.mgsx.core.Editor;
import net.mgsx.core.EntityEditor;
import net.mgsx.core.NativeService;
import net.mgsx.core.NativeService.DialogCallback;
import net.mgsx.core.ReflectionHelper;
import net.mgsx.core.plugins.EditablePlugin;
import net.mgsx.core.tools.Tool;
import net.mgsx.core.tools.ToolGroup;
import net.mgsx.core.ui.TabPane;
import net.mgsx.plugins.box2d.Box2DPresets.Box2DPreset;
import net.mgsx.plugins.box2d.behavior.BodyBehavior;
import net.mgsx.plugins.box2d.behavior.PlayerBehavior;
import net.mgsx.plugins.box2d.behavior.SimpleAI;
import net.mgsx.plugins.box2d.model.BodyItem;
import net.mgsx.plugins.box2d.model.WorldItem;
import net.mgsx.plugins.box2d.persistence.Repository;
import net.mgsx.plugins.box2d.tools.CreateChainTool;
import net.mgsx.plugins.box2d.tools.CreateCircleTool;
import net.mgsx.plugins.box2d.tools.CreateEdgeTool;
import net.mgsx.plugins.box2d.tools.CreateLoopTool;
import net.mgsx.plugins.box2d.tools.CreatePolygonTool;
import net.mgsx.plugins.box2d.tools.CreateRectangleTool;
import net.mgsx.plugins.box2d.tools.JointDistanceTool;
import net.mgsx.plugins.box2d.tools.JointFrictionTool;
import net.mgsx.plugins.box2d.tools.JointGearTool;
import net.mgsx.plugins.box2d.tools.JointMotorTool;
import net.mgsx.plugins.box2d.tools.JointMouseTool;
import net.mgsx.plugins.box2d.tools.JointPrismaticTool;
import net.mgsx.plugins.box2d.tools.JointPulleyTool;
import net.mgsx.plugins.box2d.tools.JointRevoluteTool;
import net.mgsx.plugins.box2d.tools.JointRopeTool;
import net.mgsx.plugins.box2d.tools.JointWeldTool;
import net.mgsx.plugins.box2d.tools.JointWheelTool;
import net.mgsx.plugins.box2d.tools.MoveShapeTool;
import net.mgsx.plugins.box2d.tools.NoTool;
import net.mgsx.plugins.box2d.tools.ParticleTool;
import net.mgsx.plugins.box2d.tools.PresetTool;

public class Box2DEditorPlugin implements EditablePlugin {

	private Editor editor;
	private WorldItem worldItem; // TODO worldItem should be set in entity aspect (BodyItem)
	
	public Box2DEditorPlugin(Editor editor, WorldItem worldItem) {
		super();
		this.editor = editor;
		this.worldItem = worldItem;
	}


	@Override
	public Actor createEditor(Entity entity, Skin skin) {
		
		final OrthographicCamera orthographicCamera = editor.orthographicCamera;
		ToolGroup mainTools = editor.subToolGroup; // mainToolGroup;
		mainTools.clear();
		
		// Shape tools
		final Array<Tool> shapeTools = new Array<Tool>();
		
		shapeTools.add(new CreateRectangleTool(orthographicCamera, worldItem));
		shapeTools.add(new CreatePolygonTool(orthographicCamera, worldItem));
		shapeTools.add(new CreateCircleTool(orthographicCamera, worldItem));
		shapeTools.add(new CreateChainTool(orthographicCamera, worldItem));
		shapeTools.add(new CreateLoopTool(orthographicCamera, worldItem));
		shapeTools.add(new CreateEdgeTool(orthographicCamera, worldItem));
		
		mainTools.tools.addAll(shapeTools);
		
		// Joint tools
		final Array<Tool> jointTools = new Array<Tool>();
		
		jointTools.add(new JointDistanceTool(orthographicCamera, worldItem));
		jointTools.add(new JointFrictionTool(orthographicCamera, worldItem));
		jointTools.add(new JointGearTool(orthographicCamera, worldItem));
		jointTools.add(new JointMotorTool(orthographicCamera, worldItem));
		jointTools.add(new JointMouseTool(orthographicCamera, worldItem));
		jointTools.add(new JointPrismaticTool(orthographicCamera, worldItem));
		jointTools.add(new JointPulleyTool(orthographicCamera, worldItem));
		jointTools.add(new JointRevoluteTool(orthographicCamera, worldItem));
		jointTools.add(new JointRopeTool(orthographicCamera, worldItem));
		jointTools.add(new JointWeldTool(orthographicCamera, worldItem));
		jointTools.add(new JointWheelTool(orthographicCamera, worldItem));

		mainTools.tools.addAll(jointTools);
		
		// Test tools
		final Array<Tool> testTools = new Array<Tool>();
		
		testTools.add(new ParticleTool(orthographicCamera, worldItem));
		testTools.add(behaviorTool("Player", PlayerBehavior.class));
		testTools.add(behaviorTool("Simple AI", SimpleAI.class));
		
		// ...
		


		
		mainTools.setDefaultTool(null);
		mainTools.setActiveTool(null);
		
		// remaining...
		EntityEditor worldEditor = new EntityEditor(skin);
		worldEditor.setEntity(worldItem.settings);
		worldEditor.addListener(new EventListener() {
			
			@Override
			public boolean handle(Event event) {
				if(event instanceof EntityEditor.EntityEvent){
					EntityEditor.EntityEvent e = ((EntityEditor.EntityEvent) event);
					ReflectionHelper.set(e.entity, e.field, e.value);
					worldItem.world.setGravity(worldItem.settings.gravity);
					// TODO update others ...
					return true;
				}
				return false;
			}
		});
		
		TextButton btReset = new TextButton("Reset", skin);
		btReset.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// TODO reset();
			}
		});
		
		final TextButton playPauseButton = new TextButton("pause", skin);
		playPauseButton.setChecked(true);
		playPauseButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				worldItem.settings.runSimulation = playPauseButton.isChecked();
				playPauseButton.setText(worldItem.settings.runSimulation ? "pause" : "play");
			}
		});
		
		// convert to tools
		VerticalGroup presetTable = new VerticalGroup();
		presetTable.wrap(false);
		for(Field field : Box2DPresets.class.getDeclaredFields()){
			if(field.getType() == Box2DPreset.class){
				final Box2DPreset preset = ReflectionHelper.get(null, field, Box2DPreset.class);
				final Tool tool = new PresetTool(field.getName(), orthographicCamera, worldItem, preset);
				mainTools.tools.add(tool);
				presetTable.addActor(editor.createToolButton(mainTools, tool));
			}
		}
		
		TextButton btSave = new TextButton("Save", skin);
		TextButton btOpen = new TextButton("Open", skin);
		
		btSave.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				NativeService.instance.openSaveDialog(new DialogCallback() {
					@Override
					public void selected(FileHandle file) {
						Repository.save(file, worldItem);
					}
					@Override
					public void cancel() {
					}
				});
			}
		});
		btOpen.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				NativeService.instance.openSaveDialog(new DialogCallback() {
					@Override
					public void selected(FileHandle file) {
						Repository.load(file, worldItem);
						// TODO rebuild();
					}
					@Override
					public void cancel() {
					}
				});
			}
		});

		
		VerticalGroup worldPane = new VerticalGroup();
		worldPane.addActor(btReset);
		worldPane.addActor(btSave);
		worldPane.addActor(btOpen);
		worldPane.addActor(playPauseButton);
		
		VerticalGroup shapePane = new VerticalGroup();
		for(Tool tool : shapeTools) shapePane.addActor(editor.createToolButton(mainTools, tool));
		
		VerticalGroup jointPane = new VerticalGroup();
		for(Tool tool : jointTools) jointPane.addActor(editor.createToolButton(mainTools, tool));

		VerticalGroup testPane = new VerticalGroup();
		for(Tool tool : testTools) testPane.addActor(editor.createToolButton(mainTools, tool));

		TabPane tabs = new TabPane(skin);
		tabs.addTab("World", worldPane);
		tabs.addTab("Shapes", shapePane);
		tabs.addTab("Joints", jointPane);
		tabs.addTab("Presets", presetTable);
		tabs.addTab("Test", testPane);

//		tabs.add(editor.createToolButton(mainTools, new NoTool("no tool", orthographicCamera))).row();
		tabs.add(worldEditor);
		
		tabs.setTab(worldPane);
		
		return tabs;
	}
	
	private Tool behaviorTool(final String name, final Class<? extends BodyBehavior> type) 
	{
		return new Tool(name, editor.orthographicCamera){
			@Override
			protected void activate() {
				BodyItem bodyItem = editor.getSelected().getComponent(BodyItem.class);
				BodyBehavior b = null;
				if(type != null){
					b = ReflectionHelper.newInstance(type);
					b.worldItem = worldItem;
					b.bodyItem = bodyItem;
				}
				bodyItem.behavior = b;
				end();
			}
		};
	}

}