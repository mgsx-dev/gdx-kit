package net.mgsx.game.plugins.spline;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.BSpline;
import com.badlogic.gdx.math.Bezier;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.SplineTest.AbstractBlenderCurve;
import net.mgsx.SplineTest.BlenderBezierPoint;
import net.mgsx.SplineTest.BlenderCurve;
import net.mgsx.SplineTest.BlenderNURBSCurve;
import net.mgsx.SplineTest.CubicBezierCurve;
import net.mgsx.game.core.Editor;
import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.NativeService;
import net.mgsx.game.core.NativeService.DialogCallback;
import net.mgsx.game.core.helpers.EntityHelper;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.core.tools.Tool;

public class SplineEditorPlugin extends EditorPlugin
{
	@Override
	public void initialize(final Editor editor) 
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
		
		editor.entityEngine.addSystem(new EntityHelper.SingleComponentIteratingSystem<PathComponent>(PathComponent.class, GamePipeline.RENDER_DEBUG) {
			
			private Vector3 tmp = new Vector3();
			private Vector3 out = new Vector3();
			private Vector3 prev = new Vector3();
			
			@Override
			protected void processEntity(Entity entity, PathComponent component, float deltaTime) 
			{
				editor.shapeRenderer.begin(ShapeType.Line);
				for(AbstractBlenderCurve path : component.path.splines){
					if(path instanceof BlenderNURBSCurve){
						BlenderNURBSCurve nurbs = (BlenderNURBSCurve)path;
						
						// XXX patch
						if(nurbs.bs == null){
							Vector3 [] cpy = new Vector3[nurbs.points.length+4];
							for(int i=0 ; i<cpy.length ; i++)
								if(i < 2) cpy[i] = nurbs.points[0];
								else if(i > cpy.length-3) cpy[i] = nurbs.points[nurbs.points.length-1];
								else cpy[i] = nurbs.points[i-2];
							
							nurbs.bs = new BSpline<Vector3>(cpy, 3, false);
						}
						
						editor.shapeRenderer.setColor(Color.YELLOW);
						
						int steps = 30; // TODO
						for(int i=0 ; i<steps ; i++){
							float t = (float)(i) / (float)(steps-1);
							// BSpline.calculate(out, t, nurbs.points, 3, false, tmp); // continuous == loop ! TODO get it from blender ?
							nurbs.bs.valueAt(out, t);
							if(i>0)  editor.shapeRenderer.line(prev, out);
							prev.set(out);
						}
						
						editor.shapeRenderer.setColor(Color.RED);
						
						Vector2 s = Tool.pixelSize(editor.camera).scl(10);
						for(int i=0 ; i<nurbs.points.length ; i++){
							Vector3 p1 = nurbs.points[i];
							// Vector3 p2 = nurbs.points[i+1];
							// editor.shapeRenderer.line(p1, p2);
							editor.shapeRenderer.box(p1.x-s.x/2, p1.y-s.y/2, p1.z-s.y/2, s.x, s.y, s.y);
						}
					}
					else if(path instanceof CubicBezierCurve){
						CubicBezierCurve c = (CubicBezierCurve)path;
						
						editor.shapeRenderer.setColor(Color.GREEN);
						
						BlenderBezierPoint p = null, p2 = null;
						for(int i=0 ; i<c.points.size-1 ; i++){
							p = c.points.get(i);
							p2 = c.points.get(i+1);
							int steps = 5; // TODO
							for(int j=0 ; j<steps ; j++){
								float t = (float)j / (float)(steps-1);
								Bezier.cubic(out, t, p.co, p.hr, p2.hl, p2.co, tmp);
								if(j>0)  editor.shapeRenderer.line(prev, out);
								prev.set(out);
							}
						}
					}
				}
				editor.shapeRenderer.end();
			}
		});
	}
}
