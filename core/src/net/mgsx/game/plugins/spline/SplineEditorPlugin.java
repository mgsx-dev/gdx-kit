package net.mgsx.game.plugins.spline;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.plugins.spline.components.SplineDebugComponent;
import net.mgsx.game.plugins.spline.systems.SplineDebugRender;
import net.mgsx.game.plugins.spline.tools.BSplineTool;
import net.mgsx.game.plugins.spline.tools.BezierTool;
import net.mgsx.game.plugins.spline.tools.CatmullRomTool;
import net.mgsx.game.plugins.spline.tools.ImportSplineTool;

@PluginDef(components=SplineDebugComponent.class)
public class SplineEditorPlugin extends EditorPlugin
{
	@Override
	public void initialize(final EditorScreen editor) 
	{
		editor.addTool(new ImportSplineTool(editor));
		
		editor.addTool(new CatmullRomTool(editor));
		
		editor.addTool(new BSplineTool(editor));
		
		editor.addTool(new BezierTool(editor));
		
		editor.entityEngine.addSystem(new SplineDebugRender());
	}
}
