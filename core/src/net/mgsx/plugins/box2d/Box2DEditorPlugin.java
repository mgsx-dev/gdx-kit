package net.mgsx.plugins.box2d;

import java.lang.reflect.Field;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;

import net.mgsx.core.Editor;
import net.mgsx.core.NativeService;
import net.mgsx.core.NativeService.DialogCallback;
import net.mgsx.core.helpers.ReflectionHelper;
import net.mgsx.core.plugins.GlobalEditorPlugin;
import net.mgsx.core.tools.NoTool;
import net.mgsx.core.tools.Tool;
import net.mgsx.core.tools.ToolGroup;
import net.mgsx.core.ui.EntityEditor;
import net.mgsx.core.ui.TabPane;
import net.mgsx.plugins.box2d.model.Box2DBodyModel;
import net.mgsx.plugins.box2dold.Box2DPresets;
import net.mgsx.plugins.box2dold.Box2DPresets.Box2DPreset;
import net.mgsx.plugins.box2dold.behavior.BodyBehavior;
import net.mgsx.plugins.box2dold.behavior.PlayerBehavior;
import net.mgsx.plugins.box2dold.behavior.SimpleAI;
import net.mgsx.plugins.box2dold.model.WorldItem;
import net.mgsx.plugins.box2dold.persistence.Repository;
import net.mgsx.plugins.box2dold.tools.CreateChainTool;
import net.mgsx.plugins.box2dold.tools.CreateCircleTool;
import net.mgsx.plugins.box2dold.tools.CreateEdgeTool;
import net.mgsx.plugins.box2dold.tools.CreateLoopTool;
import net.mgsx.plugins.box2dold.tools.CreatePolygonTool;
import net.mgsx.plugins.box2dold.tools.CreateRectangleTool;
import net.mgsx.plugins.box2dold.tools.JointDistanceTool;
import net.mgsx.plugins.box2dold.tools.JointFrictionTool;
import net.mgsx.plugins.box2dold.tools.JointGearTool;
import net.mgsx.plugins.box2dold.tools.JointMotorTool;
import net.mgsx.plugins.box2dold.tools.JointMouseTool;
import net.mgsx.plugins.box2dold.tools.JointPrismaticTool;
import net.mgsx.plugins.box2dold.tools.JointPulleyTool;
import net.mgsx.plugins.box2dold.tools.JointRevoluteTool;
import net.mgsx.plugins.box2dold.tools.JointRopeTool;
import net.mgsx.plugins.box2dold.tools.JointWeldTool;
import net.mgsx.plugins.box2dold.tools.JointWheelTool;
import net.mgsx.plugins.box2dold.tools.ParticleTool;
import net.mgsx.plugins.box2dold.tools.PresetTool;

public class Box2DEditorPlugin implements GlobalEditorPlugin {

	private WorldItem worldItem; // TODO worldItem should be set in entity aspect (BodyItem)
	
	public Box2DEditorPlugin(WorldItem worldItem) {
		super();
		this.worldItem = worldItem;
	}


	@Override
	public Actor createEditor(Editor editor, Skin skin) {
		
		ToolGroup mainTools = editor.subToolGroup; // mainToolGroup;
		mainTools.clear();
		
		
		// Shape tools
		final Array<Tool> shapeTools = new Array<Tool>();
		
		shapeTools.add(new NoTool("no tool", editor));
		shapeTools.add(new CreateRectangleTool(editor, worldItem));
		shapeTools.add(new CreatePolygonTool(editor, worldItem));
		shapeTools.add(new CreateCircleTool(editor, worldItem));
		shapeTools.add(new CreateChainTool(editor, worldItem));
		shapeTools.add(new CreateLoopTool(editor, worldItem));
		shapeTools.add(new CreateEdgeTool(editor, worldItem));
		
		mainTools.tools.addAll(shapeTools);
		
		// Joint tools
		final Array<Tool> jointTools = new Array<Tool>();
		
		jointTools.add(new JointDistanceTool(editor, worldItem));
		jointTools.add(new JointFrictionTool(editor, worldItem));
		jointTools.add(new JointGearTool(editor, worldItem));
		jointTools.add(new JointMotorTool(editor, worldItem));
		jointTools.add(new JointMouseTool(editor, worldItem));
		jointTools.add(new JointPrismaticTool(editor, worldItem));
		jointTools.add(new JointPulleyTool(editor, worldItem));
		jointTools.add(new JointRevoluteTool(editor, worldItem));
		jointTools.add(new JointRopeTool(editor, worldItem));
		jointTools.add(new JointWeldTool(editor, worldItem));
		jointTools.add(new JointWheelTool(editor, worldItem));

		mainTools.tools.addAll(jointTools);
		
		// Test tools
		final Array<Tool> testTools = new Array<Tool>();
		
		testTools.add(new ParticleTool(editor, worldItem));
		testTools.add(behaviorTool(editor, "Player", PlayerBehavior.class));
		testTools.add(behaviorTool(editor, "Simple AI", SimpleAI.class));
		
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
				final Tool tool = new PresetTool(field.getName(), editor, worldItem, preset);
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
	
	private Tool behaviorTool(final Editor editor, final String name, final Class<? extends BodyBehavior> type) 
	{
		return new Tool(name, editor){
			@Override
			protected void activate() {
				Box2DBodyModel bodyItem = editor.getSelected().getComponent(Box2DBodyModel.class);
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
