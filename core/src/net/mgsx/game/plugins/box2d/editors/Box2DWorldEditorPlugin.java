package net.mgsx.game.plugins.box2d.editors;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.plugins.GlobalEditorPlugin;
import net.mgsx.game.core.tools.NoTool;
import net.mgsx.game.core.tools.Tool;
import net.mgsx.game.core.ui.EntityEditor;
import net.mgsx.game.core.ui.widgets.TabPane;
import net.mgsx.game.plugins.box2d.systems.Box2DWorldContext;
import net.mgsx.game.plugins.box2d.systems.Box2DWorldSystem;
import net.mgsx.game.plugins.box2d.tools.EditBodyTool;
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

public class Box2DWorldEditorPlugin implements GlobalEditorPlugin {

	private Box2DWorldContext worldItem;
	
	public Box2DWorldEditorPlugin(Box2DWorldContext worldItem) {
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
		
		// remaining...
		EntityEditor worldEditor = new EntityEditor(editor.entityEngine.getSystem(Box2DWorldSystem.class), true, skin);

		
		VerticalGroup shapePaneButtons = new VerticalGroup();
		for(Tool tool : shapeTools) shapePaneButtons.addActor(editor.createToolButton(tool));
		
		VerticalGroup shapePaneDefs = new VerticalGroup();
		shapePaneDefs.addActor(new EntityEditor(worldItem.settings.bodyDef, skin));
		shapePaneDefs.addActor(new EntityEditor(worldItem.settings.fixtureDef, skin));
		
		HorizontalGroup shapePane = new HorizontalGroup();
		shapePane.addActor(shapePaneButtons);
		shapePane.addActor(shapePaneDefs);
		
		
		VerticalGroup jointPane = new VerticalGroup();
		for(Tool tool : jointTools) jointPane.addActor(editor.createToolButton(tool));

		TabPane tabs = new TabPane(skin);
		tabs.addTab("World", worldEditor);
		tabs.addTab("Shapes", shapePane);
		tabs.addTab("Joints", jointPane);

		tabs.setTab(worldEditor);
		
		return tabs;
	}
	

}
