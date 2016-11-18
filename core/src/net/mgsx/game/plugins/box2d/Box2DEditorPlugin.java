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
import net.mgsx.game.plugins.box2d.tools.Box2DBodySelector;
import net.mgsx.game.plugins.box2d.tools.Box2DParticleTool;

@PluginDef(dependencies={Box2DPlugin.class})
public class Box2DEditorPlugin extends EditorPlugin 
{
	@Override
	public void initialize(final EditorScreen editor) 
	{
		Box2DPlugin.worldItem.editor = editor;
		
		editor.registry.addGlobalEditor("Box2D", new Box2DWorldEditorPlugin(Box2DPlugin.worldItem));
		
		editor.addTool(new Box2DParticleTool(editor));
		
		editor.addSelector(new Box2DBodySelector(editor, Box2DPlugin.worldItem));
		// TODO entity with model Box2D (at least a body) open all the tools ...
		
		editor.entityEngine.addSystem(new Box2DRenderDebugSystem(editor));

		// TODO type should be configured in editor (activation function !)
		editor.registry.registerPlugin(Box2DBodyModel.class, new Box2DBodyEditorPlugin());
		editor.registry.registerPlugin(Box2DJointModel.class, new Box2DJointEditorPlugin());
		
	}
}
