package net.mgsx.game.plugins.box2d.editors;

import java.lang.reflect.Field;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.helpers.ReflectionHelper;
import net.mgsx.game.core.plugins.GlobalEditorPlugin;
import net.mgsx.game.core.tools.NoTool;
import net.mgsx.game.core.tools.Tool;
import net.mgsx.game.core.ui.EntityEditor;
import net.mgsx.game.core.ui.TabPane;
import net.mgsx.game.plugins.box2d.components.Box2DBodyModel;
import net.mgsx.game.plugins.box2d.components.WorldItem;
import net.mgsx.game.plugins.box2d.tools.EditBodyTool;
import net.mgsx.game.plugins.box2d.tools.ParticleTool;
import net.mgsx.game.plugins.box2d.tools.PresetTool;
import net.mgsx.game.plugins.box2d.tools.joints.JointDistanceTool;
import net.mgsx.game.plugins.box2d.tools.joints.JointFrictionTool;
import net.mgsx.game.plugins.box2d.tools.joints.JointGearTool;
import net.mgsx.game.plugins.box2d.tools.joints.JointMotorTool;
import net.mgsx.game.plugins.box2d.tools.joints.JointMouseTool;
import net.mgsx.game.plugins.box2d.tools.joints.JointPrismaticTool;
import net.mgsx.game.plugins.box2d.tools.joints.JointPulleyTool;
import net.mgsx.game.plugins.box2d.tools.joints.JointRevoluteTool;
import net.mgsx.game.plugins.box2d.tools.joints.JointRopeTool;
import net.mgsx.game.plugins.box2d.tools.joints.JointWeldTool;
import net.mgsx.game.plugins.box2d.tools.joints.JointWheelTool;
import net.mgsx.game.plugins.box2d.tools.shapes.CreateChainTool;
import net.mgsx.game.plugins.box2d.tools.shapes.CreateCircleTool;
import net.mgsx.game.plugins.box2d.tools.shapes.CreateEdgeTool;
import net.mgsx.game.plugins.box2d.tools.shapes.CreateLoopTool;
import net.mgsx.game.plugins.box2d.tools.shapes.CreatePolygonTool;
import net.mgsx.game.plugins.box2d.tools.shapes.CreateRectangleTool;
import net.mgsx.game.plugins.box2dold.Box2DPresets;
import net.mgsx.game.plugins.box2dold.Box2DPresets.Box2DPreset;
import net.mgsx.game.plugins.box2dold.behavior.BodyBehavior;
import net.mgsx.game.plugins.box2dold.behavior.PlayerBehavior;
import net.mgsx.game.plugins.box2dold.behavior.SimpleAI;

public class Box2DWorldEditorPlugin implements GlobalEditorPlugin {

	private WorldItem worldItem; // TODO worldItem should be set in entity aspect (BodyItem)
	
	public Box2DWorldEditorPlugin(WorldItem worldItem) {
		super();
		this.worldItem = worldItem;
	}


	@Override
	public Actor createEditor(EditorScreen editor, Skin skin) {
		
		final Array<Tool> allTools = new Array<Tool>();
		
		Tool noTool = new NoTool("no tool", editor);
		
		// Shape tools
		final Array<Tool> shapeTools = new Array<Tool>();
		
		shapeTools.add(noTool);
		shapeTools.add(new EditBodyTool(editor));
		shapeTools.add(new CreateRectangleTool(editor, worldItem));
		shapeTools.add(new CreatePolygonTool(editor, worldItem));
		shapeTools.add(new CreateCircleTool(editor, worldItem));
		shapeTools.add(new CreateChainTool(editor, worldItem));
		shapeTools.add(new CreateLoopTool(editor, worldItem));
		shapeTools.add(new CreateEdgeTool(editor, worldItem));
		
		allTools.addAll(shapeTools);
		
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

		allTools.addAll(jointTools);
		
		// Test tools
		final Array<Tool> testTools = new Array<Tool>();
		
		testTools.add(new ParticleTool(editor, worldItem));
		testTools.add(behaviorTool(editor, "Player", PlayerBehavior.class));
		testTools.add(behaviorTool(editor, "Simple AI", SimpleAI.class));
		
		// ...

		
		
		// remaining...
		EntityEditor worldEditor = new EntityEditor(skin);
		worldEditor.setEntity(worldItem.settings);
		
		// convert to tools
		VerticalGroup presetTable = new VerticalGroup();
		presetTable.wrap(false);
		for(Field field : Box2DPresets.class.getDeclaredFields()){
			if(field.getType() == Box2DPreset.class){
				final Box2DPreset preset = ReflectionHelper.get(null, field, Box2DPreset.class);
				final Tool tool = new PresetTool(field.getName(), editor, worldItem, preset);
				allTools.add(tool);
				presetTable.addActor(editor.createToolButton(tool));
			}
		}
		

		
		VerticalGroup shapePane = new VerticalGroup();
		for(Tool tool : shapeTools) shapePane.addActor(editor.createToolButton(tool));
		
		VerticalGroup jointPane = new VerticalGroup();
		for(Tool tool : jointTools) jointPane.addActor(editor.createToolButton(tool));

		VerticalGroup testPane = new VerticalGroup();
		for(Tool tool : testTools) testPane.addActor(editor.createToolButton(tool));

		TabPane tabs = new TabPane(skin);
		tabs.addTab("World", worldEditor);
		tabs.addTab("Shapes", shapePane);
		tabs.addTab("Joints", jointPane);
		tabs.addTab("Presets", presetTable);
		tabs.addTab("Test", testPane);

//		tabs.add(editor.createToolButton(mainTools, new NoTool("no tool", orthographicCamera))).row();
		// tabs.add(worldEditor);
		
		tabs.setTab(worldEditor);
		
		return tabs;
	}
	
	private Tool behaviorTool(final EditorScreen editor, final String name, final Class<? extends BodyBehavior> type) 
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
