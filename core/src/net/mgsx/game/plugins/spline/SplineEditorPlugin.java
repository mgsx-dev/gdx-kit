package net.mgsx.game.plugins.spline;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.SplineTest.BlenderCurve;
import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.helpers.NativeService;
import net.mgsx.game.core.helpers.NativeService.DialogCallback;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.core.tools.Tool;
import net.mgsx.game.plugins.spline.components.PathComponent;
import net.mgsx.game.plugins.spline.components.SplineDebugComponent;
import net.mgsx.game.plugins.spline.tools.BSplineTool;
import net.mgsx.game.plugins.spline.tools.BezierTool;
import net.mgsx.game.plugins.spline.tools.CatmullRomTool;

@PluginDef()
public class SplineEditorPlugin extends EditorPlugin
{
	@Override
	public void initialize(final EditorScreen editor) 
	{
		editor.addTool(new Tool("Import Spline", editor){
			@Override
			protected void activate() {
				NativeService.instance.openLoadDialog(new DialogCallback() {
					@Override
					public void selected(FileHandle file) {
						BlenderCurve curve = editor.loadAssetNow(file.path(), BlenderCurve.class);
						// BlenderCurve curve = Storage.load(file, BlenderCurve.class);
						PathComponent path = new PathComponent();
						path.path = curve;
						Entity entity = editor.currentEntity();
						entity.add(path);
						end();
					}
					@Override
					public void cancel() {
					}
				});
			}
		});
		
		editor.addTool(new CatmullRomTool(editor));
		
		editor.addTool(new BSplineTool(editor));
		
		editor.addTool(new BezierTool(editor));
		
		editor.entityEngine.addSystem(new IteratingSystem(Family.all(PathComponent.class, SplineDebugComponent.class).get(), GamePipeline.RENDER_DEBUG) {
			
			private Vector3 tmp = new Vector3();
			private Vector3 out = new Vector3();
			private Vector3 prev = new Vector3();
			
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

				
//				Vector2 s = Tool.pixelSize(editor.getRenderCamera()).scl(10);
//				
//				for(Vector3 point : debug.vertices){
//					editor.shapeRenderer.rect(point.x-s.x/2, point.y-s.y/2, s.x, s.y);
//				}
				
//				for(AbstractBlenderCurve path : component.path.splines){
//					if(path instanceof BlenderNURBSCurve){
//						BlenderNURBSCurve nurbs = (BlenderNURBSCurve)path;
//						
//						// XXX patch
//						if(nurbs.bs == null){
//							Vector3 [] cpy = new Vector3[nurbs.points.length+4];
//							for(int i=0 ; i<cpy.length ; i++)
//								if(i < 2) cpy[i] = nurbs.points[0];
//								else if(i > cpy.length-3) cpy[i] = nurbs.points[nurbs.points.length-1];
//								else cpy[i] = nurbs.points[i-2];
//							
//							nurbs.bs = new BSpline<Vector3>(cpy, 3, false);
//						}
//						
//						editor.shapeRenderer.setColor(Color.YELLOW);
//						
//						int steps = 30; // TODO
//						for(int i=0 ; i<steps ; i++){
//							float t = (float)(i) / (float)(steps-1);
//							// BSpline.calculate(out, t, nurbs.points, 3, false, tmp); // continuous == loop ! TODO get it from blender ?
//							nurbs.bs.valueAt(out, t);
//							if(i>0)  editor.shapeRenderer.line(prev, out);
//							prev.set(out);
//						}
//						
//						editor.shapeRenderer.setColor(Color.RED);
//						
//						Vector2 s = Tool.pixelSize(editor.getRenderCamera()).scl(10);
//						for(int i=0 ; i<nurbs.points.length ; i++){
//							Vector3 p1 = nurbs.points[i];
//							// Vector3 p2 = nurbs.points[i+1];
//							// editor.shapeRenderer.line(p1, p2);
//							editor.shapeRenderer.box(p1.x-s.x/2, p1.y-s.y/2, p1.z-s.y/2, s.x, s.y, s.y);
//						}
//					}
//					else if(path instanceof CubicBezierCurve){
//						CubicBezierCurve c = (CubicBezierCurve)path;
//						
//						editor.shapeRenderer.setColor(Color.GREEN);
//						
//						BlenderBezierPoint p = null, p2 = null;
//						for(int i=0 ; i<c.points.size-1 ; i++){
//							p = c.points.get(i);
//							p2 = c.points.get(i+1);
//							int steps = 5; // TODO
//							for(int j=0 ; j<steps ; j++){
//								float t = (float)j / (float)(steps-1);
//								Bezier.cubic(out, t, p.co, p.hr, p2.hl, p2.co, tmp);
//								if(j>0)  editor.shapeRenderer.line(prev, out);
//								prev.set(out);
//							}
//						}
//					}
//				}
				editor.shapeRenderer.end();
			}
		});
	}
}
