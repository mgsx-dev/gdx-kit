package net.mgsx.game.plugins.box2d;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.plugins.box2d.components.Box2DBodyModel;
import net.mgsx.game.plugins.box2d.components.Box2DJointModel;
import net.mgsx.game.plugins.box2d.editors.Box2DBodyEditorPlugin;
import net.mgsx.game.plugins.box2d.editors.Box2DJointEditorPlugin;
import net.mgsx.game.plugins.box2d.editors.Box2DWorldEditorPlugin;
import net.mgsx.game.plugins.box2d.systems.Box2DRenderDebugSystem;
import net.mgsx.game.plugins.box2d.systems.Box2DWorldContext;
import net.mgsx.game.plugins.box2d.systems.Box2DWorldSystem;
import net.mgsx.game.plugins.box2d.tools.Box2DBodySelector;
import net.mgsx.game.plugins.box2d.tools.Box2DParticleTool;

@PluginDef(dependencies={Box2DPlugin.class})
public class Box2DEditorPlugin extends EditorPlugin 
{
	@Override
	public void initialize(final EditorScreen editor) 
	{
		Box2DWorldContext context = editor.entityEngine.getSystem(Box2DWorldSystem.class).getWorldContext();
		context.editor = editor;
		
		editor.registry.addGlobalEditor("Box2D", new Box2DWorldEditorPlugin(context));
		
		editor.addTool(new Box2DParticleTool(editor));
		
		editor.addSelector(new Box2DBodySelector(editor, context));
		
		editor.entityEngine.addSystem(new Box2DRenderDebugSystem(editor));

		editor.registry.registerPlugin(Box2DBodyModel.class, new Box2DBodyEditorPlugin());
		editor.registry.registerPlugin(Box2DJointModel.class, new Box2DJointEditorPlugin());
		
	}
}
