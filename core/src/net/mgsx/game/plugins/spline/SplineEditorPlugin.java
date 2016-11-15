package net.mgsx.game.plugins.spline;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.SplineTest.AbstractBlenderCurve;
import net.mgsx.SplineTest.BlenderCurve;
import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.helpers.NativeService;
import net.mgsx.game.core.helpers.NativeService.DefaultCallback;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.core.tools.Tool;
import net.mgsx.game.plugins.spline.components.PathComponent;
import net.mgsx.game.plugins.spline.components.SplineDebugComponent;
import net.mgsx.game.plugins.spline.tools.BSplineTool;
import net.mgsx.game.plugins.spline.tools.BezierTool;
import net.mgsx.game.plugins.spline.tools.CatmullRomTool;

@PluginDef(components=SplineDebugComponent.class)
public class SplineEditorPlugin extends EditorPlugin
{
	@Override
	public void initialize(final EditorScreen editor) 
	{
		editor.addTool(new Tool("Import Spline", editor){
			@Override
			protected void activate() {
				NativeService.instance.openLoadDialog(new DefaultCallback() {
					@Override
					public void selected(FileHandle file) 
					{
						BlenderCurve curve = editor.loadAssetNow(file.path(), BlenderCurve.class);
						for(AbstractBlenderCurve c : curve.splines){
							PathComponent path = new PathComponent();
							path.path = c.toPath();
							editor.entityEngine.addEntity(editor.entityEngine.createEntity().add(path));
						}
						end();
					}
					@Override
					public boolean match(FileHandle file) {
						return file.extension().equals("json");
					}
					@Override
					public String description() {
						return "Spline files (json)";
					}
				});
			}
		});
		
		editor.addTool(new CatmullRomTool(editor));
		
		editor.addTool(new BSplineTool(editor));
		
		editor.addTool(new BezierTool(editor));
		
		editor.entityEngine.addSystem(new IteratingSystem(Family.all(PathComponent.class, SplineDebugComponent.class).get(), GamePipeline.RENDER_DEBUG) {
			
			@Override
			protected void processEntity(Entity entity, float deltaTime) 
			{
				
				SplineDebugComponent debug = SplineDebugComponent.components.get(entity);
				PathComponent path = PathComponent.components.get(entity);
				
				if(debug.vertices == null){
					
					int dotsPerSegment = 100;
					
					debug.vertices = new Vector3[dotsPerSegment];
					
					for(int i=0 ; i<debug.vertices.length ; i++) debug.vertices[i] = path.path.valueAt(new Vector3(), (float)i / (float)(debug.vertices.length-1));
				}

				
				editor.shapeRenderer.begin(ShapeType.Line);
				
				for(int i=1 ; i<debug.vertices.length ; i++)
					editor.shapeRenderer.line(debug.vertices[i-1], debug.vertices[i]);

				
				editor.shapeRenderer.end();
			}
		});
	}
}
