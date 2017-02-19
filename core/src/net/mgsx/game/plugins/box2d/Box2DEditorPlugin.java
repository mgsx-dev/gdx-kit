package net.mgsx.game.plugins.box2d;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.plugins.box2d.components.Box2DBodyModel;
import net.mgsx.game.plugins.box2d.components.Box2DJointModel;
import net.mgsx.game.plugins.box2d.editors.Box2DBodyEditorPlugin;
import net.mgsx.game.plugins.box2d.editors.Box2DJointEditorPlugin;
import net.mgsx.game.plugins.box2d.editors.Box2DWorldEditorPlugin;
import net.mgsx.game.plugins.box2d.systems.Box2DBoundaryDebugSystem;
import net.mgsx.game.plugins.box2d.systems.Box2DRayCastDebugSystem;
import net.mgsx.game.plugins.box2d.systems.Box2DRenderDebugSystem;
import net.mgsx.game.plugins.box2d.systems.Box2DWorldContext;
import net.mgsx.game.plugins.box2d.systems.Box2DWorldSystem;
import net.mgsx.game.plugins.box2d.tools.Box2DBodySelector;
import net.mgsx.game.plugins.box2d.tools.Box2DParticleTool;
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

@PluginDef(dependencies={Box2DPlugin.class})
public class Box2DEditorPlugin extends EditorPlugin 
{
	@Override
	public void initialize(final EditorScreen editor) 
	{
		Box2DWorldContext context = editor.entityEngine.getSystem(Box2DWorldSystem.class).getWorldContext();
		context.editor = editor;
		// TODO change raycast here to get the debug data
		
		editor.registry.addGlobalEditor("Box2D", new Box2DWorldEditorPlugin(context));
		
		editor.addTool(new Box2DParticleTool(editor));
		
		editor.addSelector(new Box2DBodySelector(editor, context));
		
		editor.entityEngine.addSystem(new Box2DRenderDebugSystem(editor));
		editor.entityEngine.addSystem(new Box2DBoundaryDebugSystem(editor));
		editor.entityEngine.addSystem(new Box2DRayCastDebugSystem(editor, context));

		editor.registry.registerPlugin(Box2DBodyModel.class, new Box2DBodyEditorPlugin());
		editor.registry.registerPlugin(Box2DJointModel.class, new Box2DJointEditorPlugin());
		
		
		
		// joint tools : 
		editor.addTool(new JointDistanceTool(editor, context));
		editor.addTool(new JointFrictionTool(editor, context));
		editor.addTool(new JointGearTool(editor, context));
		editor.addTool(new JointMotorTool(editor, context));
		editor.addTool(new JointMouseTool(editor, context));
		editor.addTool(new JointPrismaticTool(editor, context));
		editor.addTool(new JointPulleyTool(editor, context));
		editor.addTool(new JointRevoluteTool(editor, context));
		editor.addTool(new JointRopeTool(editor, context));
		editor.addTool(new JointWeldTool(editor, context));
		editor.addTool(new JointWheelTool(editor, context));

		// shape tools
		editor.addTool(new CreateRectangleTool(editor, context));
		editor.addTool(new CreatePolygonTool(editor, context));
		editor.addTool(new CreateCircleTool(editor, context));
		editor.addTool(new CreateChainTool(editor, context));
		editor.addTool(new CreateLoopTool(editor, context));
		editor.addTool(new CreateEdgeTool(editor, context));
	}
}
